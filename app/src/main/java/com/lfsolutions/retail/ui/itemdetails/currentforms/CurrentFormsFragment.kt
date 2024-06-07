package com.lfsolutions.retail.ui.itemdetails.currentforms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lfsolutions.retail.databinding.FragmentCurrentFormsBinding
import com.lfsolutions.retail.ui.itemdetails.FormAdapter
import com.lfsolutions.retail.ui.taxinvoice.ProductListActivity

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

        mAdapter = FormAdapter()

        mAdapter.setListener(object : FormAdapter.OnFormSelectListener {

            override fun onAgreementMemoSelect() {

            }

            override fun onServiceFormSelect() {


            }

            override fun onTaxInvoiceSelect() {

                startActivity(ProductListActivity.getIntent(requireContext()))

            }

        })

        mBinding.recyclerView.adapter = mAdapter

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