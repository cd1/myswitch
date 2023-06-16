package com.gmail.cristiandeives.myswitch.common.data.network

import kotlinx.serialization.Serializable

@Serializable
class CompanyResponse(
    val id: Long,
    val name: String,
)
