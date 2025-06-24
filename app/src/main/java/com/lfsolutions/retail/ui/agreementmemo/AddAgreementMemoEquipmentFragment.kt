package com.lfsolutions.retail.ui.agreementmemo

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
import com.lfsolutions.retail.databinding.FragmentAgreementMemoAddEquipmentBinding
import com.lfsolutions.retail.model.EquipmentType
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.AgreementMemoDetail
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.adapter.MultiSelectListAdapter
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.disableQtyFields
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.lfsolutions.retail.util.setDebouncedClickListener
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class AddAgreementMemoEquipmentFragment : Fragment() {

    private var product: Product? = null
    private lateinit var _binding: FragmentAgreementMemoAddEquipmentBinding
    private val mBinding get() = _binding
    private lateinit var mAdapter: MultiSelectListAdapter
    private val args by navArgs<AddAgreementMemoEquipmentFragmentArgs>()
    private var equipmentTypes: List<EquipmentType>? = null
    private var serialNumbers = ArrayList<SerialNumber>()
    private var selectedSerialNumbers = ArrayList<MultiSelectModelInterface>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentAgreementMemoAddEquipmentBinding.inflate(inflater, container, false)
            product = Gson().fromJson(args.product, Product::class.java)
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
    }

    private fun updateSerialNumbersAdapter() {
        mAdapter = MultiSelectListAdapter(selectedSerialNumbers)
        mBinding.recyclerView.adapter = mAdapter
        selectedSerialNumbers.disableQtyFields(
            mBinding.txtQty,
            mBinding.btnSub,
            mBinding.btnAdd
        )
    }


    private fun addSerialNumberClick() {
        mBinding.addSerialNumber.setDebouncedClickListener {
            getSerialNumbersList()
            //if (serialNumbers.isEmpty()) getSerialNumbersList() else showSerialNumbersList()
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

                override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
                    Notify.toastLong("Unable to get serial numbers list")
                }
            }).enque(
                if(getReturnStatus()){
                    Network.api()?.getSoldProductSerialNumbers(
                        productId = product?.productId,
                        locationId=Main.app.getSession().defaultLocationId?.toLong(),
                        isSold = getReturnStatus()
                    )
                }else{
                    Network.api()?.getSerialNumbers(
                        productId = product?.productId,
                        locationId=Main.app.getSession().defaultLocationId?.toLong()
                    )
                }
            ).execute()
    }

    private fun showSerialNumbersList() {
        val multiSelectDialog =
            MultiSelectDialog().title("Select serial numbers") //setting title for dialog
                .titleSize(25f).positiveText("Done").negativeText("Cancel")
                .setMinSelectionLimit(1) //you can set minimum checkbox selection limit (Optional)
                .setMaxSelectionLimit(
                    serialNumbers.size
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
                        updateAddButtonForSerialNumber()
                    }

                    override fun onCancel() {

                    }
                })
        multiSelectDialog.show(requireActivity().supportFragmentManager, "multiSelectDialog")
    }

    private fun updateAddButtonForSerialNumber() {
        mBinding.addSerialNumber.setBackgroundResource(if (selectedSerialNumbers.size > 0) R.drawable.round_green_background else R.drawable.round_red_background)
    }

    private fun updateEquipmentTypeList() {
        if (equipmentTypes == null) NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(requireActivity()))
            .setCallback(object : OnNetworkResponse {
                override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
                    equipmentTypes =
                        (response?.body() as RetailResponse<EquipmentTypeResult>).result?.items?.filter {
                            it.value.equals("F", true).not()
                                    && it.value.equals("E", true).not()
                        }
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


    private fun setData() {
        mBinding.txtQty.text = "1"
        product?.qtyOnHand?.let {
            mBinding.txtQtyAvailable.text = it.toString()
        }
        mBinding.txtProductName.text = product?.productName
        mBinding.txtCategory.text = product?.categoryName
        mBinding.txtPrice.text =
            Main.app.getSession().currencySymbol + product?.cost?.formatDecimalSeparator()
        Glide.with(this).load(Main.app.getBaseUrl() + product?.imagePath).centerCrop()
            .placeholder(R.drawable.no_image).into(mBinding.imgProduct)
        mBinding.serialNumberViewHolder.visibility =
            if (product?.isSerialEquipment() == true) View.VISIBLE else View.GONE
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        product?.productName?.let { mBinding.header.setBackText(it) }
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
    }


    private fun addOnClickListener() {
        mBinding.btnSub.setDebouncedClickListener {
            if (mBinding.txtQty.text.toString().toDouble() <= 1) {
                return@setDebouncedClickListener
            }
            mBinding.txtQty.text = mBinding.txtQty.text.toString().toDouble().minus(1).toString()
            updateTotal()
        }

        mBinding.btnAdd.setOnClickListener {
            mBinding.txtQty.text = mBinding.txtQty.text.toString().toDouble().plus(1).toString()
            updateTotal()
        }

        mBinding.txtQty.setOnClickListener {
            openQuantityUpdateDialog()
        }

        mBinding.header.setOnBackClick {
            mBinding.root.findNavController().popBackStack()
        }

        mBinding.btnSave.setOnClickListener {
            if (mBinding.txtQty.text.toString().equals("0")) {
                Notify.toastLong("Can't add zero quantity!")
                return@setOnClickListener
            }

            if (product?.isSerialEquipment() == true && selectedSerialNumbers.isEmpty()) {
                Notify.toastLong("Please add serial number")
                return@setOnClickListener
            }

            if (product?.isSerialEquipment() == true && mBinding.txtQty.text.toString()
                    .toInt() != selectedSerialNumbers.size
            ) {
                Notify.toastLong("Serial Number and quantity should be equal")
                return@setOnClickListener
            }

            addToCart()
            it.findNavController().popBackStack()
        }

    }

    private fun openQuantityUpdateDialog() {
        val modal = ProductQuantityUpdateSheet()
        modal.setProductDetails(
            product?.imagePath.toString(),
            product?.productName.toString(),
            mBinding.txtQty.text.toString().toDouble(),
            product?.cost ?: 0.0,
            product?.unitName.toString()
        )
        modal.setOnProductDetailsChangedListener(object :
            ProductQuantityUpdateSheet.OnProductDetailsChangeListener {
            override fun onQuantityChanged(quantity: Double) {
                mBinding.txtQty.text = quantity.toString()
                updateTotal()
            }

            override fun onPriceChanged(price: Double) {
                product?.cost = price
                updateTotal()
            }
        })
        requireActivity().supportFragmentManager.let { modal.show(it, NewFormsBottomSheet.TAG) }
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

        val qty = mBinding.txtQty.text.toString().toDouble()
        val cost = product?.cost ?: 0.0
        Main.app.getAgreementMemo()?.addEquipment(
            AgreementMemoDetail(
                ProductId = product?.productId?.toInt() ?: 0,
                ProductName = product?.productName,
                UnitName = product?.unitName,
                UnitId = product?.unitId,
                Qty = qty,
                QtyOnHand = product?.qtyOnHand?.toDouble(),
                Cost = cost,
                TotalCost = qty * cost,
                Type = product?.type,
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

    private fun getReturnStatus(): Boolean {
        return equipmentTypes?.get(mBinding.spinnerEquipmentType.selectedItemPosition)?.value?.equals("RT", ignoreCase = true) == true
    }



    private fun updateTotal() {
        mBinding.txtTotalPrice.text = product?.cost?.let {
            Main.app.getSession().currencyCode + (mBinding.txtQty.text.toString()
                .toDouble() * it).formatDecimalSeparator()
        }
    }

}