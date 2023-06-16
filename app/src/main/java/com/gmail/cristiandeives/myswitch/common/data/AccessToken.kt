package com.gmail.cristiandeives.myswitch.common.data

import java.time.OffsetDateTime

data class AccessToken(
    val accessToken: String,
    val expirationTime: OffsetDateTime,
    val tokenType: String,
) {
    fun isValid(): Boolean =
        expirationTime.isAfter(OffsetDateTime.now())
}