package com.lfsolutions.retail.ui.taxinvoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityProductListBinding

class ProductListActivity : AppCompatActivity() {

    private var _binding : ActivityProductListBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _binding = ActivityProductListBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAdapter = ProductAdapter()

        mAdapter.setListener(object : ProductAdapter.OnProductSelectListener{

            override fun onProductSelect() {

                startActivity(AddToCartActivity.getIntent(baseContext))

            }

        })

        mBinding.recyclerView.adapter = mAdapter

        addOnClickListener()

    }

    private fun addOnClickListener(){

        mBinding.btnCart.setOnClickListener {

            startActivity(TaxInvoiceActivity.getIntent(baseContext))

        }

    }

    companion object{

        fun getIntent(context: Context) : Intent = Intent(context, ProductListActivity::class.java)

    }

}