package com.lfsolutions.retail.ui.stocktransfer.outgoing

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentOutgoingStockBinding
import com.lfsolutions.retail.databinding.FragmentSaleOrderTaxInvoiceBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.model.GetLocationResult
import com.lfsolutions.retail.model.Locations
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.DateTime
import com.videotel.digital.util.Notify
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Date

class OutGoingStockFragment : Fragment() {

    private lateinit var binding: FragmentOutgoingStockBinding
    private val locations = ArrayList<Locations>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized.not()) {
            binding = FragmentOutgoingStockBinding.inflate(inflater, container, false)
            Main.app.getOutGoingStockTransferRequestObject().locationId =
                Main.app.getSession().defaultLocationId
            binding.remarks.visibility = View.VISIBLE
        }
        return binding.root

    }

    private fun getLocations() {
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = response?.body() as BaseResponse<GetLocationResult>
                    result.result?.items?.filter { it.id != Main.app.getSession().defaultLocationId }
                        ?.let {
                            locations.addAll(it)
                        }

                    setLocationAdapter()
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Unable to load locations")
                }
            }).enque(
                Network.api()?.getLocations()
            ).execute()
    }

    private fun setLocationAdapter() {
        val adapter = locations?.let {
            ArrayAdapter(
                requireActivity(), R.layout.simple_text_item, it
            )
        }
        binding.spinnerLocations.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        setHeaderData()
        binding.date.text = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        binding.date.tag = DateTime.getCurrentDateTime(DateTime.DateFormatRetail)
        Main.app.getOutGoingStockTransferRequestObject().date =
            binding.date.text.toString() + "T00:00:00Z"
        binding.dateSelectionView.setOnClickListener {
            DateTime.showDatePicker(requireActivity(), object : DateTime.OnDatePickedCallback {
                override fun onDateSelected(year: String, month: String, day: String) {
                    binding.date.setText("$year-$month-$day")
                    Main.app.getOutGoingStockTransferRequestObject().date =
                        year + "-" + month + "-" + day + "T00:00:00Z"
                }
            })
        }

        if (locations.isEmpty()) {
            getLocations()
        } else {
            setLocationAdapter()
        }


        binding.wareHouseName.text = Main.app.getSession().defaultLocation.toString()

    }

    private fun setHeaderData() {
        binding.header.setBackText("Stock Transfer")
        Main.app.getSession().name?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            requireActivity().finish()
        }
        binding.btnCancel.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun addOnClickListener() {
        binding.btnOpenEquipmentList.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_out_going_stock_to_product_listing)
        }
        binding.btnViewOrder.setText("View Summary")
        binding.btnViewOrder.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_out_going_stock_to_summary)
        }
        binding.btnSave.setOnClickListener {
            if (Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails.size == 0) {
                Notify.toastLong("Please add products")
                return@setOnClickListener
            }
            Main.app.getOutGoingStockTransferRequestObject().remarks =
                binding.remarks.text.toString()

            if (serialBatchVerified().not()) {
                Notify.toastLong("Please add serial numbers")
                return@setOnClickListener
            }

            if (Main.app.getOutGoingStockTransferRequestObject().date == null || Main.app.getOutGoingStockTransferRequestObject().date?.isBlank() == true) {
                Notify.toastLong("Please select date")
                return@setOnClickListener
            }

            requestOutGoingStockTransfer()
        }

    }

    private fun serialBatchVerified(): Boolean {
        var verified = true
        Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails.forEach {
            val serial = it.isAsset == true || it.type.equals("S")
            val notBatched = it.productBatchList == null || it.productBatchList.size == 0
            val batchedAndQtyNotMatch =
                it.productBatchList != null && it.qty.toInt() != it.productBatchList.size
            if (serial && (notBatched || batchedAndQtyNotMatch)) {
                verified = false
            }
        }
        return verified
    }

    private fun requestOutGoingStockTransfer() {
        Main.app.getOutGoingStockTransferRequestObject().ToLocationId =
            locations[binding.spinnerLocations.selectedItemPosition].id
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity(), "Please wait..."))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val result = response?.body() as BaseResponse<String>
                    if (result.success == true) {
                        Main.app.clearOutGoingStockTransfer()
                        Notify.toastLong("Out Going Stock Transferred: " + result.result)
                        requireActivity().finish()
                    } else {
                        Notify.toastLong("Out Going Stock Transfer Failed: ${result.result}")
                    }
                }

                override fun onFailure(
                    call: Call<*>?, response: BaseResponse<*>?, tag: Any?
                ) {
                    Notify.toastLong("Out Going Stock Transfer Failed")
                }
            }).enque(
                Network.api()
                    ?.createUpdateOutGoingStockTransfer(Main.app.getOutGoingStockTransferRequestObject())
            ).execute()
    }

}