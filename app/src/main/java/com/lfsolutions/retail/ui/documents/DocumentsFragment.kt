package com.lfsolutions.retail.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lfsolutions.retail.databinding.FragmentDocumentsBinding

class DocumentsFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null

    private val mBinding get() = _binding!!

    private val mViewModel: DocumentsViewModel by viewModels()

    private lateinit var mAdapter : DocumentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)

        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        mAdapter = DocumentAdapter()

        mBinding.recyclerViewDocuments.adapter = mAdapter

        mViewModel.loadDocuments()

        addObserver()

    }

    private fun addObserver(){

        mViewModel.documentList.observe(viewLifecycleOwner){ documentList ->

            mAdapter.setData(documentList)

        }

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }

}