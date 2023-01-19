package com.peter.asteroids.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.peter.asteroids.Asteroid
import com.peter.asteroids.Constants.API_KEY
import com.peter.asteroids.PictureOfDay
import com.peter.asteroids.api.ApiService
import com.peter.asteroids.api.AsteroidApi
import com.peter.asteroids.api.parseAsteroidsJsonResult
import com.peter.asteroids.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().minusDays(7).toString()
    private val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    @RequiresApi(Build.VERSION_CODES.O)
    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroids()) {
                it.asAsteroidsModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val todayListofAsteroid: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsToday(
            startDate.format(DateTimeFormatter.ISO_DATE))) {
                it.asAsteroidsModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val weekListofAsteroid: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao.getAsteroidsWeek(
                startDate.format(DateTimeFormatter.ISO_DATE),
                endDate.format(DateTimeFormatter.ISO_DATE)
            )
        ) {
            it.asAsteroidsModel()
        }
    suspend fun getAsteroidsFromAPI() {
        withContext(Dispatchers.IO) {
            val asteroids = AsteroidApi.getAsteroids()
            database.asteroidDao.insertAll(*asteroids.asComeDatabaseModel())
        }
    }

    suspend fun getPictureFromAPI(): PictureOfDay {
        lateinit var Picture: PictureOfDay
        withContext(Dispatchers.IO) {
            Picture = AsteroidApi.getPicOfTheDay()
        }
        return Picture
    }
}