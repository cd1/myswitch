package com.gmail.cristiandeives.myswitch.common.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AccessTokenResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("expires_in")
    val expiresIn: Long,

    @SerialName("token_type")
    val tokenType: String,
)
