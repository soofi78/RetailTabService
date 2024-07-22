package com.lfsolutions.retail.ui.login

import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.lfsolutions.retail.R
import com.lfsolutions.retail.databinding.ActivityLoginBinding
import com.lfsolutions.retail.model.LoginRequest
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.network.BaseResponse
import com.lfsolutions.retail.network.Network
import com.lfsolutions.retail.network.NetworkCall
import com.lfsolutions.retail.network.OnNetworkResponse
import com.lfsolutions.retail.ui.HomeActivity
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import com.lfsolutions.retail.util.Loading
import com.videotel.digital.util.Notify
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity(), OnNetworkResponse {

    private var _binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(_binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _binding?.buttonSignin?.setOnClickListener {
            if (validated().not())
                return@setOnClickListener
            prepareNetworkLayer()
            login()
        }

        if (AppSession.getBoolean(Constants.IS_LOGGED_IN)) goToHome()

    }

    private fun prepareNetworkLayer() {
        AppSession.put(Constants.baseUrl, _binding?.inputServerAddress?.text.toString())
        Network.getInstance()
    }

    private fun validated(): Boolean {
        if (_binding?.inputServerAddress?.text.toString()
                .isEmpty() || URLUtil.isValidUrl(_binding?.inputServerAddress?.text.toString())
                .not()
        ) {
            _binding?.inputServerAddress?.setError("Invalid or empty Server Address")
            return false
        }

        if (_binding?.inputTenant?.text.toString().isEmpty()) {
            _binding?.inputTenant?.setError("Tenant can't be null or empty")
            return false
        }

        if (_binding?.inputUserName?.text.toString().isEmpty()) {
            _binding?.inputUserName?.setError("username can't be null or empty")
            return false
        }
        if (_binding?.inputPassword?.text.toString().isEmpty()) {
            _binding?.inputPassword?.setError("password can't be null or empty")
            return false
        }
        return true

    }

    private fun login() {
        val request = getLoginRequestBody()
        NetworkCall.make()
            .autoLoadigCancel(Loading().forApi(this))
            .setTag("LOGIN")
            .setCallback(this)
            .enque(Network.api()?.login(request))
            .execute()

    }

    private fun getLoginRequestBody(): LoginRequest {
        return LoginRequest(
            tenancyName = _binding?.inputTenant?.text.toString(),
            userNameOrEmailAddress = _binding?.inputUserName?.text.toString(),
            password = _binding?.inputPassword?.text.toString()
        )
    }

    override fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?) {
        val userSession = response?.body() as UserSession
        AppSession.put(Constants.SESSION, Gson().toJson(userSession))
        AppSession.put(Constants.IS_LOGGED_IN, true)
        Notify.toastLong("Login Success")
        goToHome()
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        this.finish()
    }

    override fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?) {
        Notify.toastLong(response?.error?.details.toString())
    }

}