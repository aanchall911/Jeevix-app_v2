package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ui.UserProfile

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String, // email in lowercase
    val name: String,
    val email: String,
    val phone: String = "",
    val password: String,
    val bloodGroup: String = "O+",
    val donorStatus: String = "Available",
    val donorId: String = "JX-8842",
    val donationsCount: Int = 0,
    val emergencyContact: String = "+91 98765 43210",
    val lastDonationDate: String = "Never"
) {
    fun toUserProfile(): UserProfile = UserProfile(
        name = name,
        email = email,
        password = password,
        bloodGroup = bloodGroup,
        donorStatus = donorStatus,
        donorId = donorId,
        donationsCount = donationsCount,
        emergencyContact = emergencyContact,
        lastDonationDate = lastDonationDate
    )
}
