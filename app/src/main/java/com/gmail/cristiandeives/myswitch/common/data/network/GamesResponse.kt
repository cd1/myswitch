package com.gmail.cristiandeives.myswitch.common.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GamesResponse(
    val id: Long,

    val artworks: List<ArtworksResponse>? = null,

    val cover: CoversResponse? = null,

    @SerialName("involved_companies")
    val involvedCompanies: List<InvolvedCompaniesResponse>? = null,

    val name: String,

    @SerialName("release_dates")
    val releaseDates: List<ReleaseDatesResponse>? = null,

    val summary: String? = null,
)