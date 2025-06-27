package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.Printer
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentAgreementMemoDetailsBinding
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.memo.AgreementMemo
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
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

class AgreementMemoDetailsFragment : Fragment() {

    private var memo: CreateUpdateAgreementMemoRequestBody? = null
    private lateinit var item: AgreementMemo
    private val args by navArgs<AgreementMemoDetailsFragmentArgs>()
    private lateinit var binding: FragmentAgreementMemoDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentAgreementMemoDetailsBinding.inflate(inflater)
            item = Gson().fromJson(args.item, AgreementMemo::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaleInvoiceDetail()
    }

    private fun setData() {
        memo?.AgreementMemo?.AgreementNo?.let { binding.header.setBackText(it) }
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        binding.header.setOnBackClick { findNavController().popBackStack() }
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        Glide.with(binding.signature).load(memo?.AgreementMemo?.signatureUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.signature)
        binding.agreementNo.text = memo?.AgreementMemo?.AgreementNo
        binding.agreementDate.text = memo?.AgreementMemo?.agreementDateFormatted()
        binding.status.text = memo?.AgreementMemo?.Status
        binding.agreementAmount.text = memo?.AgreementMemo?.totalFormatted()
        binding.customer.text = memo?.AgreementMemo?.CustomerName
        val items = ArrayList<HistoryItemInterface>()
        memo?.AgreementMemoDetail?.forEach {
            items.add(it)
        }
        binding.invoiceItems.adapter = SaleOrderInvoiceDetailsListAdapter(items,
            object : SaleOrderInvoiceDetailsListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    Notify.toastLong(saleOrderInvoiceItem.getTitle())
                }
            })

        binding.pdf.setDebouncedClickListener {
            getPDFLink()
        }

        binding.print.setDebouncedClickListener {
            Printer.printAgreementMemo(requireActivity(), memo)
        }
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
                Network.api()?.getAgreementMemoPDF(IdRequest(id = item.Id))
            ).execute()
    }

    private fun getSaleInvoiceDetail() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading agreement memo"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    memo = (response?.body() as BaseResponse<CreateUpdateAgreementMemoRequestBody>).result
                    setData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get order details")
                }
            }).enque(
                Network.api()?.getAgreementMemoDetails(IdRequest(id = item.Id))
            ).execute()
    }
}