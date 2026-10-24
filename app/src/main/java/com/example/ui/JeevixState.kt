package com.example.ui

const val DEMO_PASSWORD = "jeevix123"

enum class AppScreen {
    LOGIN,
    SIGN_UP,
    HOME
}

enum class HomeTab {
    HOME,
    PASSPORT,
    DONATE,
    PROFILE
}

data class UserProfile(
    val name: String = "Aanchal",
    val email: String = "aanchalbb11@gmail.com",
    val password: String = DEMO_PASSWORD,
    val bloodGroup: String = "O+",
    val donorStatus: String = "Available",
    val donorId: String = "JX-8842",
    val donationsCount: Int = 3,
    val emergencyContact: String = "+91 98765 43210",
    val lastDonationDate: String = "12 Jan 2026"
)
