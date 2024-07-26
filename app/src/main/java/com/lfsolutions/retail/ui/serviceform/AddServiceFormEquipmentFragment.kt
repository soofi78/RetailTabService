package com.lfsolutions.retail.ui.serviceform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentServiceFormAddEquipmentBinding
import com.lfsolutions.retail.model.Equipment
import com.lfsolutions.retail.model.EquipmentType
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.service.ActionType
import com.lfsolutions.retail.model.service.ActionTypeResult
import com.lfsolutions.retail.model.service.ComplaintServiceDetails
import com.lfsolutions.retail.model.service.ComplaintTypeResult
import com.lfsolutions.retail.model.service.ComplaintTypes
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.MultiSelectListAdapter
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class AddServiceFormEquipmentFragment : Fragment() {

    private var equipment: Equipment? = null
    private lateinit var _binding: FragmentServiceFormAddEquipmentBinding
    private val mBinding get() = _binding!!
    private lateinit var serialNumberAdapter: MultiSelectListAdapter
    private lateinit var complaintTypeAdapter: MultiSelectListAdapter
    private val args by navArgs<AddServiceFormEquipmentFragmentArgs>()
    private var equipmentTypes: List<EquipmentType>? = null
    private var actionType: List<ActionType>? = null
    private var serialNumbers = ArrayList<SerialNumber>()
    private var selectedSerialNumbers = ArrayList<MultiSelectModelInterface>()
    private var complaintTypes = ArrayList<ComplaintTypes>()
    private var selectedComplaintTypes = ArrayList<MultiSelectModelInterface>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentServiceFormAddEquipmentBinding.inflate(inflater, container, false)
            equipment = Gson().fromJson(args.equipment, Equipment::class.java)
        }
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        updateSerialNumbersAdapter()
        updateComplaintTypeAdapter()
        setData()
        updateTotal()
        updateEquipmentTypeList()
        updateActionTypeList()
        addSerialNumberClick()
        addComplaintTypeClick()

    }

    private fun updateActionTypeList() {
        if (actionType == null) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    actionType =
                        (response?.body() as RetailResponse<ActionTypeResult>)?.result?.items
                    setActionTypesAdapter()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get equipment list")
                }
            }).enque(Network.api()?.getActionTypes()).execute()
    }

    private fun updateSerialNumbersAdapter() {
        serialNumberAdapter = MultiSelectListAdapter(selectedSerialNumbers)
        mBinding.serialNumberRecyclerView.adapter = serialNumberAdapter
    }

    private fun updateComplaintTypeAdapter() {
        complaintTypeAdapter = MultiSelectListAdapter(selectedComplaintTypes)
        mBinding.complainTypesRecyclerView.adapter = complaintTypeAdapter
    }

    private fun addSerialNumberClick() {
        mBinding.addSerialNumber.setOnClickListener {
            if (serialNumbers == null || serialNumbers.isEmpty()) getSerialNumbersList() else showSerialNumbersList()
        }
    }

    private fun addComplaintTypeClick() {
        mBinding.addComplaintTypes.setOnClickListener {
            if (complaintTypes == null || complaintTypes.isEmpty()) getComplaintTypeList() else showComplaintTypes()
        }
    }

    private fun getSerialNumbersList() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading serial numbers"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<ArrayList<SerialNumber>>).result?.let {
                        serialNumbers = it
                    }
                    showSerialNumbersList()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get serial numbers list")
                }
            }).enque(
                Network.api()?.getSerialNumbers(
                    equipment?.productId, Main.app.getSession().defaultLocationId?.toLong()
                )
            ).execute()
    }

    private fun getComplaintTypeList() {
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity(), "Loading complaint types"))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<ComplaintTypeResult>).result?.items?.let {
                        complaintTypes = it
                    }
                    showComplaintTypes()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get complaint type list")
                }
            }).enque(
                Network.api()?.getComplaintTypes()
            ).execute()
    }

    private fun showSerialNumbersList() {
        val multiSelectDialog =
            MultiSelectDialog().title("Select serial numbers")
                .titleSize(25f).positiveText("Done").negativeText("Cancel")
                .setMinSelectionLimit(1)
                .setMaxSelectionLimit(serialNumbers.size)
                .preSelectIDsList(selectedSerialNumbers)
                .multiSelectList(serialNumbers)
                .onSubmit(object : SubmitCallbackListener {
                    override fun onSelected(
                        selectedIds: ArrayList<MultiSelectModelInterface>?,
                        selectedNames: ArrayList<String>?,
                        commonSeperatedData: String?
                    ) {
                        selectedSerialNumbers.clear()
                        selectedIds?.let { selectedSerialNumbers.addAll(it) }
                        updateSerialNumbersAdapter()
                        mBinding.txtQty.text = selectedSerialNumbers.size.toString()
                        updateTotal()
                        updateAddButtonForSerialNumber()
                    }

                    override fun onCancel() {

                    }
                })
        multiSelectDialog.show(requireActivity().supportFragmentManager, "serialNumber")
    }

    private fun updateAddButtonForSerialNumber() {
        mBinding.addSerialNumber.setBackgroundResource(if (selectedSerialNumbers.size > 0) R.drawable.round_green_background else R.drawable.round_red_background)
    }

    private fun updateButtonForComplaint() {
        mBinding.addComplaintTypes.setBackgroundResource(if (selectedSerialNumbers.size > 0) R.drawable.round_green_background else R.drawable.round_red_background)
    }

    private fun showComplaintTypes() {
        val multiSelectDialog =
            MultiSelectDialog().title("Select complaint types")
                .titleSize(25f).positiveText("Done").negativeText("Cancel")
                .setMinSelectionLimit(1)
                .setMaxSelectionLimit(complaintTypes.size)
                .preSelectIDsList(selectedComplaintTypes)
                .multiSelectList(complaintTypes)
                .onSubmit(object : SubmitCallbackListener {
                    override fun onSelected(
                        selectedIds: ArrayList<MultiSelectModelInterface>?,
                        selectedNames: ArrayList<String>?,
                        commonSeperatedData: String?
                    ) {
                        selectedComplaintTypes.clear()
                        selectedIds?.let { selectedComplaintTypes.addAll(it) }
                        updateComplaintTypeAdapter()
                        updateButtonForComplaint()
                    }

                    override fun onCancel() {

                    }
                })
        multiSelectDialog.show(requireActivity().supportFragmentManager, "complaintType")
    }

    private fun updateEquipmentTypeList() {
        if (equipmentTypes == null) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    equipmentTypes =
                        (response?.body() as RetailResponse<EquipmentTypeResult>).result?.items?.toList()
                    setEquipmentTypesAdapter()
                }

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get equipment list")
                }
            }).enque(Network.api()?.getEquipmentType()).execute()
    }

    private fun setEquipmentTypesAdapter() {
        val adapter = equipmentTypes?.let {
            ArrayAdapter(
                requireActivity(), R.layout.simple_text_item, it
            )
        }
        mBinding.spinnerEquipmentType.adapter = adapter
    }

    private fun setActionTypesAdapter() {
        val adapter = actionType?.let {
            ArrayAdapter(
                requireActivity(), R.layout.simple_text_item, it
            )
        }
        mBinding.spinnerActionType.adapter = adapter
    }


    private fun setData() {
        mBinding.txtQty.text = if (equipment?.qtyOnHand == 0) "0" else "1"
        mBinding.txtProductName.text = equipment?.productName
        mBinding.txtCategory.text = equipment?.categoryName
        mBinding.txtPrice.text =
            Main.app.getSession().currencySymbol + equipment?.cost?.formatDecimalSeparator() + "/ Pcs"
        Glide.with(this).load(Main.app.getBaseUrl() + equipment?.imagePath).centerCrop()
            .placeholder(R.drawable.no_image).into(mBinding.imgProduct)
        mBinding.serialNumberViewHolder.visibility =
            if (equipment?.isSerialEquipment() == true) View.VISIBLE else View.GONE
        Main.app.getSession().name?.let { mBinding.header.setName(it) }
        equipment?.productName?.let { mBinding.header.setBackText(it) }
    }


    private fun addOnClickListener() {
        mBinding.btnSub.setOnClickListener {
            mBinding.txtQty.text.toString().toInt().let { qty ->
                if (qty > 1) mBinding.txtQty.text = (qty - 1).toString()
                updateTotal()
            }
        }

        mBinding.btnAdd.setOnClickListener {
            mBinding.txtQty.text.toString().toInt().let { qty ->
                mBinding.txtQty.text = (qty + 1).toString()
                updateTotal()
            }
        }

        mBinding.header.setOnBackClick {
            mBinding.root.findNavController().popBackStack()
        }

        mBinding.btnSave.setOnClickListener {
            if (mBinding.txtQty.text.toString() == "0") {
                Notify.toastLong("Can't add zero quantity!")
                return@setOnClickListener
            }

            if (equipment?.isSerialEquipment() == true && selectedSerialNumbers.isEmpty()) {
                Notify.toastLong("Please add serial number")
                return@setOnClickListener
            }

            if (equipment?.isSerialEquipment() == true && mBinding.txtQty.text.toString()
                    .toInt() != selectedSerialNumbers.size
            ) {
                Notify.toastLong("Serial Number and quantity should be equal")
                return@setOnClickListener
            }

            if (selectedComplaintTypes.isEmpty()) {
                Notify.toastLong("Please select complaint types")
                return@setOnClickListener
            }

            addToCart()
            it.findNavController().popBackStack()
        }

    }

    private fun addToCart() {
        val batchList = arrayListOf<ProductBatchList>()
        if (selectedSerialNumbers != null && selectedSerialNumbers.size > 0) {
            selectedSerialNumbers.forEach { serialItem ->
                batchList.add(
                    ProductBatchList(
                        Id = serialItem.getId().toInt(), SerialNumber = serialItem.getText()
                    )
                )
            }
        }

        val complaintTypes = arrayListOf<ComplaintTypes>()
        if (selectedComplaintTypes.size > 0) {
            selectedComplaintTypes.forEach {
                complaintTypes.add(it as ComplaintTypes)
            }
        }

        val qty = mBinding.txtQty.text.toString().toInt()
        val cost = equipment?.cost ?: 0
        Main.app.getComplaintService()?.addEquipment(
            ComplaintServiceDetails(
                productId = equipment?.productId?.toInt() ?: 0,
                productName = equipment?.productName,
                unitName = equipment?.unitName,
                unitId = equipment?.unitId,
                qty = qty.toString(),
                qtyOnHand = equipment?.qtyOnHand,
                unitPrice = cost,
                price = qty * cost,
                type = equipment?.type,
                transType = getEquipmentType(),
                transTypeDisplayText = getEquipmentTypeDisplayText(),
                productBatchList = batchList,
                actionType = getComplaintServiceActionType(),
                complaintTypes = complaintTypes
            )
        )
    }

    private fun getComplaintServiceActionType(): String? {
        return actionType?.get(mBinding.spinnerActionType.selectedItemPosition)?.value ?: ""
    }

    private fun getEquipmentTypeDisplayText(): String {
        return equipmentTypes?.get(mBinding.spinnerEquipmentType.selectedItemPosition)?.displayText
            ?: ""
    }

    private fun getEquipmentType(): String {
        return equipmentTypes?.get(mBinding.spinnerEquipmentType.selectedItemPosition)?.value ?: ""
    }

    private fun updateTotal() {
        mBinding.txtTotalPrice.text =
            equipment?.cost?.let {
                Main.app.getSession().currencySymbol + (mBinding.txtQty.text.toString()
                    .toInt() * it).formatDecimalSeparator()
            }
    }
}