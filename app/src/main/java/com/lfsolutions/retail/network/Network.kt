package com.lfsolutions.retail.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lfsolutions.retail.Main
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.util.AppSession
import com.lfsolutions.retail.util.Constants
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by fah33m on 21/05/16.
 */
class Network private constructor() {
    private var networkClient: Retrofit? = null
    private var apiServices: ApiServices? = null

    fun init() {
        val gson = GsonBuilder().serializeNulls().create()
        val httpClient = OkHttpClient.Builder()
        httpClient.retryOnConnectionFailure(true)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.writeTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(loggingIntercept)
        httpClient.addInterceptor { chain ->
            if (Main.app.isLoggedIn().not()) {
                chain.proceed(chain.request())
            } else {
                val newHeaders = chain.request().headers().newBuilder()
                newHeaders.add("Authorization", "Bearer " + getSession()?.result)
                val newRequest: Request = chain.request().newBuilder()
                    .headers(newHeaders.build())
                    .build()
                Log.d("Token", newHeaders.toString())
                chain.proceed(newRequest)
            }
        }
        networkClient = Retrofit.Builder()
            .baseUrl(Main.app.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()
        apiServices = networkClient!!.create(ApiServices::class.java)
    }

    public fun getApis(): ApiServices? {
        return instance?.apiServices
    }

    private fun getSession(): UserSession? {
        return Gson().fromJson(AppSession.get(Constants.SESSION), UserSession::class.java)
    }


    private fun getWrappedTrustManagers(trustManagers: Array<TrustManager>): Array<TrustManager> {
        val originalTrustManager = trustManagers[0] as X509TrustManager
        return arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return originalTrustManager.acceptedIssuers
            }

            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
                try {
                    originalTrustManager.checkClientTrusted(certs, authType)
                } catch (ignored: CertificateException) {
                }
            }

            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
                try {
                    originalTrustManager.checkServerTrusted(certs, authType)
                } catch (ignored: CertificateException) {
                }
            }
        }
        )
    }

    val loggingIntercept: Interceptor
        get() {
            val logging = HttpLoggingInterceptor()
            return logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        }

    inner class BasicAuthInterceptor(user: String?, password: String?) : Interceptor {
        private val credentials: String = Credentials.basic(user, password)

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build()
            return chain.proceed(authenticatedRequest)
        }
    }

    companion object {
        private var instance: Network? = null

        @Synchronized
        fun getInstance(): Network? {
            if (instance == null) {
                instance = Network()
                instance?.init()
            }
            return instance
        }

        fun api(): ApiServices? {
            return getInstance()?.getApis()
        }


        fun clearInstance() {
            instance = null
        }


        val baseUrl: String
            get() = getNetworkClient()!!.baseUrl().toString()

        fun getNetworkClient(): Retrofit? {
            if (instance == null) {
                getInstance()?.init()
            }
            return getInstance()!!.networkClient
        }

        @JvmStatic
        fun parseErrorResponsee(response: retrofit2.Response<*>): ErrorResponse? {
            var errorResponsee: ErrorResponse? = null

            try {
                if (ResponseCode.isBetweenSuccessRange(response.code())) {
                    return response.body() as ErrorResponse?
                } else {
                    val errorConverter =
                        getNetworkClient()!!.responseBodyConverter<ErrorResponse>(
                            ErrorResponse::class.java, arrayOfNulls(0)
                        )
                    errorResponsee = errorConverter.convert(response.errorBody())
                    checkNotNull(errorResponsee)
                    errorResponsee.error?.code = response.code()
                    return errorResponsee
                }
            } catch (e: Exception) {
                errorResponsee = ErrorResponse()
                errorResponsee.error?.code = response.code()
                var errorString: String?
                errorString = try {
                    response.errorBody()!!.string()
                } catch (ex: Exception) {
                    "Something went wrong"
                }
                if (errorString == null || errorString.trim { it <= ' ' }
                        .equals("", ignoreCase = true)) {
                    errorString = "Something went wrong"
                }
                errorResponsee.error?.message = errorString
                return errorResponsee
            }
        }
    }
}
