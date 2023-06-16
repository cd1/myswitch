package com.gmail.cristiandeives.myswitch.common.data.network

import com.gmail.cristiandeives.myswitch.common.data.AccessToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
class AccessTokenResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("expires_in")
    val expiresIn: Long,

    @SerialName("token_type")
    val tokenType: String,
)

fun AccessTokenResponse.toAccessToken() = AccessToken(
    accessToken = this.accessToken,
    expirationTime = OffsetDateTime.now().plusSeconds(this.expiresIn),
    tokenType = this.tokenType,
)