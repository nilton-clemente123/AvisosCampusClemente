package com.clemente.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.clemente.avisoscampus.data.AvisosRepositoryMemoria
import com.clemente.avisoscampus.ui.PantallaAvisos
import com.clemente.avisoscampus.viewmodel.AvisosViewModel
import com.clemente.avisoscampus.viewmodel.AvisosViewModelFactory
import com.clemente.avisoscampus.ui.theme.AvisosCampusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvisosCampusTheme {
                val vm: AvisosViewModel = viewModel(
                    factory = AvisosViewModelFactory(AvisosRepositoryMemoria())
                )
                PantallaAvisos(vm)
            }
        }
    }
}
