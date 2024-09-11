package com.shalatan.audioplayground.di

import android.content.Context
import com.shalatan.audioplayground.play.AudioPlayerImpl
import com.shalatan.audioplayground.record.AudioRecorderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import linc.com.amplituda.Amplituda
import javax.inject.Singleton

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

    @Provides
    fun provideAmplituda(@ApplicationContext context: Context): Amplituda {
        return Amplituda(context)
    }

    @Provides
    @Singleton
    fun provideContext(application: Context): Context {
        return application
    }
}