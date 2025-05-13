package com.lfsolutions.retail.ui.serviceform

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
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentComplaintServiceDetailsBinding
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.service.ComplaintService
import com.lfsolutions.retail.model.service.ServiceFormBody
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

class ComplaintServiceDetailsFragment : Fragment() {

    private var service: ServiceFormBody? = null
    private lateinit var item: ComplaintService
    private val args by navArgs<ComplaintServiceDetailsFragmentArgs>()
    private lateinit var binding: FragmentComplaintServiceDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentComplaintServiceDetailsBinding.inflate(inflater)
            item = Gson().fromJson(args.item, ComplaintService::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSaleInvoiceDetail()
    }

    private fun setData() {
        service?.complaintService?.csNo?.let { binding.header.setBackText(it) }
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        binding.header.setOnBackClick { findNavController().popBackStack() }
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        Glide.with(binding.signature).load(service?.complaintService?.signatureUrl()).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.signature)
        binding.serviceNo.text = service?.complaintService?.csNo
        binding.serviceDate.text = service?.complaintService?.serviceDateFormatted()
        binding.status.text = service?.complaintService?.statusFormatted()
        binding.serviceAmount.text = service?.complaintService?.totalFormatted()
        binding.customer.text = service?.complaintService?.customerName
        val items = ArrayList<HistoryItemInterface>()
        service?.complaintServiceDetails?.forEach {
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
                Network.api()?.getComplaintServicePDF(IdRequest(id = item.id))
            ).execute()
    }

    private fun getSaleInvoiceDetail() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading service details"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    service = (response?.body() as BaseResponse<ServiceFormBody>).result
                    setData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get service detail")
                }
            }).enque(
                Network.api()?.getComplaintServiceDetails(IdRequest(id = item.id))
            ).execute()
    }
}