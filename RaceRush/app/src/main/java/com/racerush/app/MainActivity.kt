package com.racerush.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.racerush.app.databinding.ActivityMainBinding

/**
 * Única Activity de la app. Aloja el NavHostFragment y todas las pantallas
 * se implementan como Fragments (patrón Single-Activity + Navigation Component).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
