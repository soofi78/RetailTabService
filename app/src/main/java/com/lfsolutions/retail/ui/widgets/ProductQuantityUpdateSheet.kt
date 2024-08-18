package com.lfsolutions.retail.ui.widgets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ProductQuantityUpdateSheetBinding
import com.lfsolutions.retail.util.formatDecimalSeparator
import com.videotel.digital.util.SoftKeyBoard

class ProductQuantityUpdateSheet : BottomSheetDialogFragment() {

    private lateinit var onProductDetailsChangeListener: OnProductDetailsChangeListener
    private lateinit var binding: ProductQuantityUpdateSheetBinding
    private var image: String = ""
    private var name: String = ""
    private var quantity: Double = 0.0
    private var price: Double = 0.0
    private var unitName: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = ProductQuantityUpdateSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireContext()).load(Main.app.getBaseUrl() + image).centerCrop()
            .placeholder(R.drawable.no_image).into(binding.imgProduct)
        binding.txtProductName.text = name
        binding.txtQty.setText(quantity.toString())
        binding.etPrice.setText(price.formatDecimalSeparator())
        binding.txtCurrency.text = Main.app.getSession().currencySymbol.toString()
        binding.txtUnitName.text = " /$unitName"
        binding.priceView.setOnClickListener {
            binding.etPrice.requestFocus()
            binding.etPrice.text?.length?.let { it1 -> binding.etPrice.setSelection(it1) }
            SoftKeyBoard.show(requireActivity(), binding.etPrice)
        }
        binding.etPrice.addTextChangedListener {
            if (binding.etPrice.text?.isNotEmpty() == true)
                onProductDetailsChangeListener.onPriceChanged(
                    binding.etPrice.text.toString().replace(",", "").toDouble()
                )
        }

        binding.txtQty.addTextChangedListener {
            if (binding.txtQty.text?.isNotEmpty() == true)
                onProductDetailsChangeListener.onQuantityChanged(
                    binding.txtQty.text.toString().toDouble()
                )
        }

        binding.btnSub.setOnClickListener {
            binding.txtQty.text.toString().toDouble().let { qty ->
                if (qty > 1) binding.txtQty.setText((qty - 1.0).toString())
            }
        }

        binding.btnAdd.setOnClickListener {
            binding.txtQty.text.toString().toDouble().let { qty ->
                binding.txtQty.setText((qty + 1.0).toString())
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

    fun setOnProductDetailsChangedListener(onProductDetailsChangeListener: OnProductDetailsChangeListener) {
        this.onProductDetailsChangeListener = onProductDetailsChangeListener
    }

    fun setProductDetails(
        image: String,
        name: String,
        quantity: Double,
        price: Double,
        unitName: String
    ) {
        this.image = image
        this.name = name
        this.quantity = quantity
        this.price = price
        this.unitName = unitName
    }

    companion object {
        const val TAG = "Product Quantity Update Dialog"
    }

    interface OnProductDetailsChangeListener {
        fun onQuantityChanged(quantity: Double)
        fun onPriceChanged(price: Double)
    }
}
