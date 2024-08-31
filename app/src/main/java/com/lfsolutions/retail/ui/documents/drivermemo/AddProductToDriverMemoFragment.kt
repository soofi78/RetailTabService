package com.lfsolutions.retail.ui.documents.drivermemo

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
import com.lfsolutions.retail.databinding.FragmentDriverMemoAddProductBinding
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.EquipmentType
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.memo.AgreementMemoDetail
import com.lfsolutions.retail.model.memo.DriverMemoDetail
import com.lfsolutions.retail.model.memo.ProductBatchList
import com.lfsolutions.retail.model.sale.order.SalesOrderDetail
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.adapter.MultiSelectListAdapter
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.Loading
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog
import com.lfsolutions.retail.util.multiselect.MultiSelectDialog.SubmitCallbackListener
import com.lfsolutions.retail.util.multiselect.MultiSelectModelInterface
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response


class AddProductToDriverMemoFragment : Fragment() {

    private var product: Product? = null
    private lateinit var _binding: FragmentDriverMemoAddProductBinding
    private val mBinding get() = _binding
    private val args by navArgs<AddProductToDriverMemoFragmentArgs>()
    private var equipmentTypes: List<EquipmentType>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentDriverMemoAddProductBinding.inflate(inflater, container, false)
            product = Gson().fromJson(args.product, Product::class.java)
        }
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        setData()
        updateTotal()
        updateEquipmentTypeList()
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
        Main.app.getSession().name?.let { mBinding.header.setName(it) }
        product?.productName?.let { mBinding.header.setBackText(it) }
    }


    private fun addOnClickListener() {
        mBinding.btnSub.setOnClickListener {
            openQuantityUpdateDialog()
        }

        mBinding.btnAdd.setOnClickListener {
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
        val qty = mBinding.txtQty.text.toString().toDouble()
        val cost = product?.cost ?: 0.0
        Main.app.getDriverMemo().addEquipment(
            DriverMemoDetail(
                productId = product?.productId?.toInt() ?: 0,
                productImage = product?.imagePath,
                productName = product?.productName,
                uom = product?.unitName,
                unitId = product?.unitId,
                qty = qty,
                cost = cost,
                price = cost,
                totalCost = qty * cost,
                totalPrice = qty * cost,
                type = product?.type,
                driverType = getAgreementType()
            )
        )
    }

    private fun getAgreementType(): String {
        return equipmentTypes?.get(mBinding.spinnerEquipmentType.selectedItemPosition)?.value ?: ""
    }

    private fun updateTotal() {
        mBinding.txtTotalPrice.text = product?.cost?.let {
            Main.app.getSession().currencyCode + (mBinding.txtQty.text.toString()
                .toDouble() * it).formatDecimalSeparator()
        }
    }

}