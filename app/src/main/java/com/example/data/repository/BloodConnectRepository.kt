package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.BloodRequestEntity
import com.example.data.model.DonorRequestEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class BloodConnectRepository private constructor(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val bloodRequestDao = db.bloodRequestDao()
    private val donorRequestDao = db.donorRequestDao()
    private val prefs = context.getSharedPreferences("blood_connect_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    private suspend fun seedDatabaseIfEmpty() {
        val userCount = userDao.countUsers()
        if (userCount == 0) {
            val defaultUser = UserEntity(
                uid = "usr_current_01",
                name = "Dr. Arvind Rao",
                email = "arvind@example.com",
                phone = "+91 98490 88776",
                city = "Hyderabad",
                area = "Madhapur",
                bloodGroup = "O+",
                isDonor = true,
                availability = true,
                lastDonationDate = "15 Jan 2026",
                profileImage = "",
                age = 28,
                agreedToContact = true,
                totalDonations = 4,
                bloodCredits = 4 // 4 emergency credits earned from 4 past donations
            )

            val seedDonors = listOf(
                defaultUser,
                // O+ Donors
                UserEntity(
                    uid = "donor_001",
                    name = "Rahul Kumar",
                    email = "rahul.k@example.com",
                    phone = "+91 98490 12345",
                    city = "Hyderabad",
                    area = "Madhapur",
                    bloodGroup = "O+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "10 Nov 2025",
                    profileImage = "",
                    age = 26,
                    agreedToContact = true,
                    totalDonations = 3,
                    bloodCredits = 3
                ),
                UserEntity(
                    uid = "donor_009",
                    name = "Rohan Gupta",
                    email = "rohan.g@example.com",
                    phone = "+91 98765 11223",
                    city = "Hyderabad",
                    area = "Kondapur",
                    bloodGroup = "O+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "10 Jan 2026",
                    profileImage = "",
                    age = 25,
                    agreedToContact = true,
                    totalDonations = 2,
                    bloodCredits = 2
                ),
                UserEntity(
                    uid = "donor_o_plus_3",
                    name = "Suresh Patel",
                    email = "suresh.p@example.com",
                    phone = "+91 98480 33445",
                    city = "Hyderabad",
                    area = "Kukatpally",
                    bloodGroup = "O+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "05 Feb 2026",
                    profileImage = "",
                    age = 30,
                    agreedToContact = true,
                    totalDonations = 5,
                    bloodCredits = 5
                ),

                // O- Donors (Universal Donors)
                UserEntity(
                    uid = "donor_005",
                    name = "Vikram Patel",
                    email = "vikram.p@example.com",
                    phone = "+91 98494 56789",
                    city = "Hyderabad",
                    area = "Jubilee Hills",
                    bloodGroup = "O-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "01 Oct 2025",
                    profileImage = "",
                    age = 27,
                    agreedToContact = true,
                    totalDonations = 4,
                    bloodCredits = 4
                ),
                UserEntity(
                    uid = "donor_o_minus_2",
                    name = "Deepa Menon",
                    email = "deepa.m@example.com",
                    phone = "+91 98481 99887",
                    city = "Hyderabad",
                    area = "Banjara Hills",
                    bloodGroup = "O-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "14 Jan 2026",
                    profileImage = "",
                    age = 29,
                    agreedToContact = true,
                    totalDonations = 3,
                    bloodCredits = 3
                ),
                UserEntity(
                    uid = "donor_o_minus_3",
                    name = "David John",
                    email = "david.j@example.com",
                    phone = "+91 98482 11229",
                    city = "Hyderabad",
                    area = "Secunderabad",
                    bloodGroup = "O-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "22 Dec 2025",
                    profileImage = "",
                    age = 32,
                    agreedToContact = true,
                    totalDonations = 6,
                    bloodCredits = 6
                ),

                // A+ Donors
                UserEntity(
                    uid = "donor_002",
                    name = "Priya Sharma",
                    email = "priya.s@example.com",
                    phone = "+91 98491 23456",
                    city = "Hyderabad",
                    area = "Gachibowli",
                    bloodGroup = "A+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "05 Dec 2025",
                    profileImage = "",
                    age = 24,
                    agreedToContact = true,
                    totalDonations = 2,
                    bloodCredits = 2
                ),
                UserEntity(
                    uid = "donor_a_plus_2",
                    name = "Ankit Mishra",
                    email = "ankit.m@example.com",
                    phone = "+91 98483 55667",
                    city = "Hyderabad",
                    area = "Hitec City",
                    bloodGroup = "A+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "18 Jan 2026",
                    profileImage = "",
                    age = 28,
                    agreedToContact = true,
                    totalDonations = 4,
                    bloodCredits = 4
                ),
                UserEntity(
                    uid = "donor_a_plus_3",
                    name = "Kavita Krishnan",
                    email = "kavita.k@example.com",
                    phone = "+91 98484 77889",
                    city = "Hyderabad",
                    area = "Begumpet",
                    bloodGroup = "A+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "29 Nov 2025",
                    profileImage = "",
                    age = 31,
                    agreedToContact = true,
                    totalDonations = 5,
                    bloodCredits = 5
                ),

                // A- Donors
                UserEntity(
                    uid = "donor_006",
                    name = "Ananya Roy",
                    email = "ananya.r@example.com",
                    phone = "+91 98495 67890",
                    city = "Hyderabad",
                    area = "Kondapur",
                    bloodGroup = "A-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "10 Jan 2026",
                    profileImage = "",
                    age = 23,
                    agreedToContact = true,
                    totalDonations = 2,
                    bloodCredits = 2
                ),
                UserEntity(
                    uid = "donor_a_minus_2",
                    name = "Farhan Akhtar",
                    email = "farhan.a@example.com",
                    phone = "+91 98485 22334",
                    city = "Hyderabad",
                    area = "Tolichowki",
                    bloodGroup = "A-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "08 Dec 2025",
                    profileImage = "",
                    age = 33,
                    agreedToContact = true,
                    totalDonations = 3,
                    bloodCredits = 3
                ),
                UserEntity(
                    uid = "donor_a_minus_3",
                    name = "Neha Verma",
                    email = "neha.v@example.com",
                    phone = "+91 98486 44556",
                    city = "Hyderabad",
                    area = "Ameerpet",
                    bloodGroup = "A-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "19 Jan 2026",
                    profileImage = "",
                    age = 27,
                    agreedToContact = true,
                    totalDonations = 1,
                    bloodCredits = 1
                ),

                // B+ Donors
                UserEntity(
                    uid = "donor_003",
                    name = "Sneha Reddy",
                    email = "sneha.r@example.com",
                    phone = "+91 98492 34567",
                    city = "Hyderabad",
                    area = "Banjara Hills",
                    bloodGroup = "B+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "20 Jan 2026",
                    profileImage = "",
                    age = 29,
                    agreedToContact = true,
                    totalDonations = 5,
                    bloodCredits = 5
                ),
                UserEntity(
                    uid = "donor_010",
                    name = "Pooja Deshmukh",
                    email = "pooja.d@example.com",
                    phone = "+91 98200 44556",
                    city = "Hyderabad",
                    area = "Madhapur",
                    bloodGroup = "B+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "02 Dec 2025",
                    profileImage = "",
                    age = 26,
                    agreedToContact = true,
                    totalDonations = 3,
                    bloodCredits = 3
                ),
                UserEntity(
                    uid = "donor_b_plus_3",
                    name = "Manoj Bajpayee",
                    email = "manoj.b@example.com",
                    phone = "+91 98487 66778",
                    city = "Hyderabad",
                    area = "Somajiguda",
                    bloodGroup = "B+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "15 Jan 2026",
                    profileImage = "",
                    age = 35,
                    agreedToContact = true,
                    totalDonations = 7,
                    bloodCredits = 7
                ),

                // B- Donors
                UserEntity(
                    uid = "donor_007",
                    name = "Rajesh Iyer",
                    email = "rajesh.i@example.com",
                    phone = "+91 98496 78901",
                    city = "Hyderabad",
                    area = "Kukatpally",
                    bloodGroup = "B-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "18 Nov 2025",
                    profileImage = "",
                    age = 34,
                    agreedToContact = true,
                    totalDonations = 7,
                    bloodCredits = 7
                ),
                UserEntity(
                    uid = "donor_b_minus_2",
                    name = "Sunita Rao",
                    email = "sunita.r@example.com",
                    phone = "+91 98488 88990",
                    city = "Hyderabad",
                    area = "Manikonda",
                    bloodGroup = "B-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "25 Dec 2025",
                    profileImage = "",
                    age = 28,
                    agreedToContact = true,
                    totalDonations = 2,
                    bloodCredits = 2
                ),
                UserEntity(
                    uid = "donor_b_minus_3",
                    name = "Devansh Joshi",
                    email = "devansh.j@example.com",
                    phone = "+91 98489 11234",
                    city = "Hyderabad",
                    area = "Mehdipatnam",
                    bloodGroup = "B-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "12 Jan 2026",
                    profileImage = "",
                    age = 26,
                    agreedToContact = true,
                    totalDonations = 3,
                    bloodCredits = 3
                ),

                // AB+ Donors (Universal Recipient)
                UserEntity(
                    uid = "donor_004",
                    name = "Amit Verma",
                    email = "amit.v@example.com",
                    phone = "+91 98493 45678",
                    city = "Hyderabad",
                    area = "Hitec City",
                    bloodGroup = "AB+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "12 Aug 2025",
                    profileImage = "",
                    age = 31,
                    agreedToContact = true,
                    totalDonations = 6,
                    bloodCredits = 6
                ),
                UserEntity(
                    uid = "donor_ab_plus_2",
                    name = "Shalini Nair",
                    email = "shalini.n@example.com",
                    phone = "+91 98490 99001",
                    city = "Hyderabad",
                    area = "Jubilee Hills",
                    bloodGroup = "AB+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "04 Jan 2026",
                    profileImage = "",
                    age = 27,
                    agreedToContact = true,
                    totalDonations = 4,
                    bloodCredits = 4
                ),
                UserEntity(
                    uid = "donor_ab_plus_3",
                    name = "Varun Dhawan",
                    email = "varun.d@example.com",
                    phone = "+91 98491 55660",
                    city = "Hyderabad",
                    area = "Gachibowli",
                    bloodGroup = "AB+",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "20 Nov 2025",
                    profileImage = "",
                    age = 29,
                    agreedToContact = true,
                    totalDonations = 2,
                    bloodCredits = 2
                ),

                // AB- Donors
                UserEntity(
                    uid = "donor_008",
                    name = "Karthik Nair",
                    email = "karthik.n@example.com",
                    phone = "+91 98497 89012",
                    city = "Hyderabad",
                    area = "Secunderabad",
                    bloodGroup = "AB-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "14 Dec 2025",
                    profileImage = "",
                    age = 28,
                    agreedToContact = true,
                    totalDonations = 2,
                    bloodCredits = 2
                ),
                UserEntity(
                    uid = "donor_ab_minus_2",
                    name = "Meera Nambiar",
                    email = "meera.n@example.com",
                    phone = "+91 98492 77881",
                    city = "Hyderabad",
                    area = "Kondapur",
                    bloodGroup = "AB-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "11 Jan 2026",
                    profileImage = "",
                    age = 30,
                    agreedToContact = true,
                    totalDonations = 3,
                    bloodCredits = 3
                ),
                UserEntity(
                    uid = "donor_ab_minus_3",
                    name = "Tanvi Seth",
                    email = "tanvi.s@example.com",
                    phone = "+91 98493 88992",
                    city = "Hyderabad",
                    area = "Dilsukhnagar",
                    bloodGroup = "AB-",
                    isDonor = true,
                    availability = true,
                    lastDonationDate = "16 Dec 2025",
                    profileImage = "",
                    age = 25,
                    agreedToContact = true,
                    totalDonations = 1,
                    bloodCredits = 1
                )
            )

            userDao.insertUsers(seedDonors)
            val sampleRequests = listOf(
                BloodRequestEntity(
                    requestId = "BR000124",
                    requesterId = "donor_003",
                    requesterName = "Sneha Reddy",
                    requesterPhone = "+91 98492 34567",
                    patientName = "Master Aarav Sharma (Age 9)",
                    operationDetails = "Emergency Open-Heart Cardiac Surgery",
                    bloodGroup = "O+",
                    unitsRequired = 4,
                    unitsFulfilled = 1, // 1 donated, 3 packets still urgently needed!
                    hospitalName = "Apollo Hospital",
                    location = "Road No 72, Film Nagar",
                    city = "Hyderabad",
                    requiredDate = "Immediate / Today",
                    isEmergency = true,
                    message = "Pediatric cardiac surgery in progress in Operation Theatre 3. 1 packet received, 3 more packets urgently needed today!",
                    assignedDonorId = "donor_001",
                    assignedDonorName = "Rahul Kumar",
                    status = "Partially Fulfilled"
                ),
                BloodRequestEntity(
                    requestId = "BR000130",
                    requesterId = "donor_005",
                    requesterName = "Vikram Patel",
                    requesterPhone = "+91 98494 56789",
                    patientName = "Smt. Meenakshi Sundaram",
                    operationDetails = "Multiple Trauma & Orthopedic Reconstruction",
                    bloodGroup = "B+",
                    unitsRequired = 3,
                    unitsFulfilled = 0, // All 3 needed!
                    hospitalName = "KIMS Hospital",
                    location = "Minister Road, Secunderabad",
                    city = "Hyderabad",
                    requiredDate = "Today - Emergency",
                    isEmergency = true,
                    message = "Road accident victim admitted to ICU-2. Immediate blood arrangement required for major orthopedic surgery.",
                    assignedDonorId = null,
                    assignedDonorName = null,
                    status = "Pending"
                ),
                BloodRequestEntity(
                    requestId = "BR000119",
                    requesterId = "donor_002",
                    requesterName = "Priya Sharma",
                    requesterPhone = "+91 98491 23456",
                    patientName = "Mr. Raghavan Nambiar",
                    operationDetails = "Emergency Liver Transplant Procedure",
                    bloodGroup = "A+",
                    unitsRequired = 2,
                    unitsFulfilled = 2, // Fully completed!
                    hospitalName = "Yashoda Hospital",
                    location = "Alexander Road, Secunderabad",
                    city = "Hyderabad",
                    requiredDate = "18 Sep 2026",
                    isEmergency = false,
                    message = "Scheduled liver procedure. All units successfully fulfilled by BloodConnect donors.",
                    assignedDonorId = "donor_a_plus_2",
                    assignedDonorName = "Ankit Mishra",
                    status = "Completed"
                ),
                BloodRequestEntity(
                    requestId = "BR000098",
                    requesterId = "other_user_88",
                    requesterName = "Sunita Chawla",
                    requesterPhone = "+91 98112 33445",
                    patientName = "Baby Ananya Roy",
                    operationDetails = "Thalassemia Blood Transfusion Cycle",
                    bloodGroup = "O-",
                    unitsRequired = 2,
                    unitsFulfilled = 1, // 1 received, 1 remaining
                    hospitalName = "Rainbow Children's Hospital",
                    location = "Road No 2, Banjara Hills",
                    city = "Hyderabad",
                    requiredDate = "Urgent",
                    isEmergency = true,
                    message = "Critical Thalassemia transfusion needed. 1 packet donated, need 1 more universal O- donor.",
                    assignedDonorId = "donor_005",
                    assignedDonorName = "Vikram Patel",
                    status = "Partially Fulfilled"
                )
            )
            bloodRequestDao.insertRequests(sampleRequests)
        }

        // Restore user session only if explicitly logged in
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val savedUid = prefs.getString("logged_in_uid", null)
        if (isLoggedIn && savedUid != null) {
            val existing = userDao.getUserByIdSync(savedUid)
            _currentUser.value = existing
        } else {
            _currentUser.value = null
        }
    }

    fun completeOnboarding() {
        _onboardingCompleted.value = true
    }

    suspend fun login(email: String, nameIfNew: String = "User"): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(email.trim())
        val user = if (existing != null) {
            existing
        } else {
            val newUser = UserEntity(
                uid = "usr_" + UUID.randomUUID().toString().take(8),
                name = nameIfNew,
                email = email.trim(),
                phone = "+91 98490 " + (10000..99999).random(),
                city = "Hyderabad",
                area = "Madhapur",
                bloodGroup = "O+",
                isDonor = false,
                availability = true
            )
            userDao.insertUser(newUser)
            newUser
        }
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("logged_in_uid", user.uid)
            .apply()
        _currentUser.value = user
        Result.success(user)
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String = "",
        city: String = "",
        area: String = "",
        bloodGroup: String = "O+"
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(email.trim())
        val user = if (existing != null) {
            existing
        } else {
            val newUser = UserEntity(
                uid = "usr_" + UUID.randomUUID().toString().take(8),
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim().ifEmpty { "+91 98490 " + (10000..99999).random() },
                city = city.trim().ifEmpty { "Hyderabad" },
                area = area.trim().ifEmpty { "Central" },
                bloodGroup = bloodGroup,
                isDonor = false,
                availability = true
            )
            userDao.insertUser(newUser)
            newUser
        }
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("logged_in_uid", user.uid)
            .apply()
        _currentUser.value = user
        Result.success(user)
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("logged_in_uid")
            .apply()
        _currentUser.value = null
    }

    suspend fun updateCurrentUser(updated: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun updateDonorStatus(
        uid: String,
        isDonor: Boolean,
        availability: Boolean,
        phone: String,
        city: String,
        area: String,
        bloodGroup: String,
        lastDonationDate: String
    ) = withContext(Dispatchers.IO) {
        userDao.updateDonorDetails(
            uid = uid,
            isDonor = isDonor,
            availability = availability,
            phone = phone,
            city = city,
            area = area,
            bloodGroup = bloodGroup,
            lastDonationDate = lastDonationDate
        )
        val refreshed = userDao.getUserByIdSync(uid)
        _currentUser.value = refreshed
    }

    suspend fun setDonorAvailability(uid: String, available: Boolean) = withContext(Dispatchers.IO) {
        userDao.updateAvailability(uid, available)
        val refreshed = userDao.getUserByIdSync(uid)
        _currentUser.value = refreshed
    }

    fun getAvailableDonors(): Flow<List<UserEntity>> {
        return userDao.getAvailableDonors()
    }

    fun getAllDonors(): Flow<List<UserEntity>> {
        return userDao.getAllDonors()
    }

    fun searchDonors(bloodGroup: String, city: String, area: String): Flow<List<UserEntity>> {
        return userDao.getAvailableDonors().map { donors ->
            donors.filter { donor ->
                val matchesBlood = if (bloodGroup.isEmpty() || bloodGroup.equals("Any", ignoreCase = true) || bloodGroup.equals("Any Blood Group", ignoreCase = true)) {
                    true
                } else {
                    donor.bloodGroup.equals(bloodGroup.trim(), ignoreCase = true)
                }

                val matchesCity = if (city.isBlank()) {
                    true
                } else {
                    donor.city.contains(city.trim(), ignoreCase = true)
                }

                val matchesArea = if (area.isBlank()) {
                    true
                } else {
                    donor.area.contains(area.trim(), ignoreCase = true)
                }

                matchesBlood && matchesCity && matchesArea
            }
        }
    }

    suspend fun getDonorById(donorId: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByIdSync(donorId)
    }

    fun getRequestsByUser(userId: String): Flow<List<BloodRequestEntity>> {
        return bloodRequestDao.getRequestsByRequester(userId)
    }

    fun getAllRequests(): Flow<List<BloodRequestEntity>> {
        return bloodRequestDao.getAllRequests()
    }

    fun getEmergencyRequests(): Flow<List<BloodRequestEntity>> {
        return bloodRequestDao.getEmergencyRequests()
    }

    fun getActiveEmergencyRequestsInCity(city: String): Flow<List<BloodRequestEntity>> {
        return bloodRequestDao.getActiveEmergencyRequestsInCity(city)
    }

    fun getRequestsForDonor(donorId: String, donorBloodGroup: String): Flow<List<BloodRequestEntity>> {
        return bloodRequestDao.getRequestsForDonor(donorId, donorBloodGroup)
    }

    suspend fun createBloodRequest(
        bloodGroup: String,
        unitsRequired: Int,
        hospitalName: String,
        location: String,
        city: String,
        requiredDate: String,
        isEmergency: Boolean,
        message: String,
        patientName: String = "",
        operationDetails: String = "",
        creditsToRedeem: Int = 0,
        assignedDonorId: String? = null,
        assignedDonorName: String? = null
    ): BloodRequestEntity = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        val reqNum = (100000..999999).random()
        val requestId = "BR$reqNum"

        val effectivePatient = patientName.trim().ifEmpty { "Patient in Need" }
        val effectiveOperation = operationDetails.trim().ifEmpty { if (isEmergency) "Emergency Medical Procedure" else "Scheduled Treatment" }

        // Deduct blood credits if redeemed
        if (creditsToRedeem > 0 && user != null) {
            userDao.useBloodCredits(user.uid, creditsToRedeem)
            val refreshed = userDao.getUserByIdSync(user.uid)
            _currentUser.value = refreshed
        }

        val request = BloodRequestEntity(
            requestId = requestId,
            requesterId = user?.uid ?: "anon_usr",
            requesterName = user?.name ?: "Blood Seeker",
            requesterPhone = user?.phone ?: "",
            patientName = effectivePatient,
            operationDetails = effectiveOperation,
            bloodGroup = bloodGroup,
            unitsRequired = unitsRequired,
            unitsFulfilled = 0,
            creditsRedeemed = creditsToRedeem,
            hospitalName = hospitalName,
            location = location,
            city = city,
            requiredDate = requiredDate,
            isEmergency = isEmergency,
            message = message,
            assignedDonorId = assignedDonorId,
            assignedDonorName = assignedDonorName,
            status = "Pending"
        )
        bloodRequestDao.insertRequest(request)

        if (assignedDonorId != null) {
            val donorReq = DonorRequestEntity(
                id = UUID.randomUUID().toString(),
                requestId = requestId,
                donorId = assignedDonorId,
                requesterId = request.requesterId,
                requesterName = request.requesterName,
                bloodGroup = bloodGroup,
                unitsRequired = unitsRequired,
                hospitalName = hospitalName,
                location = location,
                isEmergency = isEmergency,
                status = "Pending"
            )
            donorRequestDao.insertDonorRequest(donorReq)
        } else if (isEmergency) {
            // Live broadcast to matching available donors in the city
            val available = userDao.getAvailableDonorsSync()
            available.filter { d ->
                d.city.equals(city, ignoreCase = true) &&
                (d.bloodGroup.equals(bloodGroup, ignoreCase = true) || d.bloodGroup == "O-")
            }.forEach { d ->
                val donorReq = DonorRequestEntity(
                    id = UUID.randomUUID().toString(),
                    requestId = requestId,
                    donorId = d.uid,
                    requesterId = request.requesterId,
                    requesterName = request.requesterName,
                    bloodGroup = bloodGroup,
                    unitsRequired = unitsRequired,
                    hospitalName = hospitalName,
                    location = location,
                    isEmergency = true,
                    status = "Pending"
                )
                donorRequestDao.insertDonorRequest(donorReq)
            }
        }

        request
    }

    suspend fun pledgeBloodDonation(
        requestId: String,
        donorId: String,
        donorName: String,
        packetsDonated: Int
    ) = withContext(Dispatchers.IO) {
        val currentReq = bloodRequestDao.getRequestByIdSync(requestId) ?: return@withContext
        val newFulfilled = currentReq.unitsFulfilled + packetsDonated
        val newStatus = if (newFulfilled >= currentReq.unitsRequired) "Completed" else "Partially Fulfilled"

        bloodRequestDao.updateUnitsFulfilled(requestId, newFulfilled, newStatus)
        if (currentReq.assignedDonorId == null) {
            bloodRequestDao.assignDonor(requestId, donorId, donorName, newStatus)
        }
        donorRequestDao.updateStatusByBloodRequestId(requestId, newStatus)

        // Award 1 Blood Credit per packet donated!
        userDao.addBloodCredits(donorId, packetsDonated)

        // Refresh current user if the active session is this donor
        if (_currentUser.value?.uid == donorId) {
            val updated = userDao.getUserByIdSync(donorId)
            _currentUser.value = updated
        }
    }

    suspend fun redeemBloodCredits(uid: String, packetsCount: Int) = withContext(Dispatchers.IO) {
        userDao.useBloodCredits(uid, packetsCount)
        val refreshed = userDao.getUserByIdSync(uid)
        _currentUser.value = refreshed
    }

    suspend fun updateRequestStatus(requestId: String, newStatus: String) = withContext(Dispatchers.IO) {
        bloodRequestDao.updateRequestStatus(requestId, newStatus)
        donorRequestDao.updateStatusByBloodRequestId(requestId, newStatus)
    }

    suspend fun acceptRequest(requestId: String, donorId: String, donorName: String) = withContext(Dispatchers.IO) {
        pledgeBloodDonation(requestId, donorId, donorName, 1)
    }

    suspend fun rejectRequest(requestId: String) = withContext(Dispatchers.IO) {
        bloodRequestDao.updateRequestStatus(requestId, "Rejected")
        donorRequestDao.updateStatusByBloodRequestId(requestId, "Rejected")
    }

    companion object {
        @Volatile
        private var INSTANCE: BloodConnectRepository? = null

        fun getInstance(context: Context): BloodConnectRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = BloodConnectRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
