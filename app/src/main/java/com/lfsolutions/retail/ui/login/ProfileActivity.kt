package com.lfsolutions.retail.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lfsolutions.retail.databinding.ActivityProfileBinding
import com.lfsolutions.retail.model.Customer
import com.lfsolutions.retail.ui.BaseActivity

class ProfileActivity : BaseActivity() {

    var customer: Customer? = null
    private var mBinding: ActivityProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
    }
}