package com.lfsolutions.retail.ui.taxinvoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityTaxInvoiceBinding

class TaxInvoiceActivity : AppCompatActivity() {

    private var _binding : ActivityTaxInvoiceBinding? = null

    private val mBinding get() = _binding!!

    private lateinit var mAdapter: CartProductAdapter

    private val mViewModel : TaxInvoiceViewModel by viewModels()

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

        mViewModel.toggleSignaturePad(false)

        addOnClickListener()

        addDataObserver()

    }

    private fun addDataObserver() {

        mViewModel.isSignOn.observe(this){ isSignOn ->

            mBinding.signaturePad.isEnabled = isSignOn

            if(isSignOn) {

                mBinding.btnSignOn.text = getString(R.string.label_sign_off)

                mBinding.btnSignOn.setBackgroundDrawable(AppCompatResources.getDrawable(baseContext,R.drawable.rounded_corner_white_background))

            }
            else {

                mBinding.btnSignOn.text = getString(R.string.label_sign_on)

                mBinding.btnSignOn.setBackgroundDrawable(AppCompatResources.getDrawable(baseContext,R.drawable.rounded_corner_yellow_background))

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

    }

    companion object{

        fun getIntent(context : Context) : Intent = Intent(context, TaxInvoiceActivity::class.java)

    }

}