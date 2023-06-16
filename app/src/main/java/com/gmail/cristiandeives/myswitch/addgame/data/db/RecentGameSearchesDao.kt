package com.gmail.cristiandeives.myswitch.addgame.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentGameSearchesDao {
    @Upsert
    suspend fun upsert(recentGameSearch: RecentGameSearchEntity)

    @Query("SELECT id, `query` FROM recentGameSearch ORDER BY lastUpdated DESC LIMIT 10")
    fun readAll(): Flow<List<SimpleRecentGameSearch>>

    @Query("SELECT id FROM recentGameSearch WHERE `query` = :query")
    suspend fun readIdByQuery(query: String): Long?

    @Query("DELETE FROM recentGameSearch WHERE id = :id")
    suspend fun deleteById(id: Long)
}

