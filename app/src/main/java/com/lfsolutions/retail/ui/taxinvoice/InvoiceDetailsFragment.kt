package com.lfsolutions.retail.ui.taxinvoice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentInvoiceDetailsBinding
import com.lfsolutions.retail.model.CategoryItem
import com.lfsolutions.retail.model.CategoryResult
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListItem
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceRequest
import com.lfsolutions.retail.model.sale.invoice.response.SaleInvoiceResponse
import com.lfsolutions.retail.model.sale.order.SaleOrderListItem
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
import com.lfsolutions.retail.ui.documents.history.SaleOrderInvoiceItem
import com.lfsolutions.retail.ui.saleorder.OrderDetailsFragmentArgs
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class InvoiceDetailsFragment : Fragment() {

    private var invoice: SaleInvoiceResponse? = null
    private lateinit var binding: FragmentInvoiceDetailsBinding
    private lateinit var item: SaleInvoiceListItem
    private val args by navArgs<InvoiceDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentInvoiceDetailsBinding.inflate(inflater)
            item = Gson().fromJson(args.item, SaleInvoiceListItem::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaleInvoiceDetail()
    }


    private fun setData() {
        invoice?.salesInvoiceRes?.invoiceNo?.let { binding.header.setBackText(it) }
        binding.header.setOnBackClick { findNavController().popBackStack() }
        Glide.with(binding.signature).load(invoice?.salesInvoiceRes?.signatureUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.signature)
        binding.invoiceNo.text = invoice?.salesInvoiceRes?.invoiceNo
        binding.invoiceDate.text = invoice?.salesInvoiceRes?.InvoiceDateFormatted()
        binding.status.text = invoice?.salesInvoiceRes?.StatusFormatted()
        binding.invoiceAmount.text = invoice?.salesInvoiceRes?.InvoiceNetTotalFromatted()
        binding.balance.text = invoice?.salesInvoiceRes?.BalanceFormatted()
        binding.customer.text = invoice?.salesInvoiceRes?.customerName
        val items = ArrayList<SaleOrderInvoiceItem>()
        invoice?.salesInvoiceDetailRes?.forEach {
            items.add(it)
        }
        binding.invoiceItems.adapter = SaleOrderInvoiceDetailsListAdapter(items,
            object : SaleOrderInvoiceDetailsListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: SaleOrderInvoiceItem) {
                    Notify.toastLong(saleOrderInvoiceItem.getTitle())
                }
            })
    }

    private fun getSaleInvoiceDetail() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Invoice Details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    invoice = (response?.body() as BaseResponse<SaleInvoiceResponse>).result
                    setData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get invoice detail")
                }
            }).enque(
                Network.api()?.getSaleInvoiceDetail(IdRequest(id = item.id))
            ).execute()
    }
}