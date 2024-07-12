package com.lfsolutions.retail.network

import com.lfsolutions.retail.model.FormResult
import com.lfsolutions.retail.model.FormsRequest
import com.lfsolutions.retail.model.CustomerResult
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.EquipmentListRequest
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.LoginRequest
import com.lfsolutions.retail.model.LoginResponse
import com.lfsolutions.retail.util.Api
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {
    @POST(Api.Base.plus(Api.Name.AUTHENTICATE))
    fun login(@Body request: LoginRequest): Call<LoginResponse>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS))
    fun getCustomers(): Call<RetailResponse<CustomerResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS_FORMS))
    fun getCustomerForm(@Body formRequest: FormsRequest): Call<RetailResponse<FormResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_EQUIPMENT_LIST))
    fun getEquipmentType(): Call<RetailResponse<EquipmentTypeResult>>


    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_EQUIPMENT))
    fun getEquipmentList(@Body equipmentListRequest: EquipmentListRequest): Call<RetailResponse<EquipmentListResult>>
}



