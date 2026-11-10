package com.racerush.app.ui.auth

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.racerush.app.R
import com.racerush.app.data.repository.FakeRaceRepository
import com.racerush.app.data.repository.RaceRepository

/** Errores por campo; null significa campo válido. */
data class AuthFormErrors(
    @StringRes val name: Int? = null,
    @StringRes val email: Int? = null,
    @StringRes val password: Int? = null,
    @StringRes val confirm: Int? = null
) {
    val isValid get() = name == null && email == null && password == null && confirm == null
}

/**
 * ViewModel de los formularios de inicio de sesión y registro.
 * Solo valida localmente; la autenticación real (Firebase Auth) llega en el siguiente avance.
 */
class AuthViewModel(
    private val repository: RaceRepository = FakeRaceRepository
) : ViewModel() {

    private val _errors = MutableLiveData(AuthFormErrors())
    val errors: LiveData<AuthFormErrors> = _errors

    private val _success = MutableLiveData(false)
    val success: LiveData<Boolean> = _success

    fun login(email: String, password: String) {
        val result = AuthFormErrors(
            email = validateEmail(email),
            password = validatePassword(password)
        )
        _errors.value = result
        if (result.isValid && repository.login(email.trim(), password)) _success.value = true
    }

    fun register(name: String, email: String, password: String, confirm: String) {
        val result = AuthFormErrors(
            name = if (name.isBlank()) R.string.error_required else null,
            email = validateEmail(email),
            password = validatePassword(password),
            confirm = when {
                confirm.isEmpty() -> R.string.error_required
                confirm != password -> R.string.error_password_match
                else -> null
            }
        )
        _errors.value = result
        if (result.isValid && repository.register(name.trim(), email.trim(), password)) _success.value = true
    }

    /** Se llama después de navegar para no repetir la navegación al volver. */
    fun onNavigated() { _success.value = false }

    private fun validateEmail(email: String): Int? = when {
        email.isBlank() -> R.string.error_required
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> R.string.error_email
        else -> null
    }

    private fun validatePassword(password: String): Int? = when {
        password.isEmpty() -> R.string.error_required
        password.length < 6 -> R.string.error_password
        else -> null
    }
}
