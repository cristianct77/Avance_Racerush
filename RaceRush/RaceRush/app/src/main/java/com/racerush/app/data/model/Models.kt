package com.racerush.app.data.model

import androidx.annotation.ColorRes
import com.racerush.app.R

/** Auto disponible en el garaje. Las estadísticas van de 0 a 100. */
data class Car(
    val id: Int,
    val name: String,
    val speed: Int,
    val grip: Int,
    val boost: Int,
    @ColorRes val colorRes: Int
) {
    /** Velocidad máxima en km/h derivada de la estadística de velocidad. */
    val maxSpeedKmh: Float get() = 150f + speed * 0.9f
}

/** Pista seleccionable para una carrera. */
data class Track(
    val id: Int,
    val name: String,
    val laps: Int,
    val bestTime: String
)

/** Fila de la tabla de clasificación del campeonato. */
data class Standing(
    val position: Int,
    val driver: String,
    val points: Int,
    val isPlayer: Boolean = false
)

/** Campeonato al que el jugador se puede inscribir. */
data class Championship(
    val id: Int,
    val name: String,
    val races: Int
) {
    override fun toString(): String = name
}

enum class NotificationType { RACE, CHAMPIONSHIP, SYSTEM }

/** Notificación que se muestra dentro de la app. */
data class AppNotification(
    val title: String,
    val message: String,
    val type: NotificationType,
    val timeLabel: String
)

/** Resultado de una carrera finalizada. */
data class RaceResult(
    val trackName: String,
    val carName: String,
    val position: Int,
    val totalRacers: Int,
    val timeMillis: Long,
    val pointsEarned: Int
)

val NotificationType.colorRes: Int
    get() = when (this) {
        NotificationType.RACE -> R.color.rr_red
        NotificationType.CHAMPIONSHIP -> R.color.rr_yellow
        NotificationType.SYSTEM -> R.color.rr_gray_text
    }
