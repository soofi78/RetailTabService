package com.lfsolutions.retail.ui.taxinvoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityTaxInvoiceBinding

class TaxInvoiceActivity : AppCompatActivity() {

    private var _binding : ActivityTaxInvoiceBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: CartProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _binding = ActivityTaxInvoiceBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAdapter = CartProductAdapter()

        mBinding.recyclerView.adapter = mAdapter

    }

    companion object{

        fun getIntent(context : Context) : Intent = Intent(context, TaxInvoiceActivity::class.java)

    }

}