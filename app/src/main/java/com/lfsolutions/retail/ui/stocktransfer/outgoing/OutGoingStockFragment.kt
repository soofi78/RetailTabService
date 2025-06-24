package com.lfsolutions.retail.ui.stocktransfer.outgoing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentOutgoingStockBinding
import com.lfsolutions.retail.model.GetLocationResult
import com.lfsolutions.retail.model.LocationIdRequestObject
import com.lfsolutions.retail.model.Locations
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.outgoingstock.StockTransferProduct
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.util.DateTime
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

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
//        val adapter = locations?.let {
//            ArrayAdapter(
//                requireActivity(), R.layout.simple_text_item, it
//            )
//        }
//        binding.spinnerLocations.adapter = adapter

        binding.toWareHouseName.text = Main.app.getSession().wareHouseLocationName
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
                    binding.date.text = "$year-$month-$day"
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
        binding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
        Main.app.getSession().userName?.let { binding.header.setName(it) }
        binding.header.setOnBackClick {
            requireActivity().finish()
        }
        binding.btnCancel.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun addOnClickListener() {
        binding.btnOpenEquipmentList.setOnClickListener {
            findNavController().navigate(
                R.id.navigation_outgoing_stock_bottom_navigation,
                bundleOf("IsEquipment" to true)
            )
        }
        binding.btnViewOrder.text = "View Summary"
        binding.btnViewOrder.setOnClickListener {
            findNavController().navigate(
                R.id.navigation_outgoing_stock_bottom_navigation,
                bundleOf("IsEquipment" to false)
            )
        }
        binding.btnLoadCurrentStock.setOnClickListener {
            loadCurrentStock()
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

    private fun loadCurrentStock() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading current products"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    val res = response?.body() as BaseResponse<ArrayList<Product>>
                    Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails.clear()
                    res.result?.forEach { product ->
                        val qty = product.qtyOnHand ?: 0.0
                        val subTotal = (qty * (product.cost ?: 0.0))
                        val taxAmount =
                            subTotal * (product.getApplicableTaxRate().toDouble() / 100.0)
                        val total = (subTotal + taxAmount)
                        val customerId =
                            if (Main.app.getSession().customerId != null) Main.app.getSession().customerId.toString()
                                .toInt() else 0

                        Main.app.getOutGoingStockTransferRequestObject().addEquipment(
                            StockTransferProduct(
                                productId = product.productId ?: 0,
                                inventoryCode = product.inventoryCode,
                                productName = product.productName,
                                imagePath = product.imagePath,
                                unitId = product.unitId,
                                unitName = product.unitName,
                                qty = product.qtyOnHand ?: 0.0,
                                qtyOnHand = product.qtyOnHand?.toLong() ?: 0,
                                price = product.cost ?: 0.0,
                                cost = product.cost ?: 0.0,
                                subTotal = total,
                                customerId = customerId,
                                isAsset = product.isAsset,
                                productBatchList = product.productBatchList
                            ).apply {
                                product.applicableTaxes?.let {
                                    applicableTaxes = it
                                }
                            })
                    }
                    Main.app.getOutGoingStockTransferRequestObject().calculateAndSerialize()
                    Notify.toastLong("Stock Loaded")
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get current stock")
                }
            }).enque(
                Network.api()
                    ?.getCurrentProductStockQuantity(LocationIdRequestObject(Main.app.getSession().defaultLocationId))
            ).execute()
    }

    private fun serialBatchVerified(): Boolean {
        var verified = true
        Main.app.getOutGoingStockTransferRequestObject().stockTransferDetails.forEach {
            val serial = it.isAsset == true || it.type.equals("S")
            val notBatched = it.productBatchList == null || it.productBatchList?.size == 0
            val batchedAndQtyNotMatch =
                it.productBatchList != null && it.qty.toInt() != it.productBatchList?.size
            if (serial && (notBatched || batchedAndQtyNotMatch)) {
                verified = false
            }
        }
        return verified
    }

    private fun requestOutGoingStockTransfer() {
//        Main.app.getOutGoingStockTransferRequestObject().ToLocationId =
//            locations[binding.spinnerLocations.selectedItemPosition].id

        Main.app.getOutGoingStockTransferRequestObject().ToLocationId =
            Main.app.getSession().wareHouseLocationId

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