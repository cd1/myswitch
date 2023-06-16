package com.gmail.cristiandeives.myswitch.common.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.gmail.cristiandeives.myswitch.common.data.AccessToken
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import com.gmail.cristiandeives.myswitch.common.data.preferences.proto.AccessTokenProto
import com.google.protobuf.Timestamp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

class MySwitchPreferences @Inject constructor(
    private val accessTokenDataStore: DataStore<AccessTokenProto>,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: Logger,
) {
    fun getAccessToken(): Flow<AccessToken> =
        accessTokenDataStore.data.map { accessTokenProto ->
            accessTokenProto.toData().also { accessToken ->
                logger.v(TAG, "[getAccessToken] Got new access token: $accessToken")
            }
        }

    suspend fun setAccessToken(token: AccessToken) {
        logger.d(TAG, "[setAccessToken] Saving access token")
        withContext(ioDispatcher) {
            accessTokenDataStore.updateData { token.toProto() }
        }
        logger.v(TAG, "[setAccessToken] Access token saved")
    }

    suspend fun clearAccessToken() {
        logger.d(TAG, "[clearAccessToken] Clearing access token")
        withContext(ioDispatcher) {
            accessTokenDataStore.updateData { AccessTokenProto.getDefaultInstance() }
        }
        logger.v(TAG, "[clearAccessToken] Access token cleared")
    }

    companion object {
        private val TAG = MySwitchPreferences::class.simpleName!!

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
    serializer = AccessTokenProtoSerializer,
)