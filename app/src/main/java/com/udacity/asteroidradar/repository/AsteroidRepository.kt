package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.LocalDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: LocalDatabase) {

    val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids()

    suspend fun refreshAsteroids() {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        val nextSevenDays = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        withContext(Dispatchers.IO){
            val playlist = Network.asteroidApiService.getAsteroidByDate(
                startDate = dateFormat.format(currentTime),
                endDate = dateFormat.format(nextSevenDays)
            ).await()

            database.asteroidDao.insertAll(
                * JSONObject(playlist).parseAsteroidsJsonResult(
                    getNextSevenDaysFormattedDates()
                ).toTypedArray()
            )
        }
    }
}