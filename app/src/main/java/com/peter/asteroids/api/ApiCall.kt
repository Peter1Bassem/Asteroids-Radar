package com.peter.asteroids.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.peter.asteroids.Asteroid
import com.peter.asteroids.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

object AsteroidApi {
    val retrofitService: ApiService.AsteroidService by lazy {
        retrofit.create(ApiService.AsteroidService::class.java)
    }
suspend fun getAsteroids(): List<Asteroid>{
    val response = retrofitService.getAsteroids(Constants.API_KEY)
    val jsonObject = JSONObject(response)
    return parseAsteroidsJsonResult(jsonObject)
}
suspend fun getPicOfTheDay() =
    retrofitService.getPictureOfTheDay(Constants.API_KEY)

}