package com.gmail.cristiandeives.myswitch.common.data.network

import kotlinx.serialization.Serializable

@Serializable
class GamesResponse(
    val id: Long,
    val cover: CoversResponse? = null,
    val name: String,
)