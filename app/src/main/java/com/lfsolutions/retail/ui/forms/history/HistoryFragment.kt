package com.lfsolutions.retail.ui.forms.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lfsolutions.retail.databinding.FragmentHistoryBinding
import com.lfsolutions.retail.ui.forms.FormAdapter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    private val mBinding get() = _binding!!

    private val mViewModel : HistoryViewModel by viewModels()

    private lateinit var mAdapter : FormAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mViewModel.getData()

        addDataObserver()

        addOnClickListener()

        mAdapter = FormAdapter(null)

        mBinding.recyclerView.adapter = mAdapter

    }

    private fun addDataObserver() {
        mViewModel.formTypeLiveData.observe(viewLifecycleOwner) { formType ->

        }
    }

    private fun addOnClickListener() {

        mBinding.flowBack.setOnClickListener {

            requireActivity().finish()

        }

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}