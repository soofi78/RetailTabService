package com.lfsolutions.retail.ui.taxinvoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lfsolutions.retail.databinding.FragmentAddToCartBinding

class AddToCartFragment : Fragment() {

    private var _binding: FragmentAddToCartBinding? = null

    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddToCartBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        addOnClickListener()

        addOnCheckedChangeListener()

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

        mBinding.flowBack.setOnClickListener {

            it.findNavController().popBackStack()

        }

        mBinding.btnSave.setOnClickListener {

            it.findNavController().popBackStack()

        }

    }

}