package com.lfsolutions.retail.model

data class LoginRequest(
    var tenancyName: String,
    var userNameOrEmailAddress: String,
    var password: String
)
