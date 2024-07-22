package com.lfsolutions.retail.network

import retrofit2.Call
import retrofit2.Response


/**
 * Created by Taimur on 21/05/16.
 */
interface OnNetworkResponse {
    fun onSuccess(call: Call<*>?, response: Response<*>?, tag: Any?)

    fun onFailure(call: Call<*>?, response: BaseResponse<*>?, tag: Any?)
}
