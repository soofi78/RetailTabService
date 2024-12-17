package com.lfsolutions.retail.ui.documents.dailysale

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentDailySaleRecordBinding
import com.lfsolutions.retail.model.PrintTemplate
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.TypeRequest
import com.lfsolutions.retail.model.UserIdDateRequestBody
import com.lfsolutions.retail.model.dailysale.DailySaleRecord
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.settings.printer.PrinterManager
import com.lfsolutions.retail.ui.widgets.SaleRecordItemView
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.DocumentDownloader
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class DailySaleRecordFragment : Fragment() {

    private var saleRecord: DailySaleRecord? = null
    private var selectedDate: String = ""
    private lateinit var binding: FragmentDailySaleRecordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentDailySaleRecordBinding.inflate(inflater, container, false)
        }
        return binding.root

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        setData()
    }


    private fun setData() {
        binding.dateHolder.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    selectedDate = "$year-$month-$day"
                    binding.date.text = selectedDate
                    fetchDailySale()
                }
            })
        }
        selectedDate = (requireActivity() as DailySaleFlowActivity).getSelectedDate().toString()
        binding.date.text = selectedDate
        binding.header.setBackText("Daily Sale Record")
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        binding.pdf.setOnClickListener {
            getPdf()
        }
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        fetchDailySale()
    }

    private fun getPdf() {
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
                Network.api()?.getDailySaleRecordPdf(
                    UserIdDateRequestBody(
                        userId = Main.app.getSession().userId,
                        date = selectedDate
                    )
                )
            ).execute()
    }


    private fun addOnClickListener() {
        binding.header.setOnBackClick {
            requireActivity().finish()
        }
    }

    private fun fetchDailySale() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    saleRecord = (response?.body() as BaseResponse<DailySaleRecord>).result
                    setDailySaleRecordData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toast("Unable to load the sale record")
                }
            }).enque(
                Network.api()?.dailySaleRecord(
                    UserIdDateRequestBody(
                        userId = Main.app.getSession().userId,
                        date = selectedDate
                    )
                )
            )
            .execute()

    }

    private fun setDailySaleRecordData() {
        binding.recordItems.removeAllViews()
        saleRecord?.dailySalesItem?.forEach { dailySalesItem ->
            val view = SaleRecordItemView(requireContext())
            view.setData(dailySalesItem)
            binding.recordItems.addView(view)
        }

        binding.netTotal.text =
            Main.app.getSession().currencySymbol + saleRecord?.netTotal.toString()
                .formatDecimalSeparator()

        binding.print.setOnClickListener {
            printDailySaleRecord()
        }
    }


    private fun printDailySaleRecord() {
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
                Network.api()?.getReceiptTemplatePrint(TypeRequest(9))
            ).execute()
    }

    private fun preparePrintTemplate(template: PrintTemplate?) {
        var templateText = template?.template


        templateText = templateText?.replace(
            Constants.Common.Date, selectedDate
        )


        val paymentTermTemplateList = templateText?.substring(
            templateText.indexOf(Constants.Payment.TermStart),
            templateText.indexOf(Constants.Payment.TermEnd) + 16
        )
        var items = ""

        saleRecord?.dailySalesItem?.forEach { term ->
            val paymentTermClean =
                paymentTermTemplateList?.replace(Constants.Payment.TermStart, "")
                    ?.replace(Constants.Payment.TermEnd, "")

            val itemsTemplate =
                paymentTermClean?.substring(
                    paymentTermClean.indexOf(Constants.Common.ItemsStart),
                    paymentTermClean.indexOf(Constants.Common.ItemsEnd) + 10
                )
            val itemsTemplateClean = itemsTemplate?.replace(Constants.Common.ItemsStart, "")
                ?.replace(Constants.Common.ItemsEnd, "")?.replace("\n", "")

            var count = 1
            var termItems = ""
            term.dailySalesInvoices.forEach {
                termItems += itemsTemplateClean?.replace(Constants.Common.Index, count.toString())
                    ?.replace(Constants.Invoice.InvoiceNo, it.invoiceNo.toString())
                    ?.replace(
                        Constants.Payment.Amount,
                        Main.app.getSession().currencySymbol + it.netTotal?.formatDecimalSeparator()
                    )
                termItems += "\n"
                count += 1
            }

            items += paymentTermClean?.replace(itemsTemplate.toString(), termItems)
                ?.replace(Constants.Payment.Term, term.paymentTerm.toString())
            items += "\n"
        }

        templateText = templateText?.replace(paymentTermTemplateList.toString(), items)

        templateText = templateText?.replace(
            Constants.Common.TotalAmount,
            Main.app.getSession().currencySymbol + saleRecord?.netTotal?.formatDecimalSeparator()
        )
        templateText?.let { PrinterManager.print(it) }
        Log.d("Print", templateText.toString())
    }
}