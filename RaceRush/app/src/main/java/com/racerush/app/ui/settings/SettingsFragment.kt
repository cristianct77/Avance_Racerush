package com.racerush.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.racerush.app.R
import com.racerush.app.databinding.FragmentSettingsBinding
import com.racerush.app.ui.GameSessionViewModel

/** Ajustes: audio, vibración, idioma (sigue al dispositivo) y cierre de sesión. */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val session: GameSessionViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.header.tvTitle.setText(R.string.settings_title)
        binding.header.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.swSound.isChecked = session.soundEnabled
        binding.swMusic.isChecked = session.musicEnabled
        binding.swVibration.isChecked = session.vibrationEnabled
        binding.swSound.setOnCheckedChangeListener { _, v -> session.soundEnabled = v }
        binding.swMusic.setOnCheckedChangeListener { _, v -> session.musicEnabled = v }
        binding.swVibration.setOnCheckedChangeListener { _, v -> session.vibrationEnabled = v }

        binding.tvPlayer.text = session.playerName
        val version = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName ?: "0.2.0"
        binding.tvVersion.text = getString(R.string.settings_version, version)

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_login)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
