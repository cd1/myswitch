package com.gmail.cristiandeives.myswitch.common.data.network

import kotlinx.serialization.Serializable

@Serializable
class InvolvedCompaniesResponse(
    val id: Long,
    val company: CompanyResponse,
    val publisher: Boolean,
)
