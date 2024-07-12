package com.lfsolutions.retail.network

import com.google.gson.stream.MalformedJsonException
import com.lfsolutions.retail.network.serializers.Error
import com.lfsolutions.retail.util.Configurations
import com.lfsolutions.retail.util.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

/**
 * Created by fah33m on 21/05/16.
 */
class NetworkCall private constructor() {
    private var taggedObject: Any? = null
    private var callback: OnNetworkResponse? = null
    private var request: Call<*>? = null
    private var loading: Loading? = null

    fun setCallback(callback: OnNetworkResponse?): NetworkCall {
        this.callback = callback
        return this
    }

    fun enque(call: Call<*>?): NetworkCall {
        this.request = call
        return this
    }

    fun setTag(tag: Any?): NetworkCall {
        this.taggedObject = tag
        return this
    }

    fun autoLoadigCancel(loading: Loading?): NetworkCall {
        this.loading = loading
        return this
    }

    fun execute(): NetworkCall {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = request?.execute()
                withContext(Dispatchers.Main) {
                    cancelLoading()
                }
                if (response?.isSuccessful == true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        callback!!.onSuccess(request, response, taggedObject)
                    }
                } else {
                    onFailure(response)
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    cancelLoading()
                }
                onException(ex)
                ex.printStackTrace()
            } finally {
                withContext(Dispatchers.Main) {
                    cancelLoading()
                }
            }
        }
        return this
    }

    private fun onException(ex: Exception) {
        val response: ErrorResponse
        response = if (isCause(SocketTimeoutException::class.java, ex)) {
            ErrorResponse(success = false, error = Error(details = "Socket timemout"))
        } else if (isCause(ConnectException::class.java, ex)) {
            ErrorResponse(success = false, error = Error(details = "Connect Error"))
        } else if (isCause(MalformedJsonException::class.java, ex)) {
            ErrorResponse(
                success = false,
                error = Error(details = "Application Error, invalid data")
            )
        } else if (ex is SSLHandshakeException || ex is SSLException) {
            ErrorResponse(success = false, error = Error(details = "SSL Handeshake error"))
        } else {
            ErrorResponse(
                success = false, error = Error(
                    details =
                    (if (Configurations.isDevelopment()) ex.message else "Something went wrong")!!
                )
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            callback!!.onFailure(request, response, taggedObject)
        }
    }

    private fun onFailure(response: Response<out Any>?) {
        if (response?.code() == ResponseCode.UN_AUTHORIZED) {
            //  MainApplication.resetApplication();
            //                    MainApplication.startErrorActivity(string(R.string.title_sessionExpired), string(R.string.desc_sessionExpired));
        } else if (response?.body() == null) {
            try {
                CoroutineScope(Dispatchers.Main).launch {
                    callback!!.onFailure(
                        request,
                        Network.parseErrorResponsee(response!!),
                        taggedObject
                    )
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    callback!!.onFailure(
                        request,
                        response?.code()?.let {
                            ErrorResponse(
                                success = false,
                                error = Error(details = "Invalid data received")
                            )
                        },
                        taggedObject
                    )
                }
            }
        }

    }

    private fun cancelLoading() {
        if (loading != null && loading!!.isVisible) loading!!.cancel()
    }

    fun isCause(
        expected: Class<out Throwable?>,
        exc: Throwable?
    ): Boolean {
        return expected.isInstance(exc) || (exc != null && isCause(expected, exc.cause)
                )
    }

    companion object {
        @Synchronized
        fun make(): NetworkCall {
            return NetworkCall()
        }
    }
}
