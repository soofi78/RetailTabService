package com.lfsolutions.retail.ui.agreementmemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.FragmentNewAgreementMemoBinding

class NewAgreementMemoActivity : AppCompatActivity() {

    private var _binding: FragmentNewAgreementMemoBinding? = null

    private val mBinding get() = _binding!!

    private val mViewModel: NewAgreementMemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = FragmentNewAgreementMemoBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        mViewModel.toggleSignaturePad(false)

        addOnClickListener()

        addDataObserver()

    }

    private fun addDataObserver() {

        mViewModel.isSignOn.observe(this) { isSignOn ->

            mBinding.signaturePad.isEnabled = isSignOn

            if (isSignOn) {

                mBinding.btnSignOn.text = getString(R.string.label_sign_off)

                mBinding.btnSignOn.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.rounded_corner_white_background
                    )
                )

            } else {

                mBinding.btnSignOn.text = getString(R.string.label_sign_on)

                mBinding.btnSignOn.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        this,
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

        mBinding.flowBack.setOnClickListener {

            finish()

        }

        mBinding.btnOpenEquipmentList.setOnClickListener {

            startActivity(AgreementMemoBottomNavigationActivity.getIntent(this, true))

        }

        mBinding.btnViewOrder.setOnClickListener {

            startActivity(AgreementMemoBottomNavigationActivity.getIntent(this, false))

        }

        mBinding.btnSave.setOnClickListener {

            //it.findNavController().popBackStack(R.id.navigation_current_forms, false)

            finish()

        }

    }

    companion object {

        fun getIntent(context: Context): Intent =
            Intent(context, NewAgreementMemoActivity::class.java)

    }

}