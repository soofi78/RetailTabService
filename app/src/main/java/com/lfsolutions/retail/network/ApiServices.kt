package com.lfsolutions.retail.network

import com.lfsolutions.retail.model.FormResult
import com.lfsolutions.retail.model.FormsRequest
import com.lfsolutions.retail.model.CustomerResult
import com.lfsolutions.retail.model.EquipmentListResult
import com.lfsolutions.retail.model.EquipmentListRequest
import com.lfsolutions.retail.model.EquipmentTypeResult
import com.lfsolutions.retail.model.RetailResponse
import com.lfsolutions.retail.model.LoginRequest
import com.lfsolutions.retail.model.SerialNumber
import com.lfsolutions.retail.model.SignatureUploadResult
import com.lfsolutions.retail.model.UserSession
import com.lfsolutions.retail.model.memo.CreateUpdateAgreementMemoRequestBody
import com.lfsolutions.retail.util.Api
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File

interface ApiServices {
    @POST(Api.Base.plus(Api.Name.AUTHENTICATE))
    fun login(@Body request: LoginRequest): Call<UserSession>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS))
    fun getCustomers(): Call<RetailResponse<CustomerResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_CUSTOMERS_FORMS))
    fun getCustomerForm(@Body formRequest: FormsRequest): Call<RetailResponse<FormResult>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_EQUIPMENT_LIST))
    fun getEquipmentType(): Call<RetailResponse<EquipmentTypeResult>>


    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_EQUIPMENT))
    fun getEquipmentList(@Body equipmentListRequest: EquipmentListRequest): Call<RetailResponse<EquipmentListResult>>

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.GET_SERIAL_NUMBERS))
    fun getSerialNumbers(
        @Query("productId") productId: Long?,
        @Query("locationId") locationId: Long?,
        @Query("isSold") isSold: Boolean = false
    ): Call<RetailResponse<ArrayList<SerialNumber>>>?

    @POST(Api.Base.plus(Api.ServicesApp).plus(Api.Name.CREATE_UPDATE_MEMO))
    fun createUpdateMemo(@Body createUpdateAgreementMemoRequestBody: CreateUpdateAgreementMemoRequestBody): Call<RetailResponse<Any>>?

    @Multipart
    @POST(Api.Name.UPLOAD_SIGNATURE)
    fun uploadSignature(@Part file: MultipartBody.Part): Call<RetailResponse<SignatureUploadResult>>
}



