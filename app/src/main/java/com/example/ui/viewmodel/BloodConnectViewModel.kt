package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BloodRequestEntity
import com.example.data.model.UserEntity
import com.example.data.repository.BloodConnectRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Main : Screen("main")
    object SearchResults : Screen("search_results")
    object DonorDetails : Screen("donor_details")
    object BecomeDonor : Screen("become_donor")
    object EditDonorProfile : Screen("edit_donor_profile")
    object BloodRequest : Screen("blood_request")
    object RequestConfirmation : Screen("request_confirmation")
    object RequestDetails : Screen("request_details")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object About : Screen("about")
}

@OptIn(ExperimentalCoroutinesApi::class)
class BloodConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BloodConnectRepository.getInstance(application)

    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    fun isUserLoggedIn(): Boolean = repository.isUserLoggedIn()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Search, 2: Requests, 3: Profile
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _requestsSubTab = MutableStateFlow(0) // 0: My Requests, 1: Donor Requests
    val requestsSubTab: StateFlow<Int> = _requestsSubTab.asStateFlow()

    // Search Query State
    val searchBloodGroup = MutableStateFlow("Any")
    val searchCity = MutableStateFlow("")
    val searchArea = MutableStateFlow("")

    val availableDonors: StateFlow<List<UserEntity>> = repository.getAvailableDonors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<UserEntity>> = combine(
        searchBloodGroup,
        searchCity,
        searchArea
    ) { bg, city, area ->
        Triple(bg, city, area)
    }.flatMapLatest { (bg, city, area) ->
        repository.searchDonors(bg, city, area)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val emergencyRequests: StateFlow<List<BloodRequestEntity>> = repository.getEmergencyRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cityEmergencySosRequests: StateFlow<List<BloodRequestEntity>> = currentUser.flatMapLatest { user ->
        val city = user?.city?.ifBlank { "Hyderabad" } ?: "Hyderabad"
        repository.getActiveEmergencyRequestsInCity(city)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myRequests: StateFlow<List<BloodRequestEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getRequestsByUser(user.uid)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val donorRequests: StateFlow<List<BloodRequestEntity>> = currentUser.flatMapLatest { user ->
        if (user != null && user.isDonor) {
            repository.getRequestsForDonor(user.uid, user.bloodGroup)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDonor = MutableStateFlow<UserEntity?>(null)
    val selectedDonor: StateFlow<UserEntity?> = _selectedDonor.asStateFlow()

    private val _selectedRequest = MutableStateFlow<BloodRequestEntity?>(null)
    val selectedRequest: StateFlow<BloodRequestEntity?> = _selectedRequest.asStateFlow()

    private val _lastSubmittedRequest = MutableStateFlow<BloodRequestEntity?>(null)
    val lastSubmittedRequest: StateFlow<BloodRequestEntity?> = _lastSubmittedRequest.asStateFlow()

    val isEmergencyRequest = MutableStateFlow(false)

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
        if (_currentScreen.value != Screen.Main) {
            _currentScreen.value = Screen.Main
        }
    }

    fun setRequestsSubTab(subTab: Int) {
        _requestsSubTab.value = subTab
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun completeOnboarding() {
        repository.completeOnboarding()
        if (currentUser.value != null) {
            _currentScreen.value = Screen.Main
        } else {
            _currentScreen.value = Screen.Login
        }
    }

    fun selectDonor(donor: UserEntity) {
        _selectedDonor.value = donor
        _currentScreen.value = Screen.DonorDetails
    }

    fun selectRequest(request: BloodRequestEntity) {
        _selectedRequest.value = request
        _currentScreen.value = Screen.RequestDetails
    }

    fun startBloodRequest(isEmergency: Boolean = false, donor: UserEntity? = null) {
        _selectedDonor.value = donor
        isEmergencyRequest.value = isEmergency
        _currentScreen.value = Screen.BloodRequest
    }

    fun login(email: String, nameIfNew: String = "User", onComplete: (Boolean, String?) -> Unit) {
        if (email.isBlank() || !email.contains("@")) {
            onComplete(false, "Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(email, nameIfNew)
            _isLoading.value = false
            if (result.isSuccess) {
                _currentScreen.value = Screen.Main
                onComplete(true, null)
            } else {
                onComplete(false, result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPass: String,
        phone: String,
        city: String,
        area: String,
        bloodGroup: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (name.isBlank()) {
            onComplete(false, "Full Name is required.")
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            onComplete(false, "Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            onComplete(false, "Password must contain at least 6 characters.")
            return
        }
        if (password != confirmPass) {
            onComplete(false, "Passwords do not match.")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.register(name, email, phone, city, area, bloodGroup)
            _isLoading.value = false
            if (result.isSuccess) {
                _currentScreen.value = Screen.Main
                onComplete(true, null)
            } else {
                onComplete(false, "Registration failed.")
            }
        }
    }

    fun logout() {
        repository.logout()
        _selectedTab.value = 0
        _requestsSubTab.value = 0
        _selectedDonor.value = null
        _selectedRequest.value = null
        _lastSubmittedRequest.value = null
        _currentScreen.value = Screen.Login
        showToast("Signed out successfully.")
    }

    fun toggleAvailability(isAvailable: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.setDonorAvailability(user.uid, isAvailable)
            showToast(if (isAvailable) "You are now marked as Available for donations" else "You won't appear in donor searches while unavailable")
        }
    }

    fun registerAsDonor(
        name: String,
        age: Int,
        bloodGroup: String,
        phone: String,
        city: String,
        area: String,
        lastDonationDate: String,
        availability: Boolean,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val user = currentUser.value ?: return
        if (name.isBlank()) {
            onComplete(false, "Name is required.")
            return
        }
        if (phone.isBlank()) {
            onComplete(false, "Phone number is required.")
            return
        }
        if (city.isBlank()) {
            onComplete(false, "Please enter your city.")
            return
        }
        if (area.isBlank()) {
            onComplete(false, "Please enter your area.")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateDonorStatus(
                uid = user.uid,
                isDonor = true,
                availability = availability,
                phone = phone,
                city = city,
                area = area,
                bloodGroup = bloodGroup,
                lastDonationDate = lastDonationDate
            )
            _isLoading.value = false
            showToast("Successfully registered as a Blood Donor!")
            _currentScreen.value = Screen.Main
            onComplete(true, null)
        }
    }

    fun updateProfile(
        name: String,
        phone: String,
        city: String,
        area: String,
        bloodGroup: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val current = currentUser.value ?: return
        if (name.isBlank()) {
            onComplete(false, "Name cannot be empty.")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val updated = current.copy(
                name = name.trim(),
                phone = phone.trim(),
                city = city.trim(),
                area = area.trim(),
                bloodGroup = bloodGroup
            )
            repository.updateCurrentUser(updated)
            _isLoading.value = false
            showToast("Profile updated successfully")
            _currentScreen.value = Screen.Main
            selectTab(3) // Profile tab
            onComplete(true, null)
        }
    }

    fun submitBloodRequest(
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
        assignedDonorName: String? = null,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (hospitalName.isBlank()) {
            onComplete(false, "Hospital name is required.")
            return
        }
        if (location.isBlank() || city.isBlank()) {
            onComplete(false, "Hospital location and city are required.")
            return
        }
        if (requiredDate.isBlank()) {
            onComplete(false, "Required date is required.")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val created = repository.createBloodRequest(
                bloodGroup = bloodGroup,
                unitsRequired = unitsRequired,
                hospitalName = hospitalName,
                location = location,
                city = city,
                requiredDate = requiredDate,
                isEmergency = isEmergency,
                message = message,
                patientName = patientName,
                operationDetails = operationDetails,
                creditsToRedeem = creditsToRedeem,
                assignedDonorId = assignedDonorId,
                assignedDonorName = assignedDonorName
            )
            _isLoading.value = false
            _lastSubmittedRequest.value = created
            _currentScreen.value = Screen.RequestConfirmation
            onComplete(true, null)
        }
    }

    fun pledgeDonation(requestId: String, packetsDonated: Int, onComplete: () -> Unit = {}) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.pledgeBloodDonation(requestId, user.uid, user.name, packetsDonated)
            showToast("Thank you! Donated $packetsDonated packet(s). You earned $packetsDonated Emergency Blood Credit(s)!")
            val current = _selectedRequest.value
            if (current != null && current.requestId == requestId) {
                val newFulfilled = current.unitsFulfilled + packetsDonated
                val newStatus = if (newFulfilled >= current.unitsRequired) "Completed" else "Partially Fulfilled"
                _selectedRequest.value = current.copy(
                    unitsFulfilled = newFulfilled,
                    status = newStatus,
                    assignedDonorId = user.uid,
                    assignedDonorName = user.name
                )
            }
            onComplete()
        }
    }

    fun acceptRequest(requestId: String) {
        pledgeDonation(requestId, 1)
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            repository.rejectRequest(requestId)
            showToast("Request declined.")
            _selectedRequest.value = _selectedRequest.value?.copy(status = "Rejected")
        }
    }

    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, "Cancelled")
            showToast("Blood request cancelled.")
            _selectedRequest.value = _selectedRequest.value?.copy(status = "Cancelled")
        }
    }

    fun completeRequest(requestId: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, "Completed")
            showToast("Blood request marked as completed.")
            _selectedRequest.value = _selectedRequest.value?.copy(status = "Completed")
        }
    }
}
