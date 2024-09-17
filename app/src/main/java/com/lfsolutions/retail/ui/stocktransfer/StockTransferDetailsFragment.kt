package com.lfsolutions.retail.ui.stocktransfer

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
import com.lfsolutions.retail.databinding.FragmentStockTransferDetailsBinding
import com.lfsolutions.retail.model.IdRequest
import com.lfsolutions.retail.model.outgoingstock.StockTransfer
import com.lfsolutions.retail.model.outgoingstock.StockTransferDetailItem
import com.lfsolutions.retail.model.sale.invoice.SaleInvoiceListItem
import com.lfsolutions.retail.model.sale.invoice.response.SaleInvoiceResponse
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.SaleOrderInvoiceDetailsListAdapter
import com.lfsolutions.retail.ui.documents.history.HistoryItemInterface
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.DocumentDownloader
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class StockTransferDetailsFragment : Fragment() {

    private var stockTransfer: StockTransferDetailItem? = null
    private lateinit var binding: FragmentStockTransferDetailsBinding
    private lateinit var item: StockTransfer
    private val args by navArgs<StockTransferDetailsFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentStockTransferDetailsBinding.inflate(inflater)
            item = Gson().fromJson(args.item, StockTransfer::class.java)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTransferDetail()
    }


    private fun setData() {
        stockTransfer?.stockTransfer?.transferNo?.let { binding.header.setBackText(it) }
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick { findNavController().popBackStack() }
        binding.transferNo.text = stockTransfer?.stockTransfer?.transferNo
        binding.transferDate.text = stockTransfer?.stockTransfer?.transferDateFormatted()
        binding.status.text = stockTransfer?.stockTransfer?.statusFormatted()
        binding.transferQty.text = stockTransfer?.stockTransfer?.transferQty.toString()
        binding.toLocation.text = stockTransfer?.stockTransfer?.toLocationName
        binding.remarks.text = stockTransfer?.stockTransfer?.remarks
        val items = ArrayList<HistoryItemInterface>()
        stockTransfer?.stockTransferDetail?.forEach {
            items.add(it)
        }
        binding.transferItems.adapter = SaleOrderInvoiceDetailsListAdapter(items,
            object : SaleOrderInvoiceDetailsListAdapter.OnItemClickedListener {
                override fun onItemClickedListener(saleOrderInvoiceItem: HistoryItemInterface) {
                    Notify.toastLong(saleOrderInvoiceItem.getTitle())
                }
            })

        binding.pdf.setOnClickListener {
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
                Network.api()?.getStockTransferPDF(IdRequest(id = item.id))
            ).execute()
    }

    private fun getTransferDetail() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    stockTransfer =
                        (response?.body() as BaseResponse<StockTransferDetailItem>).result
                    setData()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get stock transfer details")
                }
            }).enque(
                Network.api()?.getTransferDetails(IdRequest(id = item.id))
            ).execute()
    }
}