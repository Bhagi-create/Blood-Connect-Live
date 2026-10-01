package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isDonor = 1 ORDER BY createdAt DESC")
    fun getAllDonors(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isDonor = 1 AND availability = 1 ORDER BY createdAt DESC")
    fun getAvailableDonors(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isDonor = 1 AND availability = 1")
    suspend fun getAvailableDonorsSync(): List<UserEntity>

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    fun getUserById(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserByIdSync(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET availability = :availability WHERE uid = :uid")
    suspend fun updateAvailability(uid: String, availability: Boolean)

    @Query("UPDATE users SET isDonor = :isDonor, availability = :availability, phone = :phone, city = :city, area = :area, bloodGroup = :bloodGroup, lastDonationDate = :lastDonationDate WHERE uid = :uid")
    suspend fun updateDonorDetails(
        uid: String,
        isDonor: Boolean,
        availability: Boolean,
        phone: String,
        city: String,
        area: String,
        bloodGroup: String,
        lastDonationDate: String
    )

    @Query("UPDATE users SET bloodCredits = bloodCredits + :count, totalDonations = totalDonations + :count WHERE uid = :uid")
    suspend fun addBloodCredits(uid: String, count: Int)

    @Query("UPDATE users SET bloodCredits = MAX(0, bloodCredits - :count), creditsUsed = creditsUsed + :count WHERE uid = :uid")
    suspend fun useBloodCredits(uid: String, count: Int)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
}
