package com.example.util

object BloodCompatibility {

    val allBloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    /**
     * Maps blood group to list of groups it can safely donate to
     */
    val canDonateToMap: Map<String, List<String>> = mapOf(
        "O-" to listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"), // Universal Donor
        "O+" to listOf("O+", "A+", "B+", "AB+"),
        "A-" to listOf("A-", "A+", "AB-", "AB+"),
        "A+" to listOf("A+", "AB+"),
        "B-" to listOf("B-", "B+", "AB-", "AB+"),
        "B+" to listOf("B+", "AB+"),
        "AB-" to listOf("AB-", "AB+"),
        "AB+" to listOf("AB+") // Universal Recipient for receiving, only AB+ for donation
    )

    /**
     * Maps blood group to list of groups it can safely receive from
     */
    val canReceiveFromMap: Map<String, List<String>> = mapOf(
        "O-" to listOf("O-"),
        "O+" to listOf("O+", "O-"),
        "A-" to listOf("A-", "O-"),
        "A+" to listOf("A+", "A-", "O+", "O-"),
        "B-" to listOf("B-", "O-"),
        "B+" to listOf("B+", "B-", "O+", "O-"),
        "AB-" to listOf("AB-", "A-", "B-", "O-"),
        "AB+" to listOf("AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-") // Universal Recipient
    )

    fun canDonate(donorBlood: String, recipientBlood: String): Boolean {
        val normalizedDonor = donorBlood.trim().uppercase()
        val normalizedRecipient = recipientBlood.trim().uppercase()
        return canDonateToMap[normalizedDonor]?.contains(normalizedRecipient) == true
    }

    fun getDonateTo(bloodGroup: String): List<String> {
        return canDonateToMap[bloodGroup.trim().uppercase()] ?: emptyList()
    }

    fun getReceiveFrom(bloodGroup: String): List<String> {
        return canReceiveFromMap[bloodGroup.trim().uppercase()] ?: emptyList()
    }

    fun isUniversalDonor(bloodGroup: String): Boolean = bloodGroup.trim().equals("O-", ignoreCase = true)
    fun isUniversalRecipient(bloodGroup: String): Boolean = bloodGroup.trim().equals("AB+", ignoreCase = true)
}
