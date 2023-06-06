package com.gmail.cristiandeives.myswitch.listgames.data

import com.gmail.cristiandeives.myswitch.common.data.Game
import com.gmail.cristiandeives.myswitch.common.data.GamesDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ListGamesRepositoryTest {
    @Mock
    private lateinit var gamesDao: GamesDao

    private lateinit var repository: ListGamesRepository
    private lateinit var mockitoAnnotations: AutoCloseable

    @Before
    fun setup() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)
        repository = ListGamesRepository(gamesDao)
    }

    @After
    fun tearDown() {
        mockitoAnnotations.close()
    }

    @Test
    fun getGames_defaultCall_daoIsCalled() {
        whenever(gamesDao.readAll()).thenReturn(emptyFlow())

        repository.getGames()

        verify(gamesDao).readAll()
    }

    @Test
    fun getGames_daoReturnGames_gamesAreReturned() = runTest {
        val games = List(3) { i ->
            Game(id = i.toLong(), title = "Game $i", imageUrl = "http://www.example.test/$i")
        }
        mockGameDaoReadAll(games)

        val f = repository.getGames()

        val returnedGames = f.first()
        assertEquals(games, returnedGames)
    }

    private fun mockGameDaoReadAll(games: List<Game>) {
        whenever(gamesDao.readAll())
            .thenReturn(flowOf(games))
    }
}
