package com.racerush.app.ui.race

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.racerush.app.data.model.Car
import com.racerush.app.data.model.RaceResult
import com.racerush.app.data.model.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

enum class RacePhase { COUNTDOWN, RACING, FINISHED }

/** Rival tal como se dibuja: posición lateral (0..1) y distancia relativa al jugador (m). */
data class RivalSprite(val x: Float, val relativeMeters: Float, val colorIndex: Int)

/** Estado inmutable que la Vista observa y dibuja. */
data class RaceState(
    val phase: RacePhase = RacePhase.COUNTDOWN,
    val countdown: Int = 3,
    val showGo: Boolean = false,
    val lap: Int = 1,
    val totalLaps: Int = 3,
    val position: Int = 6,
    val totalRacers: Int = 6,
    val speedKmh: Float = 0f,
    val elapsedMs: Long = 0L,
    val playerX: Float = 0.5f,
    val distanceMeters: Float = 0f,
    val rivals: List<RivalSprite> = emptyList(),
    val crashed: Boolean = false
)

/**
 * Lógica de la carrera (bucle de juego ~60 FPS en una corrutina).
 * La Vista (RaceTrackView + RaceFragment) solo dibuja el [RaceState] y envía la dirección.
 * En este avance la carrera es local: no se consumen servicios del backend.
 */
class RaceViewModel : ViewModel() {

    private class Rival(var x: Float, var distance: Float, val baseSpeed: Float, val phase: Float)

    private val _state = MutableLiveData(RaceState())
    val state: LiveData<RaceState> = _state

    private val _result = MutableLiveData<RaceResult?>(null)
    val result: LiveData<RaceResult?> = _result

    private var started = false
    private var paused = false
    private var steering = 0
    private val rivals = mutableListOf<Rival>()

    fun setSteering(direction: Int) { steering = direction }
    fun setPaused(value: Boolean) { paused = value }
    fun consumeResult() { _result.value = null }

    fun start(car: Car, track: Track) {
        if (started) return
        started = true

        val lapLength = LAP_LENGTH_M
        val totalLaps = track.laps
        val maxSpeed = car.maxSpeedKmh
        val accel = 22f + car.boost * 0.45f            // km/h por segundo
        val steerRate = 0.55f + car.grip * 0.007f       // ancho de pista por segundo

        // Parrilla de salida: el jugador sale último.
        rivals.clear()
        val lanes = listOf(0.3f, 0.7f, 0.3f, 0.7f, 0.5f)
        repeat(RIVALS) { i ->
            rivals += Rival(
                x = lanes[i],
                distance = 10f * (RIVALS - i),
                baseSpeed = Random.nextInt(178, 208).toFloat(),
                phase = Random.nextFloat() * 6f
            )
        }

        viewModelScope.launch {
            // Cuenta regresiva 3, 2, 1
            for (c in 3 downTo 1) {
                _state.value = RaceState(countdown = c, totalLaps = totalLaps, rivals = sprites(0f))
                delay(1000)
            }

            var speed = 0f
            var x = 0.5f
            var distance = 0f
            var elapsed = 0L
            var crashTimer = 0f
            var last = SystemClock.elapsedRealtime()

            while (isActive) {
                delay(FRAME_MS)
                val now = SystemClock.elapsedRealtime()
                val dtMs = now - last
                last = now
                if (paused) continue
                val dt = (dtMs.coerceAtMost(100L)) / 1000f
                elapsed += dtMs.coerceAtMost(100L)

                // Jugador
                val cap = if (crashTimer > 0f) maxSpeed * 0.55f else maxSpeed
                speed = if (speed < cap) (speed + accel * dt).coerceAtMost(cap) else (speed - 80f * dt).coerceAtLeast(cap)
                x = (x + steering * steerRate * dt).coerceIn(MIN_X, MAX_X)
                distance += speed / 3.6f * dt
                crashTimer = (crashTimer - dt).coerceAtLeast(0f)

                // Rivales
                val t = elapsed / 1000f
                rivals.forEach { r ->
                    val s = r.baseSpeed + 10f * sin(t * 0.7f + r.phase)
                    r.distance += s / 3.6f * dt
                    r.x = (r.x + 0.04f * sin(t * 0.9f + r.phase) * dt).coerceIn(MIN_X, MAX_X)
                }

                // Colisiones simples (cajas)
                if (crashTimer <= 0f && rivals.any {
                        abs(it.distance - distance) < CAR_LENGTH_M && abs(it.x - x) < CAR_WIDTH_X
                    }) {
                    crashTimer = 0.9f
                    speed *= 0.6f
                }

                val position = 1 + rivals.count { it.distance > distance }
                val lap = ((distance / lapLength).toInt() + 1).coerceAtMost(totalLaps)
                val finished = distance >= lapLength * totalLaps

                _state.value = RaceState(
                    phase = if (finished) RacePhase.FINISHED else RacePhase.RACING,
                    countdown = 0,
                    showGo = elapsed < 800,
                    lap = lap,
                    totalLaps = totalLaps,
                    position = position,
                    totalRacers = RIVALS + 1,
                    speedKmh = speed,
                    elapsedMs = elapsed,
                    playerX = x,
                    distanceMeters = distance,
                    rivals = sprites(distance),
                    crashed = crashTimer > 0f
                )

                if (finished) {
                    _result.value = RaceResult(
                        trackName = track.name,
                        carName = car.name,
                        position = position,
                        totalRacers = RIVALS + 1,
                        timeMillis = elapsed,
                        pointsEarned = POINTS.getOrElse(position - 1) { 0 }
                    )
                    break
                }
            }
        }
    }

    private fun sprites(playerDistance: Float) = rivals.mapIndexedNotNull { i, r ->
        val rel = r.distance - playerDistance
        if (rel in -20f..110f) RivalSprite(r.x, rel, i) else null
    }

    companion object {
        const val LAP_LENGTH_M = 900f
        private const val RIVALS = 5
        private const val FRAME_MS = 16L
        private const val MIN_X = 0.16f
        private const val MAX_X = 0.84f
        private const val CAR_LENGTH_M = 4.5f
        private const val CAR_WIDTH_X = 0.14f
        private val POINTS = listOf(10, 8, 6, 5, 4, 3)

        fun formatTime(ms: Long): String {
            val totalSec = ms / 1000
            val tenths = (ms % 1000) / 100
            return "%d:%02d.%d".format(totalSec / 60, totalSec % 60, tenths)
        }
    }
}
