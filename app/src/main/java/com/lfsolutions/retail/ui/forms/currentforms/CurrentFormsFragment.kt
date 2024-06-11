package com.lfsolutions.retail.ui.forms.currentforms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentCurrentFormsBinding
import com.lfsolutions.retail.ui.agreementmemo.NewAgreementMemoActivity
import com.lfsolutions.retail.ui.forms.FormAdapter

class CurrentFormsFragment : Fragment() {

    private var _binding: FragmentCurrentFormsBinding? = null

    private val mBinding get() = _binding!!

    private val mViewModel: CurrentFormsViewModel by viewModels()

    private lateinit var mAdapter: FormAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCurrentFormsBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mViewModel.getData()

        addDataObserver()

        addOnClickListener()

        mAdapter = FormAdapter()

        mAdapter.setListener(object : FormAdapter.OnFormSelectListener {

            override fun onAgreementMemoSelect() {

                startActivity(NewAgreementMemoActivity.getIntent(requireContext()))

            }

            override fun onServiceFormSelect() {


            }

            override fun onTaxInvoiceSelect() {

                mBinding.root.findNavController()
                    .navigate(R.id.action_navigation_current_forms_to_navigation_product_list)

            }

        })

        mBinding.recyclerView.adapter = mAdapter

    }

    private fun addOnClickListener() {

        mBinding.flowBack.setOnClickListener {

            requireActivity().finish()

        }

    }

    private fun addDataObserver() {

        mViewModel.formTypeLiveData.observe(viewLifecycleOwner) { formType ->

            mAdapter.setData(formType)

        }

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}