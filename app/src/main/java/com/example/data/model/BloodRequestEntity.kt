package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_requests")
data class BloodRequestEntity(
    @PrimaryKey val requestId: String,
    val requesterId: String,
    val requesterName: String,
    val requesterPhone: String = "",
    val patientName: String = "", // e.g. "Ramesh Sharma"
    val operationDetails: String = "", // e.g. "Emergency Cardiac Bypass Surgery"
    val bloodGroup: String,
    val unitsRequired: Int,
    val unitsFulfilled: Int = 0, // e.g. 1 packet donated so far
    val creditsRedeemed: Int = 0, // Free packets availed using donor credits
    val hospitalName: String,
    val location: String, // Area / Address
    val city: String,
    val requiredDate: String,
    val isEmergency: Boolean,
    val message: String = "",
    val assignedDonorId: String? = null,
    val assignedDonorName: String? = null,
    val status: String = "Pending", // Pending, Partially Fulfilled, Accepted, Completed, Cancelled
    val createdAt: Long = System.currentTimeMillis()
) {
    val unitsRemaining: Int
        get() = (unitsRequired - unitsFulfilled).coerceAtLeast(0)

    val isPartiallyFulfilled: Boolean
        get() = unitsFulfilled > 0 && unitsFulfilled < unitsRequired

    val isFullyCompleted: Boolean
        get() = unitsFulfilled >= unitsRequired || status == "Completed"

    val progressFraction: Float
        get() = if (unitsRequired > 0) (unitsFulfilled.toFloat() / unitsRequired.toFloat()).coerceIn(0f, 1f) else 0f
}
