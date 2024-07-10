package com.lfsolutions.retail.network

import com.lfsolutions.retail.model.LoginRequest
import com.lfsolutions.retail.model.LoginResponse
import com.lfsolutions.retail.util.Api
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {
    @POST(Api.Base.plus(Api.Name.AUTHENTICATE))
    fun login(@Body request: LoginRequest): Call<LoginResponse>?
}



