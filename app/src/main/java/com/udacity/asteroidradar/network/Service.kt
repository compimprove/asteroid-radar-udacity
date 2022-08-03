package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.BASE_ASTEROID_URL
import com.udacity.asteroidradar.Constants.BASE_NASA_URL
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    fun getAsteroidByDate(
        @Query("api_key") key: String = Constants.API_KEY,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
    ): Call<String>
}

interface NasaApiService {
    @GET("planetary/apod")
    fun getNasaPictureOfDay(
        @Query("api_key") key: String = Constants.API_KEY,
    ): Call<PictureOfDay>
}



private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


object Network {
    val asteroidApiService: AsteroidApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_ASTEROID_URL)
            .build()
            .create(AsteroidApiService::class.java)
    }

    val nasaApiService: NasaApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_NASA_URL)
            .build()
            .create(NasaApiService::class.java)
    }

}