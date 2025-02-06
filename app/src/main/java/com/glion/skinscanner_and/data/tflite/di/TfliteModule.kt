package com.glion.skinscanner_and.data.tflite.di

import android.content.Context
import com.glion.skinscanner_and.R
import com.glion.skinscanner_and.data.tflite.reppository.TfliteRepository
import com.glion.skinscanner_and.data.tflite.reppository.TfliteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

@Module
@InstallIn(SingletonComponent::class)
object TfliteModule {
    @Provides
    fun providesTfliteModel(
        @ApplicationContext context: Context
    ) : Interpreter {
        val fileDescriptor = context.assets.openFd(context.getString(R.string.model_name))
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val loadModelFile = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(loadModelFile)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class TfliteRepositoryModule {
    @Binds
    abstract fun bindsTfliteRepository(
        tfliteRepositoryImpl: TfliteRepositoryImpl
    ) : TfliteRepository
}
