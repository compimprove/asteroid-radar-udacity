package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.getTodayFormattedDates
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.PictureOfDayRepository
import com.udacity.asteroidradar.utils.combine
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val database = getDatabase(app)
    private val asteroidRepository = AsteroidRepository(database)
    private val pictureOfDayRepository = PictureOfDayRepository(database)
    private var _showAsteroidMenu = MutableLiveData(R.id.show_week_asteroids)

    init {
        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroids()
            } catch (e: Exception) {
            }
        }
        viewModelScope.launch {
            try {
                pictureOfDayRepository.refreshPicture()
            } catch (e: Exception) {
            }
        }
    }

    val allAsteroids = asteroidRepository.asteroids

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(allAsteroids.combine(_showAsteroidMenu)) { it ->
            run {
                val todayString = getTodayFormattedDates()
                return@map when (it.second) {
                    R.id.show_today_asteroids -> it.first?.filter { it ->
                        it.closeApproachDate == todayString
                    }
                    else -> it.first
                }
            }
        }

    val pictureOfDay = pictureOfDayRepository.picture

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid?>()
    val navigateToSelectedProperty: LiveData<Asteroid?>
        get() = _navigateToSelectedAsteroid

    fun displayAsteroidDetails(marsProperty: Asteroid) {
        _navigateToSelectedAsteroid.value = marsProperty
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun changeShowAsteroidMenu(id: Int) {
        if (id != _showAsteroidMenu.value) {
            _showAsteroidMenu.value = id
        }
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