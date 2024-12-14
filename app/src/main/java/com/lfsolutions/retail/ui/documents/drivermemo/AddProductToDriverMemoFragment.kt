package com.lfsolutions.retail.ui.documents.drivermemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentDriverMemoAddProductBinding
import com.lfsolutions.retail.model.Product
import com.lfsolutions.retail.model.memo.DriverMemoDetail
import com.lfsolutions.retail.ui.BaseActivity
import com.lfsolutions.retail.ui.forms.NewFormsBottomSheet
import com.lfsolutions.retail.ui.widgets.ProductQuantityUpdateSheet
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.Notify


class AddProductToDriverMemoFragment : Fragment() {

    private var product: Product? = null
    private lateinit var _binding: FragmentDriverMemoAddProductBinding
    private val mBinding get() = _binding
    private val args by navArgs<AddProductToDriverMemoFragmentArgs>()


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
        Main.app.getSession().userName?.let { mBinding.header.setName(it) }
        product?.productName?.let { mBinding.header.setBackText(it) }
        mBinding.header.setAccountClick((requireActivity() as BaseActivity).optionsClick)
    }


    private fun addOnClickListener() {
        mBinding.btnSub.setOnClickListener {
            if (mBinding.txtQty.text.toString().toDouble() <= 0) {
                mBinding.txtQty.text = "1"
                return@setOnClickListener
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
            )
        )
    }

    private fun updateTotal() {
        mBinding.txtTotalPrice.text = product?.cost?.let {
            Main.app.getSession().currencyCode + (mBinding.txtQty.text.toString()
                .toDouble() * it).formatDecimalSeparator()
        }
    }

}