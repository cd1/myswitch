package com.gmail.cristiandeives.myswitch.common.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gmail.cristiandeives.myswitch.addgame.data.GameMediaType

@Entity(tableName = "game")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val coverUrl: String?,
    val mediaType: GameMediaType?,
)