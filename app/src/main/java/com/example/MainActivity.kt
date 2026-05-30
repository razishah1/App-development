package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.MosqueRepository
import com.example.ui.MosqueFinanceAppShell
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(this)
    val dao = database.mosqueDao()
    val repository = MosqueRepository(dao)

    setContent {
      MyApplicationTheme {
        // Instantiate the ViewModel securely using custom Factory directly
        val vm: FinanceViewModel = viewModel(
          factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
              return FinanceViewModel(repository) as T
            }
          }
        )
        MosqueFinanceAppShell(viewModel = vm)
      }
    }
  }
}
