package com.racerush.app.ui.results

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.racerush.app.R
import com.racerush.app.databinding.FragmentRaceResultBinding
import com.racerush.app.ui.GameSessionViewModel
import com.racerush.app.ui.race.RaceViewModel

/** Resultado de la carrera (pantalla nueva respecto a los mockups). */
class RaceResultFragment : Fragment() {

    private var _binding: FragmentRaceResultBinding? = null
    private val binding get() = _binding!!
    private val session: GameSessionViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRaceResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session.lastResult.observe(viewLifecycleOwner) { r ->
            if (r == null) return@observe
            binding.tvSummary.text = getString(R.string.result_summary, r.position, r.trackName)
            binding.tvPositionValue.text = "${r.position}°"
            binding.tvTimeValue.text = RaceViewModel.formatTime(r.timeMillis)
            binding.tvPointsValue.text = getString(R.string.result_points_value, r.pointsEarned)
        }

        val nav = findNavController()
        binding.btnViewChampionship.setOnClickListener { nav.navigate(R.id.action_result_to_championship) }
        binding.btnRaceAgain.setOnClickListener { nav.navigate(R.id.action_result_to_race) }
        binding.btnBackToMenu.setOnClickListener { nav.popBackStack(R.id.mainMenuFragment, false) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
