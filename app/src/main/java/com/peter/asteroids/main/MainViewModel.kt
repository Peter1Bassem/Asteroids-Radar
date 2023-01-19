package com.peter.asteroids.main

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.peter.asteroids.Asteroid
import com.peter.asteroids.AsteroidsTimeToCome
import com.peter.asteroids.PictureOfDay
import com.peter.asteroids.data.getDatabase
import com.peter.asteroids.repo.AsteroidRepository
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val asteroidRepository = AsteroidRepository(getDatabase(application))

    val toDetailAsteroids = MutableLiveData<Asteroid>()

    private var _AsteroidsTimeToComeByTime = MutableLiveData(AsteroidsTimeToCome.ALLAsteroids)

    @RequiresApi(Build.VERSION_CODES.O)
    val asteroidList = Transformations.switchMap(_AsteroidsTimeToComeByTime) { comingFilter ->
        when (comingFilter!!) {
            AsteroidsTimeToCome.WEEKsAsteroids -> asteroidRepository.weekListofAsteroid
            AsteroidsTimeToCome.TODAYsAsteroids -> asteroidRepository.todayListofAsteroid
            else -> asteroidRepository.allAsteroids
        }
    }

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay
        get() = _pictureOfDay

    init {
        viewModelScope.launch {
            refreshAsteroids()
            refreshPictureOfDay()
            toDetailAsteroids.value = null
        }
    }

    fun onChangeInMenu(filter: AsteroidsTimeToCome) {
        _AsteroidsTimeToComeByTime.postValue(filter)
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            try {
                asteroidRepository.getAsteroidsFromAPI()
            } catch (e: Exception) {
                Log.d("Error1",e.printStackTrace().toString())
                e.printStackTrace()
            }
        }
    }

    private fun refreshPictureOfDay() {
        viewModelScope.launch {
            try {
                _pictureOfDay.value = asteroidRepository.getPictureFromAPI()
            } catch (e: Exception) {
                Log.d("Error2",e.printStackTrace().toString())
                e.printStackTrace()
            }
        }
    }

}