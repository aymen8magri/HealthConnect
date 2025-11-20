package com.example.healthconnect.ui

import androidx.lifecycle.ViewModel
import com.example.healthconnect.ui.profile.logout.LogoutUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    fun onLogoutClicked() {
        logoutUseCase.execute()
    }
}
    