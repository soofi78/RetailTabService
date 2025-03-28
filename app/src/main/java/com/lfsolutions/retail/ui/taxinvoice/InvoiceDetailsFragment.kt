package com.lfsolutions.retail.ui.taxinvoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.Printer
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentInvoiceDetailsBinding
import com.lfsolutions.retail.model.CustomerSaleTransaction
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.PaymentRequest
import com.lfsolutions.retail.model.PaymentTermsResult
import com.lfsolutions.retail.model.PaymentType
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SaleTransactionRequestBody
import com.lfsolutions.retail.model.sale.SaleReceipt
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceObject
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.ui.widgets.payment.OnPaymentOptionSelected
import com.lfsolutions.retail.ui.widgets.payment.PaymentOptionsView
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Calculator
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.DocumentDownloader
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.maltaisn.calcdialog.CalcDialog
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response
import java.math.BigDecimal

class InvoiceDetailsFragment : Fragment(), CalcDialog.CalcDialogCallback {

    private lateinit var transaction: CustomerSaleTransaction
    private val paymentTypes = ArrayList<PaymentType>()
    private var invoice: SaleInvoiceObject? = null
    private lateinit var binding: FragmentInvoiceDetailsBinding
    private var id: Int? = null
    private val args by navArgs<InvoiceDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentInvoiceDetailsBinding.inflate(inflater)
            id = args.id.toInt()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaleInvoiceDetail(id)
    }


    private fun setData() {
        invoice?.salesInvoice?.invoiceNo?.let { binding.header.setBackText(it) }
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        binding.header.setOnBackClick { findNavController().popBackStack() }
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        Glide.with(binding.signature).load(invoice?.salesInvoice?.signatureUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.signature)
        binding.invoiceNo.text = invoice?.salesInvoice?.invoiceNo
        binding.invoiceDate.text = invoice?.salesInvoice?.InvoiceDateFormatted()
        binding.status.text = invoice?.salesInvoice?.StatusFormatted()
        binding.invoiceAmount.text = invoice?.salesInvoice?.InvoiceGrandTotalFromatted()
        binding.balance.text = invoice?.salesInvoice?.BalanceFormatted()
        binding.customer.text = invoice?.salesInvoice?.customerName
        val items = ArrayList<HistoryItemInterface>()
        invoice?.salesInvoiceDetail?.forEach {
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
        binding.pay.setDebouncedClickListener {
            payFor()
        }

        binding.balance.setDebouncedClickListener {
            Calculator.show(this@InvoiceDetailsFragment)
        }
        binding.balanceViewHolder.setDebouncedClickListener {
            Calculator.show(this@InvoiceDetailsFragment)
        }

        binding.print.setDebouncedClickListener {
            Printer.printInvoice(requireActivity(), invoice)
        }

        if (invoice?.salesInvoice?.type == "F") {
            binding.pay.text = getString(R.string.foc)
            binding.pay.setDebouncedClickListener {

            }
            binding.balance.text = """${Main.app.getSession().currencySymbol}0"""
        } else if (args.pay) {
            payFor()
        }

        if (invoice?.salesInvoice?.balance == 0.0) {
            binding.pay.visibility = View.GONE
        }
    }

    private fun getTransactionReference() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<CustomerSaleTransaction>>
                val index =
                    res.result?.indexOf(CustomerSaleTransaction(transactionNo = invoice?.salesInvoice?.invoiceNo))
                        ?: -1
                if (index > -1) {
                    transaction = res.result?.get(index)!!
                    return
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get transaction details")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity(), "Loading transaction details..."))
            .enque(
                Network.api()
                    ?.getSalesTransactions(SaleTransactionRequestBody(customerId = invoice?.salesInvoice?.customerId.toString()))
            ).execute()
    }

    private fun payFor() {
        if (paymentTypes.isEmpty()) NetworkCall.make().setCallback(object : OnNetworkResponse {
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
        if (::transaction.isInitialized.not()) {
            Notify.toastLong("Unable to load transaction please close and open again")
            return
        }
        val session = Main.app.getSession()
        if (transaction.appliedAmount == null || transaction.appliedAmount == 0.0) {
            transaction.appliedAmount = transaction.balanceAmount
        }
        transaction.applied = true
        val request = PaymentRequest(
            locationId = session.defaultLocationId,
            receiptDate = DateTime.getCurrentDateTime(DateTime.ServerDateTimeFormat)
                .replace(" ", "T").plus("Z"),
            customerId = invoice?.salesInvoice?.customerId,
            amount = transaction.appliedAmount,
            paymentTypeId = paymentType.value?.toInt(),
            items = arrayListOf(transaction)
        )


        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val result = response?.body() as BaseResponse<Invoice>
                if (result.success == true) {
                    Notify.toastLong("Payment Successful: ${result.result}")
                    Printer.askForPrint(requireActivity(), {
                        result.result?.id?.let { getReceiptDetail(it) }
                    }, {
                        findNavController().popBackStack()
                    }, "Print Sale Invoice", {
                        getSaleInvoiceDetail(invoice?.salesInvoice?.id, true)
                    })
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

    private fun getReceiptDetail(id: Int) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading receipt details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val receipt = (response?.body() as BaseResponse<SaleReceipt>).result
                    Printer.printReceipt(requireActivity(), receipt)
                    findNavController().popBackStack()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to print receipt")
                    findNavController().popBackStack()
                }
            }).enque(
                Network.api()?.getReceiptDetails(IdRequest(id))
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
                Network.api()?.getSaleInvoicePDF(IdRequest(id = id))
            ).execute()
    }

    private fun getSaleInvoiceDetail(invoiceId: Int?, print: Boolean = false) {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading Invoice Details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    if (print) {
                        Printer.printInvoice(
                            requireActivity(),
                            (response?.body() as BaseResponse<SaleInvoiceObject>).result
                        )
                        return
                    }
                    invoice = (response?.body() as BaseResponse<SaleInvoiceObject>).result
                    setData()
                    getTransactionReference()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get invoice detail")
                }
            }).enque(
                Network.api()?.getSaleInvoiceDetail(IdRequest(id = invoiceId))
            ).execute()
    }

    override fun onValueEntered(requestCode: Int, value: BigDecimal?) {
        transaction.appliedAmount =
            value?.toDouble()?.formatDecimalSeparator()?.replace(",", "")?.toDouble()
        transaction.applied = true
        binding.pay.text =
            """Pay ${Main.app.getSession().currencySymbol}${transaction.appliedAmount}"""
    }
}