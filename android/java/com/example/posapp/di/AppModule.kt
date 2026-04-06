package com.example.posapp.di

import android.content.Context
import androidx.room.Room
import com.example.posapp.data.local.AppDatabase
import com.example.posapp.data.repository.PosRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pos.db"
        )
            .fallbackToDestructiveMigration() // 🔥 FIX CRASH
            .build()

    @Provides
    @Singleton
    fun provideRepository(
        db: AppDatabase
    ): PosRepository =
        PosRepository(db.orderDao())
}