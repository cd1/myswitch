package com.gmail.cristiandeives.myswitch.common.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT id, title, imageUrl FROM game ORDER BY title")
    fun readAll(): Flow<List<Game>>
}
