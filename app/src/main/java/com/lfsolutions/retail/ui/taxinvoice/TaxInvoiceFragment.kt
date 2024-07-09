package com.lfsolutions.retail.ui.taxinvoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentTaxInvoiceBinding

class TaxInvoiceFragment : Fragment() {

    private var _binding: FragmentTaxInvoiceBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: CartProductAdapter

    private val mViewModel: TaxInvoiceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTaxInvoiceBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mAdapter = CartProductAdapter()

        mBinding.recyclerView.adapter = mAdapter

        mViewModel.toggleSignaturePad(false)

        addOnClickListener()

        addDataObserver()

    }

    private fun addDataObserver() {

        mViewModel.isSignOn.observe(viewLifecycleOwner) { isSignOn ->

            mBinding.signaturePad.isEnabled = isSignOn

            if (isSignOn) {

                mBinding.btnSignOn.text = getString(R.string.label_sign_off)

                mBinding.btnSignOn.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.rounded_corner_white_background
                    )
                )

            } else {

                mBinding.btnSignOn.text = getString(R.string.label_sign_on)

                mBinding.btnSignOn.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.rounded_corner_yellow_background
                    )
                )

            }

        }

    }

    private fun addOnClickListener() {

        mBinding.btnSignOn.setOnClickListener {

            mViewModel.toggleSignaturePad(!mBinding.signaturePad.isEnabled)

        }

        mBinding.btnClearSign.setOnClickListener {

            mBinding.signaturePad.clear()

        }

        mBinding.btnSave.setOnClickListener {

            it.findNavController().popBackStack(R.id.navigation_current_forms, false)

        }

        mBinding.flowBack.setOnClickListener {

            it.findNavController().popBackStack()

        }

    }

}