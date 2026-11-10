package com.racerush.app.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.racerush.app.R
import com.racerush.app.databinding.FragmentMainMenuBinding

/** Menú principal: JUGAR, CAMPEONATO, GARAJE, AJUSTES + acceso a notificaciones. */
class MainMenuFragment : Fragment() {

    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nav = findNavController()
        // JUGAR: Garaje -> Pista -> Carrera
        binding.btnPlay.setOnClickListener {
            nav.navigate(R.id.action_menu_to_garage, bundleOf("playFlow" to true))
        }
        // GARAJE: solo elegir/guardar el auto y volver
        binding.btnGarage.setOnClickListener {
            nav.navigate(R.id.action_menu_to_garage, bundleOf("playFlow" to false))
        }
        binding.btnChampionship.setOnClickListener { nav.navigate(R.id.action_menu_to_championship) }
        binding.btnSettings.setOnClickListener { nav.navigate(R.id.action_menu_to_settings) }
        binding.btnNotifications.setOnClickListener { nav.navigate(R.id.action_menu_to_notifications) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
