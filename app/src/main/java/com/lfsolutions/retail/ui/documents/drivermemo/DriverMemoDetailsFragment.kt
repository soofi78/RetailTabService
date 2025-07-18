package com.lfsolutions.retail.ui.documents.drivermemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.databinding.FragmentDriverMemoDetailsBinding
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.memo.CreateUpdateDriverMemoRequestBody
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
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class DriverMemoDetailsFragment : Fragment() {

    private lateinit var item: CreateUpdateDriverMemoRequestBody
    private val args by navArgs<DriverMemoDetailsFragmentArgs>()
    private lateinit var binding: FragmentDriverMemoDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentDriverMemoDetailsBinding.inflate(inflater)
            item = Gson().fromJson(args.driverMemo, CreateUpdateDriverMemoRequestBody::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
    }

    private fun setData() {
        binding.header.setBackText(item.driverMemo.agreementNo.toString())
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick { findNavController().popBackStack() }
        binding.orderNo.text = item.driverMemo.agreementNo
        binding.orderDate.text = item.driverMemo.getFormattedDate()
        binding.deliveryDate.text = item.driverMemo.getFormattedDeliverDate()
        binding.qty.text = item.driverMemo.totalQty?.formatDecimalSeparator()
        binding.customer.text = item.driverMemo.customerName
        val items = ArrayList<HistoryItemInterface>()
        item.driverMemoDetail.forEach {
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
                Network.api()?.getDriverMemoPDF(IdRequest(id = item.driverMemo.id))
            ).execute()
    }
}