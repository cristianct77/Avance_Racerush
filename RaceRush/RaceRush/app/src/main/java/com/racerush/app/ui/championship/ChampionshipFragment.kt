package com.racerush.app.ui.championship

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.racerush.app.R
import com.racerush.app.databinding.FragmentChampionshipBinding

/** Clasificación del campeonato ordenada por puntos. */
class ChampionshipFragment : Fragment() {

    private var _binding: FragmentChampionshipBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChampionshipViewModel by viewModels()
    private val adapter = StandingsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChampionshipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.header.tvTitle.setText(R.string.championship_title)
        binding.header.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.rvStandings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStandings.adapter = adapter
        viewModel.standings.observe(viewLifecycleOwner) { adapter.submit(it) }

        binding.btnJoin.setOnClickListener {
            findNavController().navigate(R.id.action_championship_to_join)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadStandings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
