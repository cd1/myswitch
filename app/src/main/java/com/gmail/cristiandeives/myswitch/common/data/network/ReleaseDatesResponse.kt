package com.gmail.cristiandeives.myswitch.common.data.network

import kotlinx.serialization.Serializable

@Serializable
class ReleaseDatesResponse(
    val id: Long,
    val platform: Int,
    val y: Int? = null,
)
