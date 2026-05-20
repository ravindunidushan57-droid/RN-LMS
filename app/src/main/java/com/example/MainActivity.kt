package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LmsMainContent
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LmsViewModel
import com.example.viewmodel.LmsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: LmsViewModel = viewModel()
      val themeMode = viewModel.themeMode.collectAsState()
      val isDarkTheme = themeMode.value == LmsTheme.Dark

      MyApplicationTheme(darkTheme = isDarkTheme) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          LmsMainContent(viewModel = viewModel)
        }
      }
    }
  }
}

