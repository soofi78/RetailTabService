package com.lfsolutions.retail.ui.taxinvoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentProductListBinding

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProductListBinding.inflate(layoutInflater)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = ProductAdapter()
        mAdapter.setListener(object : ProductAdapter.OnProductSelectListener {

            override fun onProductSelect() {
                mBinding.root.findNavController()
                    .navigate(R.id.action_navigation_product_list_to_navigation_add_cart)
            }
        })

        mBinding.recyclerView.adapter = mAdapter
        addOnClickListener()
    }

    private fun addOnClickListener() {

        mBinding.btnCart.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_navigation_product_list_to_navigation_tax_invoice)
        }

        mBinding.flowBack.setOnClickListener {
            it.findNavController().popBackStack()
        }

    }


}