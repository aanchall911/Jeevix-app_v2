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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.FacilityType
import com.example.ui.theme.JeevixBorder
import com.example.ui.theme.JeevixDivider
import com.example.ui.theme.JeevixRed
import com.example.ui.theme.JeevixTextDark
import com.example.ui.theme.JeevixTextMuted
import com.example.ui.theme.JeevixWhite

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(HomeTab.HOME) }
    var donorStatus by remember { mutableStateOf(userProfile.donorStatus) }

    // Dialog states
    var showPassportDialog by remember { mutableStateOf(false) }
    var showSosDialog by remember { mutableStateOf(false) }
    var showFindBloodDialog by remember { mutableStateOf(false) }
    var showHospitalDialog by remember { mutableStateOf(false) }
    var showBloodBankDialog by remember { mutableStateOf(false) }
    var showDonorDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(JeevixWhite)
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            // Bottom Bar: Home | Passport | Donate | Profile
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JeevixWhite)
            ) {
                HorizontalDivider(color = JeevixDivider, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home Tab
                    BottomNavItem(
                        label = "Home",
                        icon = if (selectedTab == HomeTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                        isSelected = selectedTab == HomeTab.HOME,
                        onClick = { selectedTab = HomeTab.HOME },
                        testTag = "tab_home"
                    )

                    // Passport Tab
                    BottomNavItem(
                        label = "Passport",
                        icon = if (selectedTab == HomeTab.PASSPORT) Icons.Filled.Badge else Icons.Outlined.Badge,
                        isSelected = selectedTab == HomeTab.PASSPORT,
                        onClick = { selectedTab = HomeTab.PASSPORT },
                        testTag = "tab_passport"
                    )

                    // Donate Tab
                    BottomNavItem(
                        label = "Donate",
                        icon = if (selectedTab == HomeTab.DONATE) Icons.Filled.Bloodtype else Icons.Outlined.Bloodtype,
                        isSelected = selectedTab == HomeTab.DONATE,
                        onClick = { selectedTab = HomeTab.DONATE },
                        testTag = "tab_donate"
                    )

                    // Profile Tab
                    BottomNavItem(
                        label = "Profile",
                        icon = if (selectedTab == HomeTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                        isSelected = selectedTab == HomeTab.PROFILE,
                        onClick = { selectedTab = HomeTab.PROFILE },
                        testTag = "tab_profile"
                    )
                }
            }
        },
        containerColor = JeevixWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(JeevixWhite)
        ) {
            when (selectedTab) {
                HomeTab.HOME -> {
                    HomeContent(
                        userName = userProfile.name,
                        bloodGroup = userProfile.bloodGroup,
                        donorStatus = donorStatus,
                        onViewPassport = { showPassportDialog = true },
                        onSosClick = { showSosDialog = true },
                        onFindBloodClick = { showFindBloodDialog = true },
                        onHospitalClick = { showHospitalDialog = true },
                        onBloodBankClick = { showBloodBankDialog = true },
                        onDonorClick = { showDonorDialog = true },
                        onAskJeevixClick = { showAiDialog = true },
                        onNotificationClick = { showNotificationDialog = true }
                    )
                }
                HomeTab.PASSPORT -> {
                    PassportTabContent(
                        userName = userProfile.name,
                        bloodGroup = userProfile.bloodGroup,
                        donorStatus = donorStatus,
                        donorId = userProfile.donorId,
                        donationsCount = userProfile.donationsCount,
                        onToggleStatus = {
                            donorStatus = if (donorStatus == "Available") "Busy" else "Available"
                        }
                    )
                }
                HomeTab.DONATE -> {
                    DonateTabContent(
                        bloodGroup = userProfile.bloodGroup,
                        onRegisterDonor = { showDonorDialog = true }
                    )
                }
                HomeTab.PROFILE -> {
                    ProfileTabContent(
                        userProfile = userProfile,
                        donorStatus = donorStatus,
                        onToggleStatus = {
                            donorStatus = if (donorStatus == "Available") "Busy" else "Available"
                        },
                        onLogout = onLogout
                    )
                }
            }
        }
    }

    // Health Passport Dialog
    if (showPassportDialog) {
        AlertDialog(
            onDismissRequest = { showPassportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = JeevixRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Digital Health Passport",
                        fontWeight = FontWeight.Bold,
                        color = JeevixTextDark,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(label = "Donor Name", value = userProfile.name)
                    DetailRow(label = "Donor ID", value = userProfile.donorId)
                    DetailRow(label = "Blood Group", value = userProfile.bloodGroup)
                    DetailRow(label = "Status", value = donorStatus)
                    DetailRow(label = "Total Donations", value = "${userProfile.donationsCount} times")
                    DetailRow(label = "Last Donated", value = userProfile.lastDonationDate)
                    DetailRow(label = "Next Eligible", value = "Eligible Now")
                    DetailRow(label = "Emergency Contact", value = userProfile.emergencyContact)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        donorStatus = if (donorStatus == "Available") "Busy" else "Available"
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text(if (donorStatus == "Available") "Set to Busy" else "Set to Available")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPassportDialog = false }) {
                    Text("Close", color = JeevixTextMuted)
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }

    // Emergency SOS Dialog
    if (showSosDialog) {
        var sosSent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                showSosDialog = false
                sosSent = false
            },
            title = {
                Text(
                    text = "Emergency Assistance",
                    fontWeight = FontWeight.Bold,
                    color = JeevixRed,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Emergency Helplines:",
                        fontWeight = FontWeight.SemiBold,
                        color = JeevixTextDark,
                        fontSize = 14.sp
                    )
                    Text(text = "• Ambulance: 108", color = JeevixTextDark, fontSize = 14.sp)
                    Text(text = "• Police / Emergency: 112", color = JeevixTextDark, fontSize = 14.sp)
                    Text(text = "• Blood Helpline: 104", color = JeevixTextDark, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(6.dp))

                    if (sosSent) {
                        Text(
                            text = "Emergency broadcast sent to nearby donors and hospitals for ${userProfile.bloodGroup} blood.",
                            color = JeevixRed,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = "Tap Broadcast to immediately request urgent ${userProfile.bloodGroup} blood assistance.",
                            color = JeevixTextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { sosSent = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text(if (sosSent) "Broadcast Sent" else "Broadcast SOS")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSosDialog = false
                    sosSent = false
                }) {
                    Text("Close", color = JeevixTextMuted)
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }

    // Find Blood Dialog
    if (showFindBloodDialog) {
        var selectedGroup by remember { mutableStateOf("O+") }
        val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

        AlertDialog(
            onDismissRequest = { showFindBloodDialog = false },
            title = {
                Text(
                    text = "Find Blood Units",
                    fontWeight = FontWeight.Bold,
                    color = JeevixTextDark,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select Blood Group:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = JeevixTextDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bloodGroups.take(4).forEach { group ->
                            Button(
                                onClick = { selectedGroup = group },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedGroup == group) JeevixRed else JeevixDivider,
                                    contentColor = if (selectedGroup == group) JeevixWhite else JeevixTextDark
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(group, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bloodGroups.drop(4).forEach { group ->
                            Button(
                                onClick = { selectedGroup = group },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedGroup == group) JeevixRed else JeevixDivider,
                                    contentColor = if (selectedGroup == group) JeevixWhite else JeevixTextDark
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(group, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Available $selectedGroup Units Nearby:",
                        fontWeight = FontWeight.SemiBold,
                        color = JeevixTextDark,
                        fontSize = 13.sp
                    )
                    Text("• City Blood Bank: 12 Units available", fontSize = 13.sp, color = JeevixTextDark)
                    Text("• Red Cross Center: 7 Units available", fontSize = 13.sp, color = JeevixTextDark)
                    Text("• Rotary Blood Bank: 4 Units available", fontSize = 13.sp, color = JeevixTextDark)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showFindBloodDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text("Done")
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }

    // Hospital Dialog with Google Geolocation
    if (showHospitalDialog) {
        FacilityLocationDialog(
            type = FacilityType.HOSPITAL,
            onDismiss = { showHospitalDialog = false }
        )
    }

    // Blood Bank Dialog with Google Geolocation
    if (showBloodBankDialog) {
        FacilityLocationDialog(
            type = FacilityType.BLOOD_BANK,
            onDismiss = { showBloodBankDialog = false }
        )
    }

    // Donor Dialog
    if (showDonorDialog) {
        var registeredMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = {
                showDonorDialog = false
                registeredMessage = null
            },
            title = {
                Text(
                    text = "Blood Donors",
                    fontWeight = FontWeight.Bold,
                    color = JeevixTextDark,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Verified Donors Nearby:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("• Rohit S. (O+) — Available (Last donated 5 mos ago)", fontSize = 13.sp)
                    Text("• Priya M. (B+) — Available (Last donated 4 mos ago)", fontSize = 13.sp)
                    Text("• Aman K. (A+) — Available (Last donated 6 mos ago)", fontSize = 13.sp)
                    Text("• Sneha D. (AB+) — Available (Last donated 3 mos ago)", fontSize = 13.sp)

                    if (registeredMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = registeredMessage!!,
                            color = JeevixRed,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        registeredMessage = "Your donor profile is active and verified as $donorStatus."
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text("My Donor Status")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDonorDialog = false
                    registeredMessage = null
                }) {
                    Text("Close", color = JeevixTextMuted)
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }

    // Ask Jeevix AI Dialog
    if (showAiDialog) {
        var questionText by remember { mutableStateOf("") }
        var aiAnswer by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = {
                showAiDialog = false
                questionText = ""
                aiAnswer = null
            },
            title = {
                Text(
                    text = "Jeevix AI",
                    fontWeight = FontWeight.Bold,
                    color = JeevixTextDark,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "How can I help you?",
                        fontSize = 14.sp,
                        color = JeevixTextDark,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        placeholder = { Text("e.g. Can I donate blood after cold?") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (aiAnswer != null) {
                        Text(
                            text = aiAnswer!!,
                            color = JeevixTextDark,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(
                            text = "Note: AI integration will be available in Version 2. For urgent requirements, please use the SOS emergency button.",
                            fontSize = 12.sp,
                            color = JeevixTextMuted
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionText.isNotBlank()) {
                            aiAnswer = "Thank you for asking! General rule: You should be fully recovered from any cold/fever for at least 48 hours before donating blood. Full automated AI responses will activate in Version 2."
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text("Ask")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAiDialog = false
                    questionText = ""
                    aiAnswer = null
                }) {
                    Text("Close", color = JeevixTextMuted)
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }

    // Notification Dialog
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Text(
                    text = "Notifications",
                    fontWeight = FontWeight.Bold,
                    color = JeevixTextDark,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("• Welcome to Jeevix! Your profile is verified.", fontSize = 13.sp, color = JeevixTextDark)
                    HorizontalDivider(color = JeevixDivider, thickness = 1.dp)
                    Text("• Urgent O+ blood drive scheduled this weekend at City Hospital.", fontSize = 13.sp, color = JeevixTextDark)
                    HorizontalDivider(color = JeevixDivider, thickness = 1.dp)
                    Text("• Reminder: You are currently listed as Available to donate.", fontSize = 13.sp, color = JeevixTextMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
                ) {
                    Text("Close")
                }
            },
            containerColor = JeevixWhite,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun HomeContent(
    userName: String,
    bloodGroup: String,
    donorStatus: String,
    onViewPassport: () -> Unit,
    onSosClick: () -> Unit,
    onFindBloodClick: () -> Unit,
    onHospitalClick: () -> Unit,
    onBloodBankClick: () -> Unit,
    onDonorClick: () -> Unit,
    onAskJeevixClick: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header: JEEVIX (Notification icon)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "JEEVIX",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = JeevixRed,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = JeevixRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.testTag("notification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = JeevixTextDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Greeting: Good Morning \n Aanchal
        Text(
            text = "Good Morning",
            fontSize = 16.sp,
            color = JeevixTextMuted
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = userName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ────────────────────────
        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Health Passport Section
        Text(
            text = "Health Passport",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Blood Group: $bloodGroup",
            fontSize = 15.sp,
            color = JeevixTextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Donor Status: $donorStatus",
            fontSize = 15.sp,
            color = JeevixTextDark
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onViewPassport,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("view_passport_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = JeevixRed,
                contentColor = JeevixWhite
            )
        ) {
            Text(
                text = "View Passport",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ────────────────────────
        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Emergency Section
        Text(
            text = "Emergency",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Need emergency assistance?",
            fontSize = 14.sp,
            color = JeevixTextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onSosClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("sos_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = JeevixRed,
                contentColor = JeevixWhite
            )
        ) {
            Text(
                text = "SOS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ────────────────────────
        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Actions Section
        Text(
            text = "Quick Actions",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Row 1: [ Find Blood ]   [ Hospital ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onFindBloodClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("find_blood_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JeevixRed,
                    contentColor = JeevixWhite
                )
            ) {
                Text(
                    text = "Find Blood",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onHospitalClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("hospital_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JeevixRed,
                    contentColor = JeevixWhite
                )
            ) {
                Text(
                    text = "Hospital",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 2: [ Blood Bank ]   [ Donor ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBloodBankClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("blood_bank_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JeevixRed,
                    contentColor = JeevixWhite
                )
            ) {
                Text(
                    text = "Blood Bank",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onDonorClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("donor_button"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JeevixRed,
                    contentColor = JeevixWhite
                )
            ) {
                Text(
                    text = "Donor",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ────────────────────────
        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Jeevix AI Section
        Text(
            text = "Jeevix AI",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "How can I help you?",
            fontSize = 14.sp,
            color = JeevixTextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onAskJeevixClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("ask_jeevix_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = JeevixRed,
                contentColor = JeevixWhite
            )
        ) {
            Text(
                text = "Ask Jeevix",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PassportTabContent(
    userName: String,
    bloodGroup: String,
    donorStatus: String,
    donorId: String,
    donationsCount: Int,
    onToggleStatus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Health Passport",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )
        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        DetailRow(label = "Donor Name", value = userName)
        DetailRow(label = "Donor ID", value = donorId)
        DetailRow(label = "Blood Group", value = bloodGroup)
        DetailRow(label = "Donor Status", value = donorStatus)
        DetailRow(label = "Donation History", value = "$donationsCount lifetime donations")
        DetailRow(label = "Next Eligible Date", value = "Eligible Now")

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onToggleStatus,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = JeevixRed,
                contentColor = JeevixWhite
            )
        ) {
            Text(
                text = if (donorStatus == "Available") "Toggle Status to Busy" else "Toggle Status to Available",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DonateTabContent(
    bloodGroup: String,
    onRegisterDonor: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Donate Blood",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )
        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        Text(
            text = "Eligibility Checklist",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = JeevixTextDark
        )
        Text("• Age between 18 and 65 years", fontSize = 14.sp, color = JeevixTextDark)
        Text("• Weight at least 50 kg", fontSize = 14.sp, color = JeevixTextDark)
        Text("• Hemoglobin level minimum 12.5 g/dL", fontSize = 14.sp, color = JeevixTextDark)
        Text("• At least 3 months since last whole blood donation", fontSize = 14.sp, color = JeevixTextDark)

        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        Text(
            text = "Your Registered Group: $bloodGroup",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = JeevixRed
        )

        Button(
            onClick = onRegisterDonor,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = JeevixRed,
                contentColor = JeevixWhite
            )
        ) {
            Text("Find Nearby Blood Drives", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ProfileTabContent(
    userProfile: UserProfile,
    donorStatus: String,
    onToggleStatus: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Profile",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = JeevixTextDark
        )
        HorizontalDivider(color = JeevixDivider, thickness = 1.dp)

        DetailRow(label = "Full Name", value = userProfile.name)
        DetailRow(label = "Email / Phone", value = userProfile.email)
        DetailRow(label = "Blood Group", value = userProfile.bloodGroup)
        DetailRow(label = "Donor Status", value = donorStatus)
        DetailRow(label = "Emergency Contact", value = userProfile.emergencyContact)
        DetailRow(label = "Last Donated", value = userProfile.lastDonationDate)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onToggleStatus,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Change Status (${if (donorStatus == "Available") "Set Busy" else "Set Available"})",
                color = JeevixRed,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("logout_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = JeevixRed,
                contentColor = JeevixWhite
            )
        ) {
            Text("LOG OUT", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = JeevixTextMuted)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = JeevixTextDark)
    }
}

@Composable
private fun HospitalItem(name: String, subtitle: String, phone: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JeevixTextDark)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = subtitle, fontSize = 12.sp, color = JeevixTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = "Contact: $phone", fontSize = 12.sp, color = JeevixRed, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) JeevixRed else JeevixTextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) JeevixRed else JeevixTextMuted
        )
    }
}
