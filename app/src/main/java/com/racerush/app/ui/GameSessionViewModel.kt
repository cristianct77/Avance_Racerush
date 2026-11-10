package com.racerush.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.racerush.app.data.model.Car
import com.racerush.app.data.model.RaceResult
import com.racerush.app.data.model.Track
import com.racerush.app.data.repository.FakeRaceRepository
import com.racerush.app.data.repository.RaceRepository

/**
 * ViewModel compartido a nivel de Activity (activityViewModels) que guarda el
 * estado de la sesión de juego: auto elegido, pista elegida, último resultado
 * y preferencias de ajustes.
 */
class GameSessionViewModel(
    private val repository: RaceRepository = FakeRaceRepository
) : ViewModel() {

    val tracks: List<Track> = repository.getTracks()
    val playerName: String get() = repository.playerName

    private val _selectedCar = MutableLiveData(repository.getCars().first())
    val selectedCar: LiveData<Car> = _selectedCar

    private val _selectedTrack = MutableLiveData(repository.getTracks().first())
    val selectedTrack: LiveData<Track> = _selectedTrack

    private val _lastResult = MutableLiveData<RaceResult?>(null)
    val lastResult: LiveData<RaceResult?> = _lastResult

    var soundEnabled = true
    var musicEnabled = true
    var vibrationEnabled = true

    fun selectCar(car: Car) { _selectedCar.value = car }

    fun selectTrack(track: Track) { _selectedTrack.value = track }

    fun finishRace(result: RaceResult) {
        repository.saveRaceResult(result)
        _lastResult.value = result
    }
}
