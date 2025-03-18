package com.lfsolutions.retail.ui.saleorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.Printer
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentOrderDetailsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceObject
import com.lfsolutions.retail.model.sale.order.response.SaleOrderResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
import com.lfsolutions.retail.ui.delivery.order.DeliveryOrderDTO
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.DocumentDownloader
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class OrderDetailsFragment : Fragment() {

    private var order: SaleOrderResponse? = null
    private var orderId: Int? = null
    private val args by navArgs<OrderDetailsFragmentArgs>()
    private lateinit var binding: FragmentOrderDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentOrderDetailsBinding.inflate(inflater)
            orderId = args.orderId.toInt()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.header.setOnBackClick { findNavController().popBackStack() }
        getSaleOrderDetail()
    }

    private fun setData() {
        order?.salesOrder?.soNo?.let { binding.header.setBackText(it) }
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        Glide.with(binding.signature).load(order?.salesOrder?.signatureUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.signature)
        binding.orderNo.text = order?.salesOrder?.soNo
        binding.orderDate.text = order?.salesOrder?.InvoiceDateFormatted()
        binding.status.text = order?.salesOrder?.StatusFormatted()
        binding.poNumber.text = order?.salesOrder?.poNo
        binding.invoiceAmount.text = order?.salesOrder?.InvoiceNetTotalFromatted()
        binding.balance.text = order?.salesOrder?.BalanceFormatted()
        binding.customer.text = order?.salesOrder?.customerName
        val items = ArrayList<HistoryItemInterface>()
        order?.salesOrderDetail?.forEach {
            items.add(it)
        }
        binding.invoiceItems.adapter = SaleOrderInvoiceDetailsListAdapter(
            items, object : SaleOrderInvoiceDetailsListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    Notify.toastLong(saleOrderInvoiceItem.getTitle())
                }
            })

        binding.pdf.setDebouncedClickListener {
            getPDFLink()
        }
        binding.print.setDebouncedClickListener {
            Printer.printSaleOrder(requireActivity(), order)
        }


        if (order?.salesOrder?.status.equals("H") || order?.salesOrder?.status.equals("A") || order?.salesOrder?.status.equals(
                "PA"
            )
        ) {
            binding.deliveryOrder.visibility = View.VISIBLE
        } else {
            binding.deliveryOrder.visibility = View.GONE
        }

        if (order?.salesOrder?.status.equals("H").not() && order?.salesOrder?.status.equals("I")
                .not() && order?.salesOrder?.status.equals("R").not()
        ) {
            binding.saleInvoice.visibility = View.VISIBLE
        } else {
            binding.saleInvoice.visibility = View.GONE
        }

        if (order?.salesOrder?.isStockTransfer == false) {
            binding.saleInvoice.visibility = View.GONE
            binding.deliveryOrder.visibility = View.GONE
        }

        if (Main.app.getSession().hideDeliveryOrder) {
            binding.deliveryOrder.visibility = View.GONE
        }

        binding.saleInvoice.setDebouncedClickListener {
            convertToSaleInvoice()
        }

        binding.deliveryOrder.setDebouncedClickListener {
            convertToDeliveryOrder()
        }
    }

    private fun convertToSaleInvoice() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as BaseResponse<SaleInvoiceObject>
                Main.app.getTaxInvoice()?.salesInvoice = res.result?.salesInvoice
                Main.app.getTaxInvoice()?.salesInvoice?.deliveryOrderList = arrayListOf()
                res.result?.salesInvoiceDetail?.forEach {
                    it.taxRate = it.getApplicableTaxRate().toDouble()
                }
                Main.app.getTaxInvoice()?.salesInvoiceDetail = res.result?.salesInvoiceDetail!!
                Main.app.getTaxInvoice()?.salesInvoice?.salesOrderId = order?.salesOrder?.id
                Main.app.getTaxInvoice()?.updatePriceAndQty()
                findNavController().navigate(
                    R.id.action_navigation_order_details_to_tax_invoice, bundleOf(
                        Constants.Customer to Gson().toJson(
                            Customer(
                                id = order?.salesOrder?.customerId,
                                name = order?.salesOrder?.customerName,
                                address1 = order?.salesOrder?.address1,
                                address2 = order?.salesOrder?.address2,
                                isTaxInclusive = res.result?.salesInvoice?.isTaxInclusive
                            )
                        )
                    )
                )
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get convert to sale invoice")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity(), "Creating sale invoice...")).enque(
            Network.api()?.convertToSaleInvoice(IdRequest(order?.salesOrder?.id))
        ).execute()
    }

    private fun convertToDeliveryOrder() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as BaseResponse<DeliveryOrderDTO>
                res.result?.deliveryOrder?.let {
                    Main.app.getDeliveryOrder()?.deliveryOrder = it
                    Main.app.getDeliveryOrder()?.deliveryOrder?.status = "R"
                    Main.app.getDeliveryOrder()?.deliveryOrder?.salesOrderId = order?.salesOrder?.id
                }
                res.result?.deliveryOrderDetail?.let {
                    Main.app.getDeliveryOrder()?.deliveryOrderDetail = it
                    Main.app.getDeliveryOrder()?.deliveryOrderDetail?.forEach {
                        it.creatorUserId = Main.app.getSession().userId
                        it.productBatchList = arrayListOf()
                    }
                }

                findNavController().navigate(
                    R.id.action_navigation_order_details_to_delivery_order, bundleOf(
                        Constants.Customer to Gson().toJson(
                            Customer(
                                id = order?.salesOrder?.customerId,
                                name = order?.salesOrder?.customerName,
                                address1 = order?.salesOrder?.address1,
                                address2 = order?.salesOrder?.address2
                            )
                        )
                    )
                )
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get convert to sale invoice")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity(), "Creating sale invoice...")).enque(
            Network.api()?.convertToDeliveryOrder(IdRequest(order?.salesOrder?.id))
        ).execute()
    }

    private fun getPDFLink() {
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val downloadPath =
                        (response?.body() as BaseResponse<String>).result?.split("develop\\")
                            ?.last()
                    val name =
                        DateTime.getCurrentDateTime(DateTime.DateFormatWithDayNameMonthNameAndTime) + "-" + downloadPath?.split(
                            "Upload/"
                        )?.last().toString()
                    DocumentDownloader.download(
                        name, AppSession[Constants.baseUrl] + downloadPath, requireActivity()
                    )
                    Notify.toastLong("Downloading Started")
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Download Failed")
                }
            }).enque(
                Network.api()?.getSaleOrderPDF(IdRequest(id = orderId))
            ).execute()
    }

    private fun getSaleOrderDetail() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Order Details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    order = (response?.body() as BaseResponse<SaleOrderResponse>).result
                    setData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get order detail")
                }
            }).enque(
                Network.api()?.getSalesOrderDetail(IdRequest(id = orderId))
            ).execute()
    }
}