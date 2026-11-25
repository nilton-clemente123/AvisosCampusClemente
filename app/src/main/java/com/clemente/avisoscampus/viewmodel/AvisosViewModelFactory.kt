package com.clemente.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clemente.avisoscampus.data.AvisosRepository

class AvisosViewModelFactory(private val repo: AvisosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(AvisosViewModel::class.java)) {
            "ViewModel no compatible: ${modelClass.name}"
        }
        @Suppress("UNCHECKED_CAST")
        return AvisosViewModel(repo) as T
    }
}