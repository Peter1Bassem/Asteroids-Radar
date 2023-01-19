package com.peter.asteroids.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.peter.asteroids.Constants
import com.peter.asteroids.api.ApiService
import com.peter.asteroids.api.AsteroidApi
import com.peter.asteroids.data.getDatabase
import com.peter.asteroids.main.MainViewModel
import com.peter.asteroids.repo.AsteroidRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "RefreshAstroidWorker"
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            repository.getAsteroidsFromAPI()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}