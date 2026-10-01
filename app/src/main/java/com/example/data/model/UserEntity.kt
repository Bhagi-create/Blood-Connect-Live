package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val city: String = "",
    val area: String = "",
    val bloodGroup: String = "O+",
    val isDonor: Boolean = false,
    val availability: Boolean = true,
    val lastDonationDate: String = "",
    val profileImage: String = "",
    val age: Int = 25,
    val agreedToContact: Boolean = true,
    val totalDonations: Int = 0,
    val bloodCredits: Int = 0, // 1 credit per donation -> 1 free blood packet in emergency
    val creditsUsed: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
