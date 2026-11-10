package com.racerush.app.data.repository

import com.racerush.app.R
import com.racerush.app.data.model.AppNotification
import com.racerush.app.data.model.Car
import com.racerush.app.data.model.Championship
import com.racerush.app.data.model.NotificationType
import com.racerush.app.data.model.RaceResult
import com.racerush.app.data.model.Standing
import com.racerush.app.data.model.Track

/**
 * Contrato de acceso a datos (capa Repository del MVVM).
 * En la siguiente entrega se implementará con Firebase / API REST;
 * en este avance se usa [FakeRaceRepository] con datos locales.
 */
interface RaceRepository {
    val playerName: String
    fun getCars(): List<Car>
    fun getTracks(): List<Track>
    fun getChampionships(): List<Championship>
    fun getStandings(): List<Standing>
    fun getNotifications(): List<AppNotification>
    fun login(email: String, password: String): Boolean
    fun register(name: String, email: String, password: String): Boolean
    fun joinChampionship(alias: String, championship: Championship, category: String): Boolean
    fun saveRaceResult(result: RaceResult)
}

/** Implementación en memoria (sin backend) usada para probar la navegación. */
object FakeRaceRepository : RaceRepository {

    private var currentPlayer: String = "Tú"
    override val playerName: String get() = currentPlayer

    private val cars = listOf(
        Car(1, "SPEEDSTER V1", speed = 50, grip = 70, boost = 90, colorRes = R.color.rr_red),
        Car(2, "THUNDER GT", speed = 80, grip = 55, boost = 60, colorRes = R.color.rr_blue),
        Car(3, "NITRO X", speed = 65, grip = 85, boost = 70, colorRes = R.color.rr_green)
    )

    private val tracks = listOf(
        Track(1, "Circuito del Desierto", 3, "1:20.4"),
        Track(2, "Sprint Urbano", 3, "1:21.4"),
        Track(3, "Paso de Montaña", 3, "1:22.4")
    )

    private val championships = listOf(
        Championship(1, "Copa Relámpago", 4),
        Championship(2, "Gran Premio Nocturno", 6),
        Championship(3, "Liga de Montaña", 5)
    )

    private var playerPoints = 38

    private val rivals = mutableListOf(
        "N. Reyes" to 35,
        "A. Díaz" to 29,
        "K. Osei" to 24,
        "L. Gómez" to 18,
        "M. Silva" to 12
    )

    private val notifications = mutableListOf(
        AppNotification("¡Carrera Completada!", "Terminaste 1° en el Circuito del Desierto",
            NotificationType.RACE, "Hace 5 min"),
        AppNotification("Actualización del Campeonato", "Subiste al puesto #1",
            NotificationType.CHAMPIONSHIP, "Hace 5 min"),
        AppNotification("Nuevo auto disponible", "El NITRO X ya está en tu garaje",
            NotificationType.SYSTEM, "Ayer")
    )

    override fun getCars() = cars
    override fun getTracks() = tracks
    override fun getChampionships() = championships

    override fun getStandings(): List<Standing> {
        val all = rivals.map { Standing(0, it.first, it.second) } +
            Standing(0, currentPlayer, playerPoints, isPlayer = true)
        return all.sortedByDescending { it.points }
            .mapIndexed { index, s -> s.copy(position = index + 1) }
    }

    override fun getNotifications(): List<AppNotification> = notifications.toList()

    override fun login(email: String, password: String): Boolean = true

    override fun register(name: String, email: String, password: String): Boolean {
        currentPlayer = name.ifBlank { "Tú" }
        return true
    }

    override fun joinChampionship(alias: String, championship: Championship, category: String): Boolean {
        notifications.add(0, AppNotification(
            "Inscripción confirmada",
            "$alias se unió a ${championship.name} ($category)",
            NotificationType.CHAMPIONSHIP, "Ahora"
        ))
        return true
    }

    override fun saveRaceResult(result: RaceResult) {
        val before = getStandings().first { it.isPlayer }.position
        playerPoints += result.pointsEarned
        val after = getStandings().first { it.isPlayer }.position
        notifications.add(0, AppNotification(
            "¡Carrera Completada!",
            "Terminaste ${result.position}° en ${result.trackName}",
            NotificationType.RACE, "Ahora"
        ))
        if (after != before || result.pointsEarned > 0) {
            notifications.add(1, AppNotification(
                "Actualización del Campeonato",
                "Estás en el puesto #$after con $playerPoints pts",
                NotificationType.CHAMPIONSHIP, "Ahora"
            ))
        }
    }
}
