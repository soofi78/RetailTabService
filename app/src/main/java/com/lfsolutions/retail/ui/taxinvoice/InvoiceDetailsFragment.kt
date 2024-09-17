package com.lfsolutions.retail.ui.taxinvoice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentInvoiceDetailsBinding
import com.lfsolutions.retail.model.CustomerSaleTransaction
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.PaymentRequest
import com.lfsolutions.retail.model.PaymentTermsResult
import com.lfsolutions.retail.model.PaymentType
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SaleTransactionRequestBody
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListItem
import com.lfsolutions.retail.model.sale.invoice.response.SaleInvoiceResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.ui.widgets.payment.OnPaymentOptionSelected
import com.lfsolutions.retail.ui.widgets.payment.PaymentOptionsView
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.DocumentDownloader
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class InvoiceDetailsFragment : Fragment() {

    private lateinit var transaction: CustomerSaleTransaction
    private val paymentTypes = ArrayList<PaymentType>()
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
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        Glide.with(binding.signature).load(invoice?.salesInvoiceRes?.signatureUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.signature)
        binding.invoiceNo.text = invoice?.salesInvoiceRes?.invoiceNo
        binding.invoiceDate.text = invoice?.salesInvoiceRes?.InvoiceDateFormatted()
        binding.status.text = invoice?.salesInvoiceRes?.StatusFormatted()
        binding.invoiceAmount.text = invoice?.salesInvoiceRes?.InvoiceNetTotalFromatted()
        binding.balance.text = invoice?.salesInvoiceRes?.BalanceFormatted()
        binding.customer.text = invoice?.salesInvoiceRes?.customerName
        val items = ArrayList<HistoryItemInterface>()
        invoice?.salesInvoiceDetailRes?.forEach {
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
        binding.pay.setOnClickListener {
            getTransactionReference()
        }
    }

    private fun getTransactionReference() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<CustomerSaleTransaction>>
                val index =
                    res.result?.indexOf(CustomerSaleTransaction(transactionNo = invoice?.salesInvoiceRes?.invoiceNo))
                        ?: -1
                if (index > -1) {
                    res.result?.get(index)?.let { payFor(it) }
                    return
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get transaction details")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity(), "Loading transaction details..."))
            .enque(
                Network.api()
                    ?.getSalesTransactions(SaleTransactionRequestBody(customerId = invoice?.salesInvoiceRes?.customerId.toString()))
            ).execute()
    }

    private fun payFor(transaction: CustomerSaleTransaction) {
        this.transaction = transaction
        if (paymentTypes.isEmpty())
            NetworkCall.make().setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = response?.body() as BaseResponse<PaymentTermsResult>
                    result.result?.items?.let {
                        paymentTypes.addAll(it)
                    }
                    showPaymentTypes()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to load payment types")
                }
            }).autoLoadigCancel(Loading().forApi(requireActivity()))
                .enque(Network.api()?.getPaymentTerms()).execute()
        else showPaymentTypes()
    }

    private fun showPaymentTypes() {
        val modal = PaymentOptionsView()
        modal.options.addAll(paymentTypes)
        modal.setOnPaymentOptionSelected(object : OnPaymentOptionSelected {
            override fun onPaymentOptionSelected(paymentType: PaymentType) {
                onPaymentTypeSelected(paymentType)
            }
        })
        requireActivity().supportFragmentManager.let { modal.show(it, PaymentOptionsView.TAG) }
    }

    private fun onPaymentTypeSelected(paymentType: PaymentType) {
        val session = Main.app.getSession()
        if (transaction.appliedAmount == null || transaction.appliedAmount == 0.0) {
            transaction.appliedAmount = transaction.balanceAmount
        }

        transaction.applied = true
        val request = PaymentRequest(
            locationId = session.defaultLocationId,
            receiptDate = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat)
                .replace(" ", "T").plus("Z"),
            customerId = invoice?.salesInvoiceRes?.customerId,
            amount = transaction.appliedAmount,
            paymentTypeId = paymentType.value?.toInt(),
            items = arrayListOf(transaction)
        )


        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val result = response?.body() as BaseResponse<String>
                if (result.success == true) {
                    Notify.toastLong("Payment Successful: ${result.result}")
                    findNavController().popBackStack()
                } else {
                    Notify.toastLong("Payment Failed")
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong(response?.getMessage("Unable to pay"))
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity()))
            .enque(Network.api()?.createSaleReceipt(request)).execute()
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
                            "Upload\\"
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
                Network.api()?.getSaleInvoicePDF(IdRequest(id = item.id))
            ).execute()
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