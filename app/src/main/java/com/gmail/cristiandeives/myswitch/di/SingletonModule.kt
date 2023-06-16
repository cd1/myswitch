package com.gmail.cristiandeives.myswitch.di

import android.content.Context
import androidx.room.Room
import com.gmail.cristiandeives.myswitch.common.data.db.MySwitchDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MySwitchDatabase =
        Room.databaseBuilder(context, MySwitchDatabase::class.java, "myswitch.db")
            .build()
}
