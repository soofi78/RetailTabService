package com.lfsolutions.retail.ui.taxinvoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityAddToCartBinding

class AddToCartActivity : AppCompatActivity() {

    private var _binding: ActivityAddToCartBinding? = null

    private val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        _binding = ActivityAddToCartBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addOnClickListener()

        addOnCheckedChangeListener()

    }

    private fun addOnCheckedChangeListener() {

        mBinding.checkboxFOC.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){

                mBinding.checkboxRent.isChecked = false

                mBinding.checkboxExchange.isChecked = false

            }

        }

        mBinding.checkboxRent.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){

                mBinding.checkboxFOC.isChecked = false

                mBinding.checkboxExchange.isChecked = false

            }

        }

        mBinding.checkboxExchange.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked){

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

        mBinding.flowBack.setOnClickListener{

            finish()

        }

        mBinding.btnSave.setOnClickListener {

            finish()

        }

    }

    companion object {

        fun getIntent(context: Context): Intent = Intent(context, AddToCartActivity::class.java)

    }

}