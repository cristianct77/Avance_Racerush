package com.racerush.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.racerush.app.R
import com.racerush.app.databinding.FragmentRegisterBinding

/** Formulario 1 (parte B): registro de un nuevo piloto. */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.header.tvTitle.setText(R.string.register_title)
        binding.header.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnGoLogin.setOnClickListener { findNavController().navigateUp() }

        binding.btnRegister.setOnClickListener {
            viewModel.register(
                binding.etName.text?.toString().orEmpty(),
                binding.etEmail.text?.toString().orEmpty(),
                binding.etPassword.text?.toString().orEmpty(),
                binding.etConfirm.text?.toString().orEmpty()
            )
        }

        viewModel.errors.observe(viewLifecycleOwner) { e ->
            binding.tilName.error = e.name?.let { getString(it) }
            binding.tilEmail.error = e.email?.let { getString(it) }
            binding.tilPassword.error = e.password?.let { getString(it) }
            binding.tilConfirm.error = e.confirm?.let { getString(it) }
        }
        viewModel.success.observe(viewLifecycleOwner) { ok ->
            if (ok) {
                viewModel.onNavigated()
                findNavController().navigate(R.id.action_register_to_menu)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
