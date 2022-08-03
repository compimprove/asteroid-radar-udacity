package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.PictureOfDayRepository
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val database = getDatabase(app)
    private val asteroidRepository = AsteroidRepository(database)
    private val pictureOfDayRepository = PictureOfDayRepository(database)

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }
        viewModelScope.launch {
            pictureOfDayRepository.refreshPicture()
        }
    }

    val asteroids = asteroidRepository.asteroids

    val pictureOfDay = pictureOfDayRepository.picture
//    val pictureOfDay = MutableLiveData<PictureOfDay?>()

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedProperty: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    fun displayAsteroidDetails(marsProperty: Asteroid) {
        _navigateToSelectedAsteroid.value = marsProperty
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}