package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:identifier) OR LOWER(name) = LOWER(:identifier) OR phone = :identifier LIMIT 1")
    suspend fun findUserByIdentifier(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE (LOWER(email) = LOWER(:identifier) OR LOWER(name) = LOWER(:identifier) OR phone = :identifier) AND password = :password LIMIT 1")
    suspend fun authenticate(identifier: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}
