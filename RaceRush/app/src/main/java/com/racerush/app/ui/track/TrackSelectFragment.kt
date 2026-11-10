package com.racerush.app.ui.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.racerush.app.R
import com.racerush.app.databinding.FragmentTrackSelectBinding
import com.racerush.app.ui.GameSessionViewModel

/** Selección de pista antes de iniciar la carrera. */
class TrackSelectFragment : Fragment() {

    private var _binding: FragmentTrackSelectBinding? = null
    private val binding get() = _binding!!
    private val session: GameSessionViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrackSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.header.tvTitle.setText(R.string.track_title)
        binding.header.btnBack.setOnClickListener { findNavController().navigateUp() }

        val adapter = TrackAdapter(session.tracks) { session.selectTrack(it) }
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = adapter

        session.selectedTrack.observe(viewLifecycleOwner) { adapter.setSelected(it.id) }

        binding.btnStartRace.setOnClickListener {
            findNavController().navigate(R.id.action_track_to_race)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
