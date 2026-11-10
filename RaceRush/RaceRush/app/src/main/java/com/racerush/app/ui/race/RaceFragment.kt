package com.racerush.app.ui.race

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.racerush.app.R
import com.racerush.app.databinding.FragmentRaceBinding
import com.racerush.app.ui.GameSessionViewModel

/** Jugabilidad (HUD de carrera): vuelta, posición, velocidad y controles. */
class RaceFragment : Fragment() {

    private var _binding: FragmentRaceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RaceViewModel by viewModels()
    private val session: GameSessionViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val car = session.selectedCar.value ?: return
        val track = session.selectedTrack.value ?: return

        binding.raceTrackView.setPlayerColor(ContextCompat.getColor(requireContext(), car.colorRes))
        bindSteering(binding.btnLeft, -1)
        bindSteering(binding.btnRight, 1)
        binding.btnQuit.setOnClickListener { findNavController().navigateUp() }

        viewModel.start(car, track)

        viewModel.state.observe(viewLifecycleOwner) { s ->
            binding.raceTrackView.render(s)
            binding.tvLap.text = getString(R.string.hud_lap, s.lap, s.totalLaps)
            binding.tvPosition.text = getString(R.string.hud_position, s.position, s.totalRacers)
            binding.tvSpeed.text = getString(R.string.hud_speed, s.speedKmh.toInt())
            binding.tvTimer.text = RaceViewModel.formatTime(s.elapsedMs)
            binding.tvCountdown.text = when {
                s.phase == RacePhase.COUNTDOWN -> s.countdown.toString()
                s.showGo -> getString(R.string.countdown_go)
                else -> ""
            }
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                viewModel.consumeResult()
                session.finishRace(result)
                val nav = findNavController()
                if (nav.currentDestination?.id == R.id.raceFragment) {
                    nav.navigate(R.id.action_race_to_result)
                }
            }
        }
    }

    /** Mantener presionado para girar; soltar para ir recto. */
    @SuppressLint("ClickableViewAccessibility")
    private fun bindSteering(button: View, direction: Int) {
        button.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> { v.isPressed = true; viewModel.setSteering(direction) }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.isPressed = false
                    viewModel.setSteering(0)
                    if (event.actionMasked == MotionEvent.ACTION_UP) v.performClick()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPaused(false)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setPaused(true)
        viewModel.setSteering(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
