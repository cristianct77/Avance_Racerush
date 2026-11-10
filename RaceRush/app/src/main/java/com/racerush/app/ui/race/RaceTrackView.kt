package com.racerush.app.ui.race

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.racerush.app.R

/**
 * Vista personalizada que dibuja la pista vista desde arriba (como el mockup):
 * asfalto, bordes, línea central amarilla discontinua, el auto del jugador y los rivales.
 * No contiene lógica: solo pinta el último [RaceState] recibido.
 */
class RaceTrackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var state = RaceState()
    private var playerColor = ContextCompat.getColor(context, R.color.rr_red)

    private val asphalt = Paint().apply { color = ContextCompat.getColor(context, R.color.rr_asphalt) }
    private val grass = Paint().apply { color = Color.parseColor("#2E7D32") }
    private val edge = Paint().apply { color = Color.WHITE }
    private val dash = Paint().apply { color = ContextCompat.getColor(context, R.color.rr_yellow) }
    private val carPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glass = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#CC15151B") }
    private val finishWhite = Paint().apply { color = Color.WHITE }
    private val finishBlack = Paint().apply { color = Color.BLACK }
    private val rect = RectF()

    private val rivalColors = intArrayOf(
        ContextCompat.getColor(context, R.color.rr_yellow),
        ContextCompat.getColor(context, R.color.rr_blue),
        ContextCompat.getColor(context, R.color.rr_green),
        Color.parseColor("#F57C00"),
        Color.parseColor("#E0E0E0")
    )

    fun setPlayerColor(color: Int) { playerColor = color; invalidate() }

    fun render(newState: RaceState) {
        state = newState
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w == 0f || h == 0f) return

        val pxPerMeter = h / VISIBLE_METERS
        val roadLeft = w * ROAD_MARGIN
        val roadRight = w * (1 - ROAD_MARGIN)

        // Fondo y asfalto
        canvas.drawRect(0f, 0f, w, h, grass)
        canvas.drawRect(roadLeft, 0f, roadRight, h, asphalt)

        // Bordes
        val edgeW = w * 0.012f
        canvas.drawRect(roadLeft, 0f, roadLeft + edgeW, h, edge)
        canvas.drawRect(roadRight - edgeW, 0f, roadRight, h, edge)

        // Línea central discontinua que se desplaza con la distancia recorrida
        val dashLen = 6f * pxPerMeter
        val period = 14f * pxPerMeter
        val offset = (state.distanceMeters * pxPerMeter) % period
        val dashW = w * 0.018f
        var y = -period + offset
        while (y < h) {
            canvas.drawRect(w / 2 - dashW / 2, y, w / 2 + dashW / 2, y + dashLen, dash)
            y += period
        }

        val playerY = h * PLAYER_Y

        // Línea de meta / salida al inicio de cada vuelta
        val lapPos = state.distanceMeters % RaceViewModel.LAP_LENGTH_M
        val toLine = if (lapPos < 20f) -lapPos else RaceViewModel.LAP_LENGTH_M - lapPos
        val lineY = playerY - toLine * pxPerMeter
        if (lineY in -40f..h) drawFinishLine(canvas, roadLeft, roadRight, lineY)

        // Rivales
        state.rivals.forEach { r ->
            val ry = playerY - r.relativeMeters * pxPerMeter
            drawCar(canvas, xToPx(r.x, roadLeft, roadRight), ry, w, rivalColors[r.colorIndex % rivalColors.size])
        }

        // Jugador (parpadea al chocar)
        val blink = state.crashed && (state.elapsedMs / 100) % 2 == 0L
        if (!blink) drawCar(canvas, xToPx(state.playerX, roadLeft, roadRight), playerY, w, playerColor)
    }

    private fun xToPx(x: Float, left: Float, right: Float) = left + x * (right - left)

    private fun drawCar(canvas: Canvas, cx: Float, cy: Float, w: Float, color: Int) {
        val carW = w * 0.12f
        val carH = carW * 1.75f
        carPaint.color = color
        rect.set(cx - carW / 2, cy - carH / 2, cx + carW / 2, cy + carH / 2)
        canvas.drawRoundRect(rect, carW * 0.2f, carW * 0.2f, carPaint)
        rect.set(cx - carW * 0.35f, cy - carH * 0.25f, cx + carW * 0.35f, cy - carH * 0.02f)
        canvas.drawRoundRect(rect, carW * 0.1f, carW * 0.1f, glass)
    }

    private fun drawFinishLine(canvas: Canvas, left: Float, right: Float, y: Float) {
        val squares = 12
        val size = (right - left) / squares
        for (row in 0 until 2) for (i in 0 until squares) {
            val p = if ((i + row) % 2 == 0) finishWhite else finishBlack
            canvas.drawRect(left + i * size, y + row * size, left + (i + 1) * size, y + (row + 1) * size, p)
        }
    }

    companion object {
        private const val VISIBLE_METERS = 100f
        private const val ROAD_MARGIN = 0.08f
        private const val PLAYER_Y = 0.72f
    }
}
