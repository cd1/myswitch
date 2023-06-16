package com.gmail.cristiandeives.myswitch.di

import android.content.Context
import com.gmail.cristiandeives.myswitch.addgame.data.db.RecentGameSearchesDao
import com.gmail.cristiandeives.myswitch.common.data.db.GamesDao
import com.gmail.cristiandeives.myswitch.common.data.db.MySwitchDatabase
import com.gmail.cristiandeives.myswitch.common.data.log.AndroidLogger
import com.gmail.cristiandeives.myswitch.common.data.log.Logger
import com.gmail.cristiandeives.myswitch.common.data.network.IgdbService
import com.gmail.cristiandeives.myswitch.common.data.network.TwitchService
import com.gmail.cristiandeives.myswitch.common.data.preferences.accessTokenDataStore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideLogger(): Logger = AndroidLogger()

    @Provides
    fun provideGamesDao(database: MySwitchDatabase): GamesDao =
        database.gamesDao()

    @Provides
    fun provideRecentGameSearchesDao(database: MySwitchDatabase): RecentGameSearchesDao =
        database.recentGameSearchesDao()

    @IgdbRetrofit
    @Provides
    fun provideIgdbRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.igdb.com/v4/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    @TwitchRetrofit
    @Provides
    fun provideTwitchRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://id.twitch.tv/")
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    fun provideTwitchService(@TwitchRetrofit retrofit: Retrofit): TwitchService =
        retrofit.create(TwitchService::class.java)

    @Provides
    fun provideIgdbService(@IgdbRetrofit retrofit: Retrofit): IgdbService =
        retrofit.create(IgdbService::class.java)

    // Singleton?
    @Provides
    fun provideAccessTokenDataStore(@ApplicationContext context: Context) =
        context.accessTokenDataStore
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TwitchRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IgdbRetrofit
