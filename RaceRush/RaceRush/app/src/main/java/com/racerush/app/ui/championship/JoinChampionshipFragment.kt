package com.racerush.app.ui.championship

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.racerush.app.R
import com.racerush.app.data.model.Championship
import com.racerush.app.databinding.FragmentJoinChampionshipBinding

/** Formulario 2: unirse a una carrera o campeonato. */
class JoinChampionshipFragment : Fragment() {

    private var _binding: FragmentJoinChampionshipBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChampionshipViewModel by viewModels()
    private var selected: Championship? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJoinChampionshipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.header.tvTitle.setText(R.string.join_title)
        binding.header.btnBack.setOnClickListener { findNavController().navigateUp() }

        val options = viewModel.championships
        binding.actChampionship.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)
        )
        binding.actChampionship.setOnItemClickListener { _, _, position, _ ->
            selected = options[position]
            binding.tilChampionship.error = null
        }

        binding.btnConfirm.setOnClickListener { submit() }

        viewModel.joined.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                viewModel.onJoinHandled()
                Toast.makeText(requireContext(), R.string.join_success, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun submit() {
        val alias = binding.etAlias.text?.toString().orEmpty()
        var valid = true

        binding.tilAlias.error = if (alias.isBlank()) { valid = false; getString(R.string.error_required) } else null
        binding.tilChampionship.error = if (selected == null) { valid = false; getString(R.string.error_required) } else null
        if (!binding.cbRules.isChecked) {
            valid = false
            Toast.makeText(requireContext(), R.string.error_rules, Toast.LENGTH_SHORT).show()
        }
        if (!valid) return

        val category = getString(
            if (binding.rgCategory.checkedRadioButtonId == R.id.rbPro) R.string.category_pro
            else R.string.category_amateur
        )
        viewModel.join(alias, selected!!, category)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
