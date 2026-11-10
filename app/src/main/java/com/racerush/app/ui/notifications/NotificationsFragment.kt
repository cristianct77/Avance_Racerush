package com.racerush.app.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.racerush.app.R
import com.racerush.app.data.model.AppNotification
import com.racerush.app.data.repository.FakeRaceRepository
import com.racerush.app.data.repository.RaceRepository
import com.racerush.app.databinding.FragmentNotificationsBinding

class NotificationsViewModel(
    private val repository: RaceRepository = FakeRaceRepository
) : ViewModel() {
    private val _items = MutableLiveData<List<AppNotification>>()
    val items: LiveData<List<AppNotification>> = _items
    fun load() { _items.value = repository.getNotifications() }
}

/** Notificaciones dentro de la app (resultados de carrera y del campeonato). */
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.header.tvTitle.setText(R.string.notifications_title)
        binding.header.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())

        viewModel.items.observe(viewLifecycleOwner) { list ->
            binding.rvNotifications.adapter = NotificationsAdapter(list)
            binding.tvEmpty.isVisible = list.isEmpty()
        }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
