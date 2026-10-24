package com.example.data

import android.content.Context
import com.example.R
import com.example.ui.DEMO_PASSWORD
import com.example.ui.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class LoginResult {
    data class Success(val userProfile: UserProfile) : LoginResult()
    object UserNotFound : LoginResult()
    object InvalidPassword : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class SignUpResult {
    data class Success(val userProfile: UserProfile) : SignUpResult()
    object UserAlreadyExists : SignUpResult()
    data class Error(val message: String) : SignUpResult()
}

class UserRepository private constructor(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val userDao = database.userDao()

    private val firestore: FirebaseFirestore? by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            if (dbId.isNotBlank()) {
                FirebaseFirestore.getInstance(dbId)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        if (userDao.getUserCount() == 0) {
            val defaultUser = UserEntity(
                id = "aanchalbb11@gmail.com",
                name = "Aanchal",
                email = "aanchalbb11@gmail.com",
                password = DEMO_PASSWORD,
                bloodGroup = "O+",
                donorStatus = "Available",
                donorId = "JX-8842",
                donationsCount = 3,
                emergencyContact = "+91 98765 43210",
                lastDonationDate = "12 Jan 2026"
            )
            userDao.insertUser(defaultUser)
            syncUserToFirestore(defaultUser)
        }
    }

    suspend fun authenticate(identifier: String, password: String): LoginResult = withContext(Dispatchers.IO) {
        val cleanIdentifier = identifier.trim()
        val cleanPassword = password.trim()

        // 1. Check local Room database first
        var user = userDao.findUserByIdentifier(cleanIdentifier)

        // 2. If not found locally, check Firestore database
        if (user == null && firestore != null) {
            try {
                val doc = firestore?.collection("users")?.document(cleanIdentifier.lowercase())
                    ?.get()?.awaitTask()
                if (doc != null && doc.exists()) {
                    val name = doc.getString("name") ?: cleanIdentifier
                    val email = doc.getString("email") ?: cleanIdentifier
                    val storedPassword = doc.getString("password") ?: ""
                    val bloodGroup = doc.getString("bloodGroup") ?: "O+"
                    val donorStatus = doc.getString("donorStatus") ?: "Available"
                    val donorId = doc.getString("donorId") ?: "JX-1001"
                    val donationsCount = doc.getLong("donationsCount")?.toInt() ?: 0
                    val emergency = doc.getString("emergencyContact") ?: ""
                    val lastDonation = doc.getString("lastDonationDate") ?: "Never"

                    user = UserEntity(
                        id = email.lowercase(),
                        name = name,
                        email = email,
                        password = storedPassword,
                        bloodGroup = bloodGroup,
                        donorStatus = donorStatus,
                        donorId = donorId,
                        donationsCount = donationsCount,
                        emergencyContact = emergency,
                        lastDonationDate = lastDonation
                    )
                    userDao.insertUser(user)
                }
            } catch (_: Exception) {
                // Ignore remote errors and fallback to local result
            }
        }

        // If user is not in database, do not allow login
        if (user == null) {
            return@withContext LoginResult.UserNotFound
        }

        // If user is found, check if password matches
        if (user.password != cleanPassword) {
            return@withContext LoginResult.InvalidPassword
        }

        // Both username and password valid!
        LoginResult.Success(user.toUserProfile())
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        bloodGroup: String = "O+",
        emergencyContact: String = "",
        lastDonationDate: String = "Never",
        donorStatus: String = "Available"
    ): SignUpResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanName = name.trim()
        val cleanPassword = password.trim()
        val cleanEmergency = emergencyContact.trim()
        val cleanLastDonation = lastDonationDate.trim().ifBlank { "Never" }

        val existing = userDao.findUserByIdentifier(cleanEmail)
        if (existing != null) {
            return@withContext SignUpResult.UserAlreadyExists
        }

        val randomIdSuffix = (1000..9999).random()
        val initialDonationsCount = if (cleanLastDonation.equals("Never", ignoreCase = true) || cleanLastDonation.contains("First-time", ignoreCase = true)) 0 else 1

        val newUser = UserEntity(
            id = cleanEmail.lowercase(),
            name = cleanName,
            email = cleanEmail,
            phone = cleanEmergency,
            password = cleanPassword,
            bloodGroup = bloodGroup,
            donorStatus = donorStatus,
            donorId = "JX-$randomIdSuffix",
            donationsCount = initialDonationsCount,
            emergencyContact = cleanEmergency,
            lastDonationDate = cleanLastDonation
        )

        userDao.insertUser(newUser)
        syncUserToFirestore(newUser)

        SignUpResult.Success(newUser.toUserProfile())
    }

    private suspend fun syncUserToFirestore(user: UserEntity) {
        try {
            val data = mapOf(
                "userId" to user.id,
                "name" to user.name,
                "email" to user.email,
                "password" to user.password,
                "bloodGroup" to user.bloodGroup,
                "donorStatus" to user.donorStatus,
                "donorId" to user.donorId,
                "donationsCount" to user.donationsCount,
                "emergencyContact" to user.emergencyContact,
                "lastDonationDate" to user.lastDonationDate
            )
            firestore?.collection("users")?.document(user.id.lowercase())
                ?.set(data)?.awaitTask()
        } catch (_: Exception) {
            // Silently allow local storage if firestore network is unreachable
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        continuation.resume(result)
    }
    addOnFailureListener { exception ->
        continuation.resumeWithException(exception)
    }
}
