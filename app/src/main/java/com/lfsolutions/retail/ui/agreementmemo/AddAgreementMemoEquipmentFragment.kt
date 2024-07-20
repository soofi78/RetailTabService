package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.KeyEvent
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
import com.lfsolutions.retail.databinding.FragmentAgreementMemoAddEquipmentBinding
import com.lfsolutions.retail.model.Equipment
import com.lfsolutions.retail.model.EquipmentType
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.AgreementMemoDetail
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.service.ComplaintServiceDetails
import com.lfsolutions.retail.network.ErrorResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.MultiSelectListAdapter
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class AddAgreementMemoEquipmentFragment : Fragment() {

    private var equipment: Equipment? = null
    private lateinit var _binding: FragmentAgreementMemoAddEquipmentBinding
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: MultiSelectListAdapter
    private val args by navArgs<AddAgreementMemoEquipmentFragmentArgs>()
    private var equipmentTypes: List<EquipmentType>? = null
    private var serialNumbers = ArrayList<SerialNumber>()
    private var selectedSerialNumbers = ArrayList<MultiSelectModelInterface>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentAgreementMemoAddEquipmentBinding.inflate(inflater, container, false)
            equipment = Gson().fromJson(args.equipment, Equipment::class.java)
        }
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        updateSerialNumbersAdapter()
        setData()
        updateTotal()
        updateEquipmentTypeList()
        addSerialNumberClick()

        //addKeyListener()
    }

    private fun updateSerialNumbersAdapter() {
        mAdapter = MultiSelectListAdapter(selectedSerialNumbers)
        mBinding.recyclerView.adapter = mAdapter
    }

    private fun addSerialNumberClick() {
        mBinding.addSerialNumber.setOnClickListener {
            if (serialNumbers == null || serialNumbers.isEmpty()) getSerialNumbersList() else showSerialNumbersList()
        }
    }

    private fun getSerialNumbersList() {
        NetworkCall.make().autoLoadigCancel(Loading().forApi(requireActivity()))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    (response?.body() as RetailResponse<ArrayList<SerialNumber>>).result?.let {
                        serialNumbers = it
                    }
                    showSerialNumbersList()
                }

                override fun onFailure(call: Call<*>?, response: ErrorResponse?, tag: Any?) {
                    Notify.toastLong("Unable to get serial numbers list")
                }
            }).enque(
                Network.api()?.getSerialNumbers(
                    equipment?.productId, Main.app.getSession().defaultLocationId?.toLong()
                )
            ).execute()
    }

    private fun showSerialNumbersList() {
        val multiSelectDialog =
            MultiSelectDialog().title("Select serial numbers") //setting title for dialog
                .titleSize(25f).positiveText("Done").negativeText("Cancel")
                .setMinSelectionLimit(1) //you can set minimum checkbox selection limit (Optional)
                .setMaxSelectionLimit(
                    serialNumbers?.size ?: 0
                ) //you can set maximum checkbox selection limit (Optional)
                .preSelectIDsList(selectedSerialNumbers) //List of ids that you need to be selected
                .multiSelectList(serialNumbers) // the multi select model list with ids and name
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
                    }

                    override fun onCancel() {

                    }
                })
        multiSelectDialog.show(requireActivity().supportFragmentManager, "multiSelectDialog")
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

                override fun onFailure(call: Call<*>?, response: ErrorResponse?, tag: Any?) {
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


    private fun setData() {
        mBinding.txtQty.text = if (equipment?.qtyOnHand == 0) "0" else "1"
        mBinding.txtProductName.text = equipment?.productName
        mBinding.txtCategory.text = equipment?.categoryName
        mBinding.txtPrice.text = Main.app.getSession().currencySymbol + equipment?.cost.toString()
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
            if (mBinding.txtQty.text.toString().equals("0")) {
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

        val qty = mBinding.txtQty.text.toString().toInt()
        val cost = equipment?.cost ?: 0
        Main.app.getAgreementMemo()?.addEquipment(
            AgreementMemoDetail(
                ProductId = equipment?.productId?.toInt() ?: 0,
                ProductName = equipment?.productName,
                UnitName = equipment?.unitName,
                UnitId = equipment?.unitId,
                Qty = qty,
                QtyOnHand = equipment?.qtyOnHand,
                Cost = cost,
                TotalCost = qty * cost,
                Type = equipment?.type,
                AgreementType = getAgreementType(),
                AgreementTypeDisplayText = getAgreementTypeDisplayText(),
                ProductBatchList = batchList
            )
        )
    }

    private fun getAgreementTypeDisplayText(): String {
        return equipmentTypes?.get(mBinding.spinnerEquipmentType.selectedItemPosition)?.displayText
            ?: ""
    }

    private fun getAgreementType(): String {
        return equipmentTypes?.get(mBinding.spinnerEquipmentType.selectedItemPosition)?.value ?: ""
    }

    private fun updateTotal() {
        mBinding.txtTotalPrice.text =
            equipment?.cost?.let { (mBinding.txtQty.text.toString().toInt() * it).toString() }
    }

}