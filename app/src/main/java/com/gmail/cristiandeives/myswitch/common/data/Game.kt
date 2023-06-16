package com.gmail.cristiandeives.myswitch.common.data

import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType
import com.gmail.cristiandeives.myswitch.common.data.db.GameEntity

data class Game(
    val id: Long = 0,
    val coverUrl: String?,
    val name: String,
    val mediaType: GameMediaType?,
)

fun GameEntity.toData() = Game(
    id = this.id,
    coverUrl = this.coverUrl,
    name = this.name,
    mediaType = this.mediaType,
)