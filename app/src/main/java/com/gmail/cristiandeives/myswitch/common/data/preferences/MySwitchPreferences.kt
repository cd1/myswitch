package com.gmail.cristiandeives.myswitch.common.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.gmail.cristiandeives.myswitch.addgame.data.AccessToken
import com.gmail.cristiandeives.myswitch.common.data.preferences.proto.AccessTokenProto
import com.google.protobuf.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

class MySwitchPreferences @Inject constructor(
    private val accessTokenDataStore: DataStore<AccessTokenProto>,
) {
    fun getAccessToken(): Flow<AccessToken> =
        accessTokenDataStore.data.map { it.toData() }

    suspend fun setAccessToken(token: AccessToken) {
        accessTokenDataStore.updateData { token.toProto() }
    }

    suspend fun clearAccessToken() {
        accessTokenDataStore.updateData { AccessTokenProto.getDefaultInstance() }
    }

    companion object {
        private fun AccessTokenProto.toData() = AccessToken(
            accessToken = this.accessToken,
            expirationTime = OffsetDateTime.ofInstant(
                Instant.ofEpochSecond(this.expirationTime.seconds, this.expirationTime.nanos.toLong()),
                ZoneId.systemDefault(),
            ),
            tokenType = this.tokenType,
        )

        private fun AccessToken.toProto(): AccessTokenProto {
            val expirationTimeInstant = this.expirationTime.toInstant()

            return AccessTokenProto.newBuilder()
                .setAccessToken(this.accessToken)
                .setExpirationTime(Timestamp.newBuilder()
                    .setSeconds(expirationTimeInstant.epochSecond)
                    .setNanos(expirationTimeInstant.nano)
                    .build())
                .setTokenType(this.tokenType)
                .build()
        }
    }
}

val Context.accessTokenDataStore: DataStore<AccessTokenProto> by dataStore(
    fileName = "access_token.pb",
    serializer = AccessTokenSerializer,
)