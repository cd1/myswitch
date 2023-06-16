package com.gmail.cristiandeives.myswitch.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GamesDao {
    @Insert
    suspend fun insert(game: GameEntity)

    @Query("SELECT id, name, coverUrl, mediaType FROM game ORDER BY name")
    fun readAll(): Flow<List<GameEntity>>
}
