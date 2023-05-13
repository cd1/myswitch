package com.gmail.cristiandeives.myswitch.di

import com.gmail.cristiandeives.myswitch.common.data.GameDao
import com.gmail.cristiandeives.myswitch.common.data.MySwitchDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideGameDao(database: MySwitchDatabase): GameDao =
        database.gameDao()
}
