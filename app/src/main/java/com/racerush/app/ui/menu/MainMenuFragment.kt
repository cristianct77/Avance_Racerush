package com.racerush.app.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
        // Los botones se conectan en los siguientes commits:
        // CAMPEONATO, AJUSTES y notificaciones (estudiante 2); JUGAR y GARAJE (estudiante 3).
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
