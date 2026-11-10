package com.racerush.app.ui.garage

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.racerush.app.R
import com.racerush.app.databinding.FragmentGarageBinding
import com.racerush.app.ui.GameSessionViewModel

/**
 * Garaje / Selección de auto. Con playFlow = true continúa a la selección de pista;
 * con playFlow = false (botón GARAJE del menú) guarda el auto y vuelve al menú.
 */
class GarageFragment : Fragment() {

    private var _binding: FragmentGarageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GarageViewModel by viewModels()
    private val session: GameSessionViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGarageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val playFlow = arguments?.getBoolean("playFlow", true) ?: true

        binding.header.tvTitle.setText(R.string.garage_title)
        binding.header.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnSelectCar.setText(if (playFlow) R.string.select_car else R.string.save_car)

        viewModel.start(session.selectedCar.value)

        binding.btnPrev.setOnClickListener { viewModel.previous() }
        binding.btnNext.setOnClickListener { viewModel.next() }

        viewModel.currentCar.observe(viewLifecycleOwner) { car ->
            binding.tvCarName.text = "< ${car.name} >"
            binding.carBody.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), car.colorRes))
            binding.pbSpeed.progress = car.speed
            binding.pbGrip.progress = car.grip
            binding.pbBoost.progress = car.boost
            binding.tvSpeed.text = car.speed.toString()
            binding.tvGrip.text = car.grip.toString()
            binding.tvBoost.text = car.boost.toString()
        }
        viewModel.index.observe(viewLifecycleOwner) { i ->
            binding.tvCarIndex.text = "${i + 1} / ${viewModel.cars.size}"
        }

        binding.btnSelectCar.setOnClickListener {
            val car = viewModel.currentCar.value ?: return@setOnClickListener
            session.selectCar(car)
            if (playFlow) {
                findNavController().navigate(R.id.action_garage_to_track)
            } else {
                Toast.makeText(requireContext(), getString(R.string.car_saved, car.name), Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
