package com.lfsolutions.retail.ui.receipt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentReceiptDetailsBinding
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.PrintTemplate
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.TypeRequest
import com.lfsolutions.retail.model.sale.SaleReceipt
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

class ReceiptDetailsFragment : Fragment() {

    private var receipt: SaleReceipt? = null
    private lateinit var item: SaleReceipt

    private val args by navArgs<ReceiptDetailsFragmentArgs>()
    private lateinit var binding: FragmentReceiptDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentReceiptDetailsBinding.inflate(inflater)
            item = Gson().fromJson(args.item, SaleReceipt::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaleInvoiceDetail()
    }

    private fun setData() {
        binding.header.setBackText(receipt?.receiptNo ?: "Back")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            findNavController().popBackStack()
        }
        binding.receiptNo.text = receipt?.receiptNo
        binding.receiptDate.text = receipt?.getFormattedDate()
        binding.paymentMethod.text = receipt?.paymentTypeName
        binding.discount.text = receipt?.discountFormatted()
        binding.amount.text = receipt?.getAmount()
        binding.remarks.text = receipt?.remarks
        binding.customer.text = receipt?.customerName
        val items = ArrayList<HistoryItemInterface>()
        var counter = 1
        receipt?.items?.forEach {
            it.slNo = counter
            items.add(it)
            counter++
        }
        binding.receiptItems.adapter = SaleOrderInvoiceDetailsListAdapter(items,
            object : SaleOrderInvoiceDetailsListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    Notify.toastLong(saleOrderInvoiceItem.getTitle())
                }
            })

        binding.pdf.setOnClickListener {
            getPDFLink()
        }

        binding.delete.setOnClickListener {
            deleteSaleReceipt()
        }
        binding.print.setOnClickListener {
            printReceipt()
        }
    }

    private fun printReceipt() {
        NetworkCall.make().setCallback(object : OnNetworkResponse {
            override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                val res = response?.body() as RetailResponse<ArrayList<PrintTemplate>>
                if ((res.result?.size ?: 0) > 0) {
                    preparePrintTemplate(res.result?.get(4))
                }
            }

            override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                Notify.toastLong("Unable to get order template")
            }
        }).autoLoadigCancel(Loading().forApi(requireActivity(), "Loading order template..."))
            .enque(
                Network.api()?.getReceiptTemplatePrint(TypeRequest(13))
            ).execute()
    }

    private fun preparePrintTemplate(template: PrintTemplate?) {
        var templateText = template?.template
        templateText = templateText?.replace(
            Constants.Invoice.InvoiceNo, receipt?.receiptNo.toString()
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceDate, receipt?.receiptDate.toString()
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceTerm, receipt?.paymentTypeName.toString()
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceCustomerName, receipt?.customerName.toString()
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceAddress1, receipt?.address1 ?: ""
        )

        templateText = templateText?.replace(
            Constants.Invoice.InvoiceAddress2, receipt?.address2 ?: ""
        )


        val itemTemplate = templateText?.substring(
            templateText.indexOf(Constants.Common.ItemsStart),
            templateText.indexOf(Constants.Common.ItemsEnd) + 10
        )

        val itemTemplateClean = itemTemplate?.replace(Constants.Common.ItemsStart, "")
            ?.replace(Constants.Common.ItemsEnd, "")

        var items = ""
        var count = 0
        receipt?.items?.forEach {
            items += itemTemplateClean?.replace(Constants.Common.Index, it.slNo.toString())
                ?.replace(Constants.Receipt.TransactionNo, it.transactionNo.toString())
                ?.replace(Constants.Receipt.TransactionDate, it.transactionDate.toString())
                ?.replace(Constants.Receipt.TransactionAmount, it.transactionAmount.toString())
            count += 1
            if (count < (receipt?.items?.size ?: 0)) {
                items += "\n"
            }
        }

        templateText = templateText?.replace(itemTemplate.toString(), items)

//
//        templateText = templateText?.replace(
//            Constants.Order.OrderSignature,
//            "@@@" + order?.salesOrder?.signatureUrl().toString()
//        )

        templateText = templateText?.replace(
            Constants.Order.OrderQR,
            Constants.QRTagStart + receipt?.zatcaQRCode.toString() + Constants.QRTagEnd
        )

        templateText?.let { PrinterManager.print(it) }

        Log.d("Print", templateText.toString())
    }

    private fun deleteSaleReceipt() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Deleting Sale Receipt"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    Notify.toastLong("Sale Receipt Deleted")
                    findNavController().popBackStack()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to delete sale receipt")
                }
            }).enque(
                Network.api()?.deleteSaleReceipt(IdRequest(receipt?.id))
            ).execute()
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
                Network.api()?.getSaleReceiptPdf(IdRequest(id = item.id))
            ).execute()
    }

    private fun getSaleInvoiceDetail() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading receipt details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    receipt = (response?.body() as BaseResponse<SaleReceipt>).result
                    setData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get receipt details")
                }
            }).enque(
                Network.api()?.getReceiptDetails(IdRequest(id = item.id))
            ).execute()
    }
}