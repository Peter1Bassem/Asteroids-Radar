package com.peter.asteroids.api


import com.peter.asteroids.PictureOfDay
import retrofit2.http.GET
import retrofit2.http.Query

class ApiService {

    interface AsteroidService {
        @GET("neo/rest/v1/feed")
        suspend fun getAsteroids(
            @Query("api_key") api_key: String
        ): String

        @GET("planetary/apod")
        suspend fun getPictureOfTheDay(
            @Query("api_key") api_key: String
        ): PictureOfDay
    }


}