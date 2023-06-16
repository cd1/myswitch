package com.gmail.cristiandeives.myswitch.addgame.data

import arrow.core.Either
import arrow.core.raise.either
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchEntity
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchesDao
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import javax.inject.Inject

class RecentGameSearchesRepository @Inject constructor(
    private val recentGameSearchesDao: RecentGameSearchesDao,
    private val ioDispatcher: CoroutineDispatcher,
    private val logger: Logger,
) {
    fun getRecentGameSearches(): Flow<List<SimpleRecentGameSearch>> =
        recentGameSearchesDao.readAll().map { allGameSearches -> allGameSearches.map { it.toData() } }

    suspend fun upsertRecentGameSearch(query: String): Either<Unit, Unit> =
        either {
            try {
                logger.d(TAG, "[upsertRecentGameSearch] Searching for recent game search with query=$query")
                val existingRecentGameSearchId = withContext(ioDispatcher) {
                    recentGameSearchesDao.readIdByQuery(query)
                }
                logger.v(TAG, "[upsertRecentGameSearch] Recent game search result (ID=$existingRecentGameSearchId)")

                val id = existingRecentGameSearchId ?: 0
                val updatedRecentGameSearch = RecentGameSearchEntity(
                    id = id,
                    query = query,
                    lastUpdated = OffsetDateTime.now().toEpochSecond(),
                )

                logger.d(TAG, "[upsertRecentGameSearch] Upserting recent game search")
                withContext(ioDispatcher) {
                    recentGameSearchesDao.upsert(updatedRecentGameSearch)
                }
                logger.v(TAG, "[upsertRecentGameSearch] Recent game search upserted")
            } catch (ex: Exception) {
                logger.e(TAG, "[upsertRecentGameSearch] Unexpected error: ${ex.message}", ex)
                raise(Unit)
            }
        }

    suspend fun removeRecentGameSearchById(searchId: Long): Either<Unit, Unit> =
        either {
            try {
                logger.d(TAG, "[removeRecentGameSearchById] Removing recent game search with ID=$searchId")
                withContext(ioDispatcher) {
                    recentGameSearchesDao.deleteById(searchId)
                }
                logger.v(TAG, "[removeRecentGameSearchById] Recent game search removed")
            } catch (ex: Exception) {
                logger.e(TAG, "[removeRecentGameSearchById] Unexpected error: ${ex.message}", ex)
                raise(Unit)
            }
        }

    companion object {
        private val TAG = RecentGameSearchesRepository::class.simpleName!!
    }
}