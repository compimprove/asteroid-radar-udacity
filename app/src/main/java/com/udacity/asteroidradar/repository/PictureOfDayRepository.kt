package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.LocalDatabase
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await

class PictureOfDayRepository(private val database: LocalDatabase) {

    val picture = database.pictureOfDayDao.getPicture()

    suspend fun refreshPicture() {
        withContext(Dispatchers.IO) {
            val picture = Network.nasaApiService.getNasaPictureOfDay().await()
            if (picture.mediaType == "image") {
                database.pictureOfDayDao.insert(picture)
                database.pictureOfDayDao.clearExclude(picture.url)
            }
        }
    }
}