package com.racerush.app.ui.garage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.racerush.app.data.model.Car
import com.racerush.app.data.repository.FakeRaceRepository
import com.racerush.app.data.repository.RaceRepository

/** Maneja el carrusel de autos del garaje (anterior / siguiente). */
class GarageViewModel(
    repository: RaceRepository = FakeRaceRepository
) : ViewModel() {

    val cars: List<Car> = repository.getCars()

    private val _index = MutableLiveData(0)
    val index: LiveData<Int> = _index
    val currentCar: LiveData<Car> = _index.map { cars[it] }

    private var initialized = false

    /** Posiciona el carrusel en el auto ya elegido (solo la primera vez). */
    fun start(selected: Car?) {
        if (initialized) return
        initialized = true
        val i = cars.indexOfFirst { it.id == selected?.id }
        _index.value = if (i >= 0) i else 0
    }

    fun next() { _index.value = ((_index.value ?: 0) + 1) % cars.size }

    fun previous() { _index.value = ((_index.value ?: 0) - 1 + cars.size) % cars.size }
}
