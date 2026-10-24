package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.data.UserRepository
import com.example.ui.AppScreen
import com.example.ui.HomeScreen
import com.example.ui.LoginScreen
import com.example.ui.SignUpScreen
import com.example.ui.UserProfile
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        JeevixApp()
      }
    }
  }
}

@Composable
fun JeevixApp(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val repository = remember { UserRepository.getInstance(context) }
  var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
  var userProfile by remember { mutableStateOf(UserProfile(name = "Aanchal", email = "aanchalbb11@gmail.com")) }

  LaunchedEffect(Unit) {
    repository.initializeDatabase()
  }

  when (currentScreen) {
    AppScreen.LOGIN -> {
      LoginScreen(
        onLoginSuccess = { loggedInProfile ->
          userProfile = loggedInProfile
          currentScreen = AppScreen.HOME
        },
        onNavigateToSignUp = {
          currentScreen = AppScreen.SIGN_UP
        },
        modifier = modifier.fillMaxSize()
      )
    }

    AppScreen.SIGN_UP -> {
      SignUpScreen(
        onSignUpSuccess = { createdProfile ->
          userProfile = createdProfile
          currentScreen = AppScreen.HOME
        },
        onNavigateToLogin = {
          currentScreen = AppScreen.LOGIN
        },
        modifier = modifier.fillMaxSize()
      )
    }

    AppScreen.HOME -> {
      HomeScreen(
        userProfile = userProfile,
        onLogout = {
          currentScreen = AppScreen.LOGIN
        },
        modifier = modifier.fillMaxSize()
      )
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun JeevixPreview() {
  MyApplicationTheme {
    JeevixApp()
  }
}
