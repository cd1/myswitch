package com.gmail.cristiandeives.myswitch.common.data.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.gmail.cristiandeives.myswitch.common.data.preferences.proto.AccessTokenProto
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object AccessTokenSerializer : Serializer<AccessTokenProto> {
    override val defaultValue: AccessTokenProto = AccessTokenProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AccessTokenProto = try {
        AccessTokenProto.parseFrom(input)
    } catch (ex: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", ex)
    }

    override suspend fun writeTo(t: AccessTokenProto, output: OutputStream) =
        t.writeTo(output)
}
