package com.shalatan.audioplayground.di

import android.content.Context
import com.shalatan.audioplayground.play.AudioPlayerImpl
import com.shalatan.audioplayground.record.AudioRecorderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAudioRecorder(@ApplicationContext context: Context): AudioRecorderImpl {
        return AudioRecorderImpl(context)
    }

    @Provides
    fun provideAudioPlayer(@ApplicationContext context: Context): AudioPlayerImpl {
        return AudioPlayerImpl(context)
    }
}