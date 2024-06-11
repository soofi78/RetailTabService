package com.lfsolutions.retail.ui.agreementmemo

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lfsolutions.retail.databinding.FragmentAddEquipmentBinding

class AddEquipmentFragment : Fragment() {

    private var _binding: FragmentAddEquipmentBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: SerialNumberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddEquipmentBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        addOnClickListener()

        addOnCheckedChangeListener()

        mAdapter = SerialNumberAdapter()

        mBinding.recyclerView.adapter = mAdapter

        //addKeyListener()

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

            }

        }

        mBinding.btnAdd.setOnClickListener {

            mBinding.txtQty.text.toString().toInt().let { qty ->

                mBinding.txtQty.text = (qty + 1).toString()

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