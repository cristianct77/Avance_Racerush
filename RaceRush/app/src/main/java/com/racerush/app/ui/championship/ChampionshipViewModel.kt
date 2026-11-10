package com.racerush.app.ui.championship

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.racerush.app.data.model.Championship
import com.racerush.app.data.model.Standing
import com.racerush.app.data.repository.FakeRaceRepository
import com.racerush.app.data.repository.RaceRepository

/** Clasificación del campeonato e inscripción (formulario 2). */
class ChampionshipViewModel(
    private val repository: RaceRepository = FakeRaceRepository
) : ViewModel() {

    private val _standings = MutableLiveData<List<Standing>>()
    val standings: LiveData<List<Standing>> = _standings

    val championships: List<Championship> = repository.getChampionships()

    private val _joined = MutableLiveData(false)
    val joined: LiveData<Boolean> = _joined

    fun loadStandings() { _standings.value = repository.getStandings() }

    fun join(alias: String, championship: Championship, category: String) {
        _joined.value = repository.joinChampionship(alias.trim(), championship, category)
    }

    fun onJoinHandled() { _joined.value = false }
}
