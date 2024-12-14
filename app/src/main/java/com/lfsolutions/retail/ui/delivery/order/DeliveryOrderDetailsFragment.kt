package com.lfsolutions.retail.ui.delivery.order

import android.os.Bundle
import android.util.Log
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
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentDeliveryOrderDetailsBinding
import com.lfsolutions.retail.databinding.FragmentOrderDetailsBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.PrintTemplate
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.TypeRequest
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceObject
import com.lfsolutions.retail.model.sale.order.SaleOrderListItem
import com.lfsolutions.retail.model.sale.order.response.SaleOrderResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.ui.settings.printer.PrinterManager
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.DocumentDownloader
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class DeliveryOrderDetailsFragment : Fragment() {

    private var order: DeliveryOrderDTO? = null
    private lateinit var item: SaleOrderListItem
    private val args by navArgs<DeliveryOrderDetailsFragmentArgs>()
    private lateinit var binding: FragmentDeliveryOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentDeliveryOrderDetailsBinding.inflate(inflater)
            item = Gson().fromJson(args.item, SaleOrderListItem::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaleOrderDetail()
    }

    private fun setData() {
        order?.deliveryOrder?.deliveryNo?.let { binding.header.setBackText(it) }
        binding.header.setOnBackClick { findNavController().popBackStack() }
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        Glide.with(binding.signature).load(order?.deliveryOrder?.signatureUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.signature)
        binding.orderNo.text = order?.deliveryOrder?.deliveryNo.toString()
        binding.orderDate.text = order?.deliveryOrder?.DeliveryDateFormatted()
        binding.status.text = order?.deliveryOrder?.statusFormatted()
        binding.invoiceAmount.text = order?.deliveryOrder?.totalDeliveredQty.toString()
        binding.customer.text = order?.deliveryOrder?.customerName
        val items = ArrayList<HistoryItemInterface>()
        order?.deliveryOrderDetail?.forEach {
            items.add(it)
        }
        binding.invoiceItems.adapter = SaleOrderInvoiceDetailsListAdapter(items,
            object : SaleOrderInvoiceDetailsListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    Notify.toastLong(saleOrderInvoiceItem.getTitle())
                }
            })

        binding.pdf.setOnClickListener {
            getPDFLink()
        }
        binding.print.setOnClickListener {
            printDeliveryOrder()
        }

    }

    private fun printDeliveryOrder() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<PrintTemplate>>
                if ((res.result?.size ?: 0) > 0) {
                    preparePrintTemplate(res.result?.get(0))
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get order template")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity(), "Loading order template..."))
            .enque(
                Network.api()?.getReceiptTemplatePrint(TypeRequest(14))
            ).execute()
    }

    private fun preparePrintTemplate(template: PrintTemplate?) {
        var templateText = template?.template
        templateText = templateText?.replace(
            Constants.Delivery.OrderNo, order?.deliveryOrder?.deliveryNo.toString()
        )

        templateText = templateText?.replace(
            Constants.Delivery.Date, order?.deliveryOrder?.deliveryDate.toString()
        )

        templateText = templateText?.replace(
            Constants.Delivery.CustomerName, order?.deliveryOrder?.customerName.toString()
        )

        templateText = templateText?.replace(
            Constants.Order.CustomerAddress1, order?.deliveryOrder?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Order.CustomerAddress2, order?.deliveryOrder?.address2 ?: ""
        )


        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        var count = 0;
        order?.deliveryOrderDetail?.forEach {
            items += itemTemplateClean?.replace(Constants.Common.Index, it.slNo.toString())
                ?.replace(Constants.Delivery.ProductName, it.productName.toString())
                ?.replace(Constants.Delivery.Qty, it.qty.toString())
                ?.replace(Constants.Delivery.UOM, it.uom.toString())
            count += 1
            if (count < (order?.deliveryOrderDetail?.size ?: 0)) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

        templateText = templateText?.replace(
            Constants.Delivery.DeliveredQty, order?.deliveryOrder?.totalDeliveredQty.toString()
        )

        templateText = templateText?.replace(
            Constants.Delivery.DeliveryQty, order?.deliveryOrder?.totalQty.toString()
        )

        templateText = templateText?.replace(
            Constants.Delivery.DeliverySignature,
            "@@@" + order?.deliveryOrder?.signatureUrl().toString()
        )

        templateText = templateText?.replace(
            Constants.Delivery.DeliveryQR,
            Constants.QRTagStart + order?.deliveryOrder?.zatcaQRCode.toString() + Constants.QRTagEnd
        )

        templateText?.let { PrinterManager.print(it) }

        Log.d("Print", templateText.toString())
    }

    private fun getPDFLink() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
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
                        name,
                        AppSession[Constants.baseUrl] + downloadPath,
                        requireActivity()
                    )
                    Notify.toastLong("Downloading Started")
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Download Failed")
                }
            }).enque(
                Network.api()?.getDeliveryOrderPDF(IdRequest(id = item.id))
            ).execute()
    }

    private fun getSaleOrderDetail() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Order Details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    order = (response?.body() as BaseResponse<DeliveryOrderDTO>).result
                    setData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get order detail")
                }
            }).enque(
                Network.api()?.getDeliveryOrderDetails(IdRequest(id = item.id))
            ).execute()
    }
}