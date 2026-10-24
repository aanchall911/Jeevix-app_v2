package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.SignUpResult
import com.example.data.UserRepository
import com.example.ui.theme.JeevixBorder
import com.example.ui.theme.JeevixDivider
import com.example.ui.theme.JeevixRed
import com.example.ui.theme.JeevixTextDark
import com.example.ui.theme.JeevixTextMuted
import com.example.ui.theme.JeevixWhite
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onSignUpSuccess: (UserProfile) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { UserRepository.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    // Account credentials
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Donor & Medical Profile details
    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
    var selectedBloodGroup by remember { mutableStateOf("O+") }
    var emergencyContact by remember { mutableStateOf("") }
    var lastDonationOption by remember { mutableStateOf("First-time donor") }
    var customLastDonationDate by remember { mutableStateOf("") }
    var isDonorAvailable by remember { mutableStateOf(true) }

    var agreedToTerms by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val attemptSignUp = {
        val finalLastDonation = if (lastDonationOption == "Custom Date") {
            customLastDonationDate.trim().ifBlank { "Never" }
        } else {
            lastDonationOption
        }

        if (fullName.isBlank()) {
            errorMessage = "Please enter your full name"
        } else if (email.isBlank()) {
            errorMessage = "Please enter your email"
        } else if (!email.contains("@") || !email.contains(".")) {
            errorMessage = "Please enter a valid email address"
        } else if (password.isBlank()) {
            errorMessage = "Please enter a password"
        } else if (password.length < 4) {
            errorMessage = "Password must be at least 4 characters"
        } else if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
        } else if (emergencyContact.isBlank()) {
            errorMessage = "Please provide an emergency contact number"
        } else if (lastDonationOption == "Custom Date" && customLastDonationDate.isBlank()) {
            errorMessage = "Please enter your last donation date or select 'First-time donor'"
        } else if (!agreedToTerms) {
            errorMessage = "Please agree to the Terms to continue"
        } else {
            errorMessage = null
            isLoading = true
            coroutineScope.launch {
                val donorStatus = if (isDonorAvailable) "Available" else "Resting"
                when (val result = repository.register(
                    name = fullName,
                    email = email,
                    password = password,
                    bloodGroup = selectedBloodGroup,
                    emergencyContact = emergencyContact,
                    lastDonationDate = finalLastDonation,
                    donorStatus = donorStatus
                )) {
                    is SignUpResult.Success -> {
                        isLoading = false
                        onSignUpSuccess(result.userProfile)
                    }
                    is SignUpResult.UserAlreadyExists -> {
                        isLoading = false
                        errorMessage = "An account with this email already exists in the database. Please log in."
                    }
                    is SignUpResult.Error -> {
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
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Brand Header: JEEVIX
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
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Donor Registration & Account Setup",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = JeevixTextDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Set up your medical credentials & emergency profile",
                fontSize = 13.sp,
                color = JeevixTextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECTION 1: ACCOUNT CREDENTIALS ---
            Text(
                text = "1. Account Credentials",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = JeevixRed,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Full Name
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Full Name",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = JeevixTextDark,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(text = "e.g. Aanchal Sharma", color = JeevixTextMuted, fontSize = 14.sp)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
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
                        .testTag("fullname_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email Address",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = JeevixTextDark,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(text = "name@example.com", color = JeevixTextMuted, fontSize = 14.sp)
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
                        .testTag("signup_email_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = JeevixTextDark,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(text = "Enter secure password", color = JeevixTextMuted, fontSize = 14.sp)
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
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
                        .testTag("signup_password_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Confirm Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = JeevixTextDark,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(text = "Confirm password", color = JeevixTextMuted, fontSize = 14.sp)
                    },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
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
                        .testTag("signup_confirm_password_input")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = JeevixDivider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 2: DONOR & MEDICAL PROFILE ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Bloodtype,
                    contentDescription = null,
                    tint = JeevixRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "2. Blood Donor & Medical Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = JeevixRed
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Blood Group Selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select Blood Group",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = JeevixTextDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 2 rows of 4 blood groups
                val row1 = bloodGroups.take(4)
                val row2 = bloodGroups.takeLast(4)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row1.forEach { bg ->
                        val isSelected = selectedBloodGroup == bg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .background(
                                    color = if (isSelected) JeevixRed else Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) JeevixRed else JeevixBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedBloodGroup = bg }
                                .testTag("blood_group_$bg"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bg,
                                color = if (isSelected) Color.White else JeevixTextDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row2.forEach { bg ->
                        val isSelected = selectedBloodGroup == bg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .background(
                                    color = if (isSelected) JeevixRed else Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) JeevixRed else JeevixBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedBloodGroup = bg }
                                .testTag("blood_group_$bg"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bg,
                                color = if (isSelected) Color.White else JeevixTextDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emergency Contact Number
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = JeevixRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Emergency Contact Number",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = JeevixTextDark
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = {
                        emergencyContact = it
                        errorMessage = null
                    },
                    placeholder = {
                        Text(text = "+91 98765 43210 (Kin / Doctor / Guardian)", color = JeevixTextMuted, fontSize = 13.sp)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
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
                        .testTag("emergency_contact_input")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Last Blood Donation Date
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = JeevixRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Last Blood Donation Date",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = JeevixTextDark
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Quick selector buttons
                val presets = listOf("First-time donor", "3+ months ago", "Custom Date")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = lastDonationOption == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .background(
                                    color = if (isSelected) JeevixRed.copy(alpha = 0.12f) else Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) JeevixRed else JeevixBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { lastDonationOption = preset }
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset,
                                color = if (isSelected) JeevixRed else JeevixTextDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (lastDonationOption == "Custom Date") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customLastDonationDate,
                        onValueChange = {
                            customLastDonationDate = it
                            errorMessage = null
                        },
                        placeholder = {
                            Text(text = "e.g. 15 Jan 2026 or DD/MM/YYYY", color = JeevixTextMuted, fontSize = 13.sp)
                        },
                        singleLine = true,
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
                            .testTag("custom_donation_date_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Initial Donor Availability Status Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, JeevixBorder, shape = RoundedCornerShape(8.dp))
                    .clickable { isDonorAvailable = !isDonorAvailable }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Checkbox(
                    checked = isDonorAvailable,
                    onCheckedChange = { isDonorAvailable = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = JeevixRed,
                        checkmarkColor = JeevixWhite,
                        uncheckedColor = JeevixBorder
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isDonorAvailable) "Available for Emergency Donations" else "Resting / Currently Unavailable",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = JeevixTextDark
                    )
                    Text(
                        text = if (isDonorAvailable) "Hospitals & patients can request blood assistance" else "You can enable availability anytime from your profile",
                        fontSize = 11.sp,
                        color = JeevixTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Checkbox: I agree to Terms
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreedToTerms = !agreedToTerms }
                    .padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = JeevixRed,
                        checkmarkColor = JeevixWhite,
                        uncheckedColor = JeevixBorder
                    ),
                    modifier = Modifier.testTag("terms_checkbox")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "I agree to ",
                    fontSize = 13.sp,
                    color = JeevixTextDark
                )
                Text(
                    text = "Terms & Donor Code of Conduct",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = JeevixRed,
                    modifier = Modifier
                        .clickable { showTermsDialog = true }
                        .padding(2.dp)
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
                        .testTag("signup_error_message")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SET UP ACCOUNT & COMPLETE REGISTRATION Button
            Button(
                onClick = { attemptSignUp() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("create_account_button"),
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
                        text = "SET UP ACCOUNT & REGISTER",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Already have an account? Login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account?",
                    fontSize = 14.sp,
                    color = JeevixTextMuted
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Login",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = JeevixRed,
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(4.dp)
                        .testTag("go_to_login_button")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = "Terms & Donor Conduct",
                    fontWeight = FontWeight.Bold,
                    color = JeevixTextDark
                )
            },
            text = {
                Text(
                    text = "By creating an account on Jeevix, you confirm that all medical details (blood group, previous donation history, and emergency contact) are authentic. Your donor passport will be generated to coordinate life-saving blood assistance.",
                    fontSize = 14.sp,
                    color = JeevixTextMuted,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        agreedToTerms = true
                        showTermsDialog = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close", color = JeevixTextMuted)
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }
}
