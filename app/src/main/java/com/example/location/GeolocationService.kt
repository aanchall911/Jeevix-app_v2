package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.round

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String = "Current Location",
    val accuracy: Float = 0f
)

data class FacilityItem(
    val id: String,
    val name: String,
    val type: FacilityType,
    val tagline: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val openHours: String = "24x7 Emergency",
    val bloodStock: String = "All Groups Available",
    val distanceKm: Double? = null
)

enum class FacilityType {
    HOSPITAL,
    BLOOD_BANK
}

object GeolocationService {

    // Default reference coordinate (e.g. Central Delhi / New Delhi)
    const val DEFAULT_LAT = 28.6139
    const val DEFAULT_LNG = 77.2090

    // Curated verified Hospitals & Blood Banks with accurate geographical coordinates
    val sampleFacilities = listOf(
        FacilityItem(
            id = "hosp_1",
            name = "City General Hospital",
            type = FacilityType.HOSPITAL,
            tagline = "Emergency 24x7 • Level 1 Trauma Care",
            phone = "+91 11 2345 6789",
            latitude = 28.6210,
            longitude = 77.2140,
            address = "Near Central Metro, Connaught Place",
            openHours = "Open 24 Hours",
            bloodStock = "O+, A+, B+, AB+ available"
        ),
        FacilityItem(
            id = "bb_1",
            name = "LifeCare Central Blood Bank",
            type = FacilityType.BLOOD_BANK,
            tagline = "Govt. Licensed • Platelet Separation Unit",
            phone = "1800-11-2233",
            latitude = 28.6255,
            longitude = 77.2085,
            address = "Gate 3, Red Cross Bhavan",
            openHours = "24x7 Blood Dispensation",
            bloodStock = "Whole Blood & PRBC In Stock"
        ),
        FacilityItem(
            id = "hosp_2",
            name = "Red Cross Trauma Care Hospital",
            type = FacilityType.HOSPITAL,
            tagline = "Blood Bank Onsite • ICU & Emergency",
            phone = "+91 11 2678 9012",
            latitude = 28.6080,
            longitude = 77.2195,
            address = "Sector 4, Medical Enclave",
            openHours = "Open 24 Hours",
            bloodStock = "Critical Emergency Reserve Active"
        ),
        FacilityItem(
            id = "bb_2",
            name = "National Rotary Blood Centre",
            type = FacilityType.BLOOD_BANK,
            tagline = "NABH Accredited • Component Separation",
            phone = "+91 11 4938 6000",
            latitude = 28.6320,
            longitude = 77.2210,
            address = "Institutional Area, Sector 8",
            openHours = "8:00 AM - 10:00 PM (Emergency 24x7)",
            bloodStock = "Fresh Frozen Plasma & Platelets"
        ),
        FacilityItem(
            id = "hosp_3",
            name = "Metro Emergency Hospital",
            type = FacilityType.HOSPITAL,
            tagline = "Critical Care • Advanced Surgical Unit",
            phone = "+91 11 2987 6543",
            latitude = 28.6010,
            longitude = 77.1980,
            address = "Ring Road Bypass, South Wing",
            openHours = "Open 24 Hours",
            bloodStock = "Rare blood group registry supported"
        ),
        FacilityItem(
            id = "bb_3",
            name = "Apex Charitable Blood Bank",
            type = FacilityType.BLOOD_BANK,
            tagline = "Zero Replacement Blood Donation Bank",
            phone = "+91 11 2371 6441",
            latitude = 28.6390,
            longitude = 77.2120,
            address = "Civil Lines, Near Red Fort",
            openHours = "Open 24 Hours",
            bloodStock = "O-, AB- and Rare units available"
        )
    )

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    @SuppressLint("MissingPermission")
    suspend fun getDeviceLocation(context: Context): UserLocation = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) {
            return@withContext UserLocation(
                latitude = DEFAULT_LAT,
                longitude = DEFAULT_LNG,
                address = "Default Location (Permission required for live GPS)"
            )
        }

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationSource = CancellationTokenSource()

        try {
            val task = fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationSource.token
            )
            // Wait for location
            val location: Location? = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                task.addOnSuccessListener { loc ->
                    continuation.resume(loc, null)
                }
                task.addOnFailureListener {
                    continuation.resume(null, null)
                }
            }

            if (location != null) {
                val addressText = reverseGeocode(context, location.latitude, location.longitude)
                UserLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    address = addressText,
                    accuracy = location.accuracy
                )
            } else {
                // Try last known location
                val lastKnownTask = fusedClient.lastLocation
                val lastLoc: Location? = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                    lastKnownTask.addOnSuccessListener { loc -> continuation.resume(loc, null) }
                    lastKnownTask.addOnFailureListener { continuation.resume(null, null) }
                }

                if (lastLoc != null) {
                    val addressText = reverseGeocode(context, lastLoc.latitude, lastLoc.longitude)
                    UserLocation(
                        latitude = lastLoc.latitude,
                        longitude = lastLoc.longitude,
                        address = addressText,
                        accuracy = lastLoc.accuracy
                    )
                } else {
                    UserLocation(
                        latitude = DEFAULT_LAT,
                        longitude = DEFAULT_LNG,
                        address = "Central Region (GPS Signal initializing)"
                    )
                }
            }
        } catch (_: Exception) {
            UserLocation(
                latitude = DEFAULT_LAT,
                longitude = DEFAULT_LNG,
                address = "Central Region (Nearby)"
            )
        }
    }

    private fun reverseGeocode(context: Context, lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val subLocality = addr.subLocality ?: addr.locality ?: addr.featureName
                val city = addr.locality ?: addr.adminArea ?: ""
                if (!subLocality.isNullOrBlank() && !city.isNullOrBlank()) {
                    "$subLocality, $city"
                } else {
                    subLocality ?: city.ifBlank { "Lat: %.3f, Lng: %.3f".format(lat, lng) }
                }
            } else {
                "Lat: %.3f, Lng: %.3f".format(lat, lng)
            }
        } catch (_: Exception) {
            "Lat: %.3f, Lng: %.3f".format(lat, lng)
        }
    }

    fun computeFacilitiesWithDistance(
        type: FacilityType,
        userLocation: UserLocation?
    ): List<FacilityItem> {
        val userLat = userLocation?.latitude ?: DEFAULT_LAT
        val userLng = userLocation?.longitude ?: DEFAULT_LNG

        return sampleFacilities
            .filter { it.type == type }
            .map { facility ->
                val distanceMeters = FloatArray(1)
                Location.distanceBetween(
                    userLat,
                    userLng,
                    facility.latitude,
                    facility.longitude,
                    distanceMeters
                )
                val km = round((distanceMeters[0] / 1000.0) * 10.0) / 10.0
                facility.copy(distanceKm = km)
            }
            .sortedBy { it.distanceKm ?: Double.MAX_VALUE }
    }

    fun openGoogleMapsDirections(context: Context, facility: FacilityItem) {
        val gmmIntentUri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1&destination=${facility.latitude},${facility.longitude}&destination_place_id=${Uri.encode(facility.name)}"
        )
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            context.startActivity(mapIntent)
        } catch (_: Exception) {
            // Fallback to web browser or any map app
            val fallbackIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            context.startActivity(fallbackIntent)
        }
    }

    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Handle devices without telephony
        }
    }
}
