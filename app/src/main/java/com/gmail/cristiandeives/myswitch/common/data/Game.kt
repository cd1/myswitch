package com.gmail.cristiandeives.myswitch.common.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey
    val id: Long,

    val title: String,
    val imageUrl: String?,
)
