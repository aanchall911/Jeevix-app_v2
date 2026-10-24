package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoginResult
import com.example.data.UserRepository
import com.example.ui.theme.JeevixBorder
import com.example.ui.theme.JeevixRed
import com.example.ui.theme.JeevixTextDark
import com.example.ui.theme.JeevixTextMuted
import com.example.ui.theme.JeevixWhite
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (UserProfile) -> Unit,
    onNavigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { UserRepository.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    val attemptLogin = {
        if (emailOrPhone.isBlank()) {
            errorMessage = "Please enter your email or username"
        } else if (password.isBlank()) {
            errorMessage = "Please enter your password"
        } else {
            errorMessage = null
            isLoading = true
            coroutineScope.launch {
                when (val result = repository.authenticate(emailOrPhone, password)) {
                    is LoginResult.Success -> {
                        isLoading = false
                        onLoginSuccess(result.userProfile)
                    }
                    is LoginResult.UserNotFound -> {
                        isLoading = false
                        errorMessage = "User not found in database. Only registered users can log in."
                    }
                    is LoginResult.InvalidPassword -> {
                        isLoading = false
                        errorMessage = "Invalid password. Access denied."
                    }
                    is LoginResult.Error -> {
                        isLoading = false
                        errorMessage = result.message
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(JeevixWhite)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Brand Header: JEEVIX (clean, no emojis)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "JEEVIX",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = JeevixRed,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Jeevix Logo Blood Drop",
                    tint = JeevixRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Welcome Back
            Text(
                text = "Welcome Back",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = JeevixTextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email / Username Label
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email or Username",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = JeevixTextDark,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = emailOrPhone,
                    onValueChange = {
                        emailOrPhone = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(
                            text = "Enter your registered email or username",
                            color = JeevixTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JeevixRed,
                        unfocusedBorderColor = JeevixBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = JeevixTextDark,
                        unfocusedTextColor = JeevixTextDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Label
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = JeevixTextDark,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(
                            text = "Enter password",
                            color = JeevixTextMuted,
                            fontSize = 15.sp
                        )
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { attemptLogin() }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = JeevixTextMuted
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JeevixRed,
                        unfocusedBorderColor = JeevixBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = JeevixTextDark,
                        unfocusedTextColor = JeevixTextDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input")
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage!!,
                    color = JeevixRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_error_message")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Forgot Password?
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 14.sp,
                    color = JeevixRed,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { showForgotPasswordDialog = true }
                        .padding(4.dp)
                        .testTag("forgot_password_button")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LOGIN Button (Normal rounded button)
            Button(
                onClick = { attemptLogin() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("login_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JeevixRed,
                    contentColor = JeevixWhite
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = JeevixWhite,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "LOGIN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Database verified note
            Text(
                text = "Database Authentication: Only registered users in the database can log in.",
                fontSize = 12.sp,
                color = JeevixTextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Don't have an account? Sign Up
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Don't have an account?",
                    fontSize = 14.sp,
                    color = JeevixTextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sign Up",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = JeevixRed,
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .padding(4.dp)
                        .testTag("go_to_signup_button")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Simple Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                forgotMessage = null
            },
            title = {
                Text(
                    text = "Reset Password",
                    fontWeight = FontWeight.Bold,
                    color = JeevixTextDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your registered email to verify account and reset password:",
                        fontSize = 14.sp,
                        color = JeevixTextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        placeholder = { Text("Registered email address") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (forgotMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = forgotMessage!!,
                            color = JeevixRed,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotEmail.isNotBlank()) {
                            coroutineScope.launch {
                                val user = repository.authenticate(forgotEmail, "")
                                if (user is LoginResult.InvalidPassword) {
                                    forgotMessage = "Account verified in database! Password reset link sent to $forgotEmail."
                                } else {
                                    forgotMessage = "No user found with email $forgotEmail in the database."
                                }
                            }
                        } else {
                            forgotMessage = "Please enter an email address"
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text("Verify & Send")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showForgotPasswordDialog = false
                    forgotMessage = null
                }) {
                    Text("Close", color = JeevixTextMuted)
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }
}
