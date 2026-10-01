package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donor_requests")
data class DonorRequestEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val donorId: String,
    val requesterId: String,
    val requesterName: String,
    val bloodGroup: String,
    val unitsRequired: Int,
    val hospitalName: String,
    val location: String,
    val isEmergency: Boolean,
    val status: String = "Pending", // Pending, Accepted, Rejected, Completed
    val createdAt: Long = System.currentTimeMillis()
)
