package com.example.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.location.FacilityItem
import com.example.location.FacilityType
import com.example.location.GeolocationService
import com.example.location.UserLocation
import com.example.ui.theme.JeevixBorder
import com.example.ui.theme.JeevixRed
import com.example.ui.theme.JeevixTextDark
import com.example.ui.theme.JeevixTextMuted
import com.example.ui.theme.JeevixWhite
import kotlinx.coroutines.launch

@Composable
fun FacilityLocationDialog(
    type: FacilityType,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var userLocation by remember { mutableStateOf<UserLocation?>(null) }
    var isLocating by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(GeolocationService.hasLocationPermission(context)) }

    val refreshLocation = {
        isLocating = true
        coroutineScope.launch {
            val loc = GeolocationService.getDeviceLocation(context)
            userLocation = loc
            isLocating = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = granted
        if (granted) {
            refreshLocation()
        }
    }

    LaunchedEffect(Unit) {
        if (hasPermission) {
            refreshLocation()
        } else {
            // Compute default reference distance
            userLocation = UserLocation(
                latitude = GeolocationService.DEFAULT_LAT,
                longitude = GeolocationService.DEFAULT_LNG,
                address = "Central Region (Location Permission Needed)"
            )
        }
    }

    val facilities = remember(type, userLocation) {
        GeolocationService.computeFacilitiesWithDistance(type, userLocation)
    }

    val title = if (type == FacilityType.HOSPITAL) "Nearby Hospitals" else "Nearby Blood Banks"
    val icon = if (type == FacilityType.HOSPITAL) Icons.Default.LocalHospital else Icons.Default.WaterDrop

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = JeevixRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = JeevixTextDark,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (isLocating) {
                        CircularProgressIndicator(
                            color = JeevixRed,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = {
                                if (hasPermission) {
                                    refreshLocation()
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh GPS Geolocation",
                                tint = JeevixRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // User Geolocation Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF9F9F9), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = JeevixRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = userLocation?.address ?: "Detecting Google Geolocation...",
                        fontSize = 11.sp,
                        color = JeevixTextDark,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // If permission missing banner
                if (!hasPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF0F0), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, JeevixRed.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Enable Live Google Geolocation",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = JeevixRed
                                )
                                Text(
                                    text = "Allow location to calculate live distance to nearest centers",
                                    fontSize = 10.sp,
                                    color = JeevixTextDark
                                )
                            }
                            Button(
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = JeevixRed),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Enable", fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "Nearest centers sorted by distance:",
                    fontSize = 12.sp,
                    color = JeevixTextMuted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(facilities) { facility ->
                        FacilityCardItem(
                            facility = facility,
                            onNavigateMaps = {
                                GeolocationService.openGoogleMapsDirections(context, facility)
                            },
                            onCall = {
                                GeolocationService.dialPhoneNumber(context, facility.phone)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JeevixRed)
            ) {
                Text("Close")
            }
        },
        containerColor = JeevixWhite,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun FacilityCardItem(
    facility: FacilityItem,
    onNavigateMaps: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, JeevixBorder, shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = facility.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = JeevixTextDark
                    )
                    Text(
                        text = facility.tagline,
                        fontSize = 11.sp,
                        color = JeevixTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Dynamic Distance Tag
                if (facility.distanceKm != null) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = JeevixRed.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${facility.distanceKm} km",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = JeevixRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Address & Hours
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = JeevixTextMuted,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = facility.address,
                    fontSize = 11.sp,
                    color = JeevixTextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Blood stock status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = facility.bloodStock,
                    fontSize = 11.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Call & Google Maps Directions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = JeevixRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Call",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = JeevixRed
                    )
                }

                Button(
                    onClick = onNavigateMaps,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JeevixRed),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Google Maps Directions",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Google Maps",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
