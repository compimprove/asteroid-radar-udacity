package com.udacity.asteroidradar.database

/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay

@Dao
interface AsteroidDao {
    @Query("select * from asteroid order by asteroid.closeApproachDate desc")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: Asteroid)

    @Query("DELETE FROM asteroid")
    suspend fun clear()
}

@Dao
interface PictureOfDayDao {
    @Query("SELECT * FROM pictureofday LIMIT 1")
    fun getPicture(): LiveData<PictureOfDay?>

    @Insert
    suspend fun insert(night: PictureOfDay)

    @Query("DELETE FROM pictureofday where pictureofday.url != :excludedUrl")
    suspend fun clearExclude(excludedUrl: String)

    @Query("DELETE FROM pictureofday")
    suspend fun clear()
}

@Database(entities = [Asteroid::class, PictureOfDay::class], version = 3)
abstract class LocalDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val pictureOfDayDao: PictureOfDayDao
}

private lateinit var INSTANCE: LocalDatabase

fun getDatabase(context: Context): LocalDatabase {
    synchronized(LocalDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "asteroid"
                ).fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}
