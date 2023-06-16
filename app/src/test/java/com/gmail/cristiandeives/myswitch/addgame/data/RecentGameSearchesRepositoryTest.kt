package com.gmail.cristiandeives.myswitch.addgame.data

import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchEntity
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchesDao
import com.gmail.cristiandeives.myswitch.addgame.data.db.SimpleRecentGameSearch
import com.gmail.cristiandeives.myswitch.common.data.log.FakeLogger
import com.gmail.cristiandeives.myswitch.isLeft
import com.gmail.cristiandeives.myswitch.isRight
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RecentGameSearchesRepositoryTest {
    @Mock
    lateinit var recentGameSearchesDao: RecentGameSearchesDao

    private lateinit var mockitoAnnotations: AutoCloseable

    @Before
    fun before() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)
    }

    @After
    fun after() {
        mockitoAnnotations.close()
    }

    @Test
    fun getRecentGameSearches_daoIsCalled() = runTest {
        val repository = createRepository()

        repository.getRecentGameSearches()

        verify(recentGameSearchesDao)
            .readAll()
    }

    @Test
    fun getRecentGameSearches_whenDaoIsError_thenError() = runTest {
        mockDaoReadAllError()
        val repository = createRepository()

        repository.getRecentGameSearches()
            .onCompletion { ex ->
                assertThat(ex).isNotNull()
            }
    }

    @Test
    fun getRecentGameSearches_success() = runTest {
        val dbGameSearches = listOf(
            SimpleRecentGameSearch(1, "query1"),
            SimpleRecentGameSearch(2, "query2"),
        )
        mockDaoReadAll(dbGameSearches)
        val repository = createRepository()

        val searchesFlow = repository.getRecentGameSearches()

        assertThat(searchesFlow.first()).isEqualTo(dbGameSearches.map { it.toData() })
    }

    @Test
    fun upsertRecentGameSearch_whenDaoReadIdByQueryIsError_thenResultIsError() = runTest {
        mockDaoReadIdByQueryError()
        val repository = createRepository()

        val result = repository.upsertRecentGameSearch("foo")

        assertThat(result).isLeft()
    }

    @Test
    fun upsertRecentGameSearch_whenDaoUpsertIsError_thenResultIsError() = runTest {
        mockDaoUpsertError()
        val repository = createRepository()

        val result = repository.upsertRecentGameSearch("foo")

        assertThat(result).isLeft()
    }

    @Test
    fun upsertRecentGameSearch_whenQueryDoesNotExist_thenResultIsSuccess() = runTest {
        val query = "foo"
        mockDaoReadIdByQuery(query, null)
        val repository = createRepository()

        val result = repository.upsertRecentGameSearch(query)

        assertThat(result).isRight()

        val recentGameSearchEntityCaptor = argumentCaptor<RecentGameSearchEntity>()
        verify(recentGameSearchesDao)
            .readIdByQuery(query)
        verify(recentGameSearchesDao)
            .upsert(recentGameSearchEntityCaptor.capture())

        val capturedRecentGameSearchEntity = recentGameSearchEntityCaptor.firstValue
        assertThat(capturedRecentGameSearchEntity.id).isEqualTo(0)
        assertThat(capturedRecentGameSearchEntity.query).isEqualTo(query)
    }

    @Test
    fun upsertRecentGameSearch_whenQueryExists_thenTimestampIsUpdated() = runTest {
        val query = "foo"
        val recentGameSearchId = 42L
        mockDaoReadIdByQuery(query, recentGameSearchId)
        val repository = createRepository()

        val result = repository.upsertRecentGameSearch(query)

        assertThat(result).isRight()

        val recentGameSearchEntityCaptor = argumentCaptor<RecentGameSearchEntity>()
        verify(recentGameSearchesDao)
            .readIdByQuery(query)
        verify(recentGameSearchesDao)
            .upsert(recentGameSearchEntityCaptor.capture())

        val capturedRecentGameSearchEntity = recentGameSearchEntityCaptor.firstValue
        assertThat(capturedRecentGameSearchEntity.id).isEqualTo(recentGameSearchId)
        assertThat(capturedRecentGameSearchEntity.query).isEqualTo(query)
    }

    @Test
    fun removeRecentGameSearchById_daoIsCalled() = runTest {
        val gameId = 42L
        val repository = createRepository()

        repository.removeRecentGameSearchById(gameId)

        verify(recentGameSearchesDao)
            .deleteById(gameId)
    }

    @Test
    fun removeRecentGameSearchById_whenDaoIsError_thenResultIsError() = runTest {
        mockDaoRemoveRecentGameSearchError()
        val repository = createRepository()

        val result = repository.removeRecentGameSearchById(42)

        assertThat(result).isLeft()
    }

    @Test
    fun removeRecentGameSearchById_success() = runTest {
        val repository = createRepository()

        val result = repository.removeRecentGameSearchById(42)

        assertThat(result).isRight()
    }

    private fun createRepository() = RecentGameSearchesRepository(
        recentGameSearchesDao, UnconfinedTestDispatcher(), FakeLogger(),
    )

    private suspend fun mockDaoReadIdByQuery(query: String, expectedId: Long?) {
        whenever(recentGameSearchesDao.readIdByQuery(query))
            .thenReturn(expectedId)
    }

    private suspend fun mockDaoReadIdByQueryError() {
        whenever(recentGameSearchesDao.readIdByQuery(any()))
            .thenThrow(RuntimeException("mocked exception"))
    }

    private suspend fun mockDaoUpsertError() {
        whenever(recentGameSearchesDao.upsert(any()))
            .thenThrow(RuntimeException("mocked exception"))
    }

    private suspend fun mockDaoRemoveRecentGameSearchError() {
        whenever(recentGameSearchesDao.deleteById(any()))
            .thenThrow(RuntimeException("mocked exception"))
    }

    private fun mockDaoReadAll(recentGameSearches: List<SimpleRecentGameSearch>) {
        whenever(recentGameSearchesDao.readAll())
            .thenReturn(flowOf(recentGameSearches))
    }

    private fun mockDaoReadAllError() {
        whenever(recentGameSearchesDao.readAll())
            .thenReturn(flow { throw RuntimeException("mocked exception") })
    }
}