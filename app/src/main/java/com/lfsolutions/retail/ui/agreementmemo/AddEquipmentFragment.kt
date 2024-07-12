package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.KeyEvent
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
import com.lfsolutions.retail.databinding.FragmentAddEquipmentBinding
import com.lfsolutions.retail.model.Equipment

class AddEquipmentFragment : Fragment() {

    private var equipment: Equipment? = null
    private lateinit var _binding: FragmentAddEquipmentBinding
    private val mBinding get() = _binding!!
    private lateinit var mAdapter: SerialNumberAdapter
    private val args by navArgs<AddEquipmentFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::_binding.isInitialized.not()) {
            _binding = FragmentAddEquipmentBinding.inflate(inflater, container, false)
            equipment = Gson().fromJson(args.equipment, Equipment::class.java)
        }
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addOnClickListener()
        addOnCheckedChangeListener()
        mAdapter = SerialNumberAdapter()
        mBinding.recyclerView.adapter = mAdapter
        setData()
        updateTotal()
        //addKeyListener()
    }

    private fun setData() {
        mBinding.txtQty.text = equipment?.qtyOnHand.toString()
        mBinding.txtProductName.text = equipment?.productName
        mBinding.txtCategory.text = equipment?.categoryName
        mBinding.txtPrice.text = Main.app.getSession().currencySymbol + equipment?.price.toString()
        Glide.with(this)
            .load(Main.app.getBaseUrl() + equipment?.imagePath)
            .centerCrop()
            .placeholder(R.drawable.no_image)
            .into(mBinding.imgProduct)
    }

    private fun addOnCheckedChangeListener() {

        mBinding.checkboxFOC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.checkboxRent.isChecked = false
                mBinding.checkboxExchange.isChecked = false
            }
        }

        mBinding.checkboxRent.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.checkboxFOC.isChecked = false
                mBinding.checkboxExchange.isChecked = false
            }
        }

        mBinding.checkboxExchange.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mBinding.checkboxRent.isChecked = false
                mBinding.checkboxFOC.isChecked = false
            }
        }
    }

    private fun addOnClickListener() {
        mBinding.btnSub.setOnClickListener {
            mBinding.txtQty.text.toString().toInt().let { qty ->
                if (qty > 1)
                    mBinding.txtQty.text = (qty - 1).toString()

                updateTotal()
            }

        }

        mBinding.btnAdd.setOnClickListener {
            mBinding.txtQty.text.toString().toInt().let { qty ->
                mBinding.txtQty.text = (qty + 1).toString()
                updateTotal()
            }
        }

        mBinding.btnFOCSub.setOnClickListener {
            mBinding.txtFOCQty.text.toString().toInt().let { qty ->
                if (qty > 1)
                    mBinding.txtFOCQty.text = (qty - 1).toString()

            }

        }

        mBinding.btnFOCAdd.setOnClickListener {
            mBinding.txtFOCQty.text.toString().toInt().let { qty ->
                mBinding.txtFOCQty.text = (qty + 1).toString()
            }

        }

        mBinding.flowBack.setOnClickListener {
            it.findNavController().popBackStack()
        }

        mBinding.btnSave.setOnClickListener {
            it.findNavController().popBackStack()
        }

    }

    private fun updateTotal() {
        mBinding.txtTotalPrice.text =
            equipment?.price?.let { (mBinding.txtQty.text.toString().toInt() * it).toString() }
    }

    private fun addKeyListener() {
        view?.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                v.findNavController().popBackStack()
            }
            return@setOnKeyListener true;
        }
    }

}