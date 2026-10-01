package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BloodConnectBottomNav
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.BecomeDonorScreen
import com.example.ui.screens.BloodRequestScreen
import com.example.ui.screens.DonorDetailsScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.ForgotPasswordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.RequestConfirmationScreen
import com.example.ui.screens.RequestDetailsScreen
import com.example.ui.screens.RequestsScreen
import com.example.ui.screens.SearchResultsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.BloodConnectTheme
import com.example.ui.viewmodel.BloodConnectViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun BloodConnectApp(
    viewModel: BloodConnectViewModel = viewModel()
) {
    BloodConnectTheme {
        val currentScreen by viewModel.currentScreen.collectAsState()
        val selectedTab by viewModel.selectedTab.collectAsState()
        val requestsSubTab by viewModel.requestsSubTab.collectAsState()
        val currentUser by viewModel.currentUser.collectAsState()
        val emergencyRequests by viewModel.emergencyRequests.collectAsState()
        val availableDonors by viewModel.availableDonors.collectAsState()
        val searchResults by viewModel.searchResults.collectAsState()
        val myRequests by viewModel.myRequests.collectAsState()
        val donorRequests by viewModel.donorRequests.collectAsState()
        val selectedDonor by viewModel.selectedDonor.collectAsState()
        val selectedRequest by viewModel.selectedRequest.collectAsState()
        val lastSubmittedRequest by viewModel.lastSubmittedRequest.collectAsState()
        val isEmergencyRequest by viewModel.isEmergencyRequest.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()
        val toastMessage by viewModel.toastMessage.collectAsState()

        val searchBloodGroup by viewModel.searchBloodGroup.collectAsState()
        val searchCity by viewModel.searchCity.collectAsState()
        val searchArea by viewModel.searchArea.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(toastMessage) {
            toastMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearToast()
            }
        }

        // Handle hardware / gesture back button
        BackHandler(enabled = currentScreen != Screen.Splash) {
            when {
                currentScreen != Screen.Main && currentScreen != Screen.Login && currentScreen != Screen.Onboarding -> {
                    viewModel.navigateTo(Screen.Main)
                }
                currentScreen == Screen.Main && selectedTab != 0 -> {
                    viewModel.selectTab(0)
                }
            }
        }

        if (currentScreen == Screen.Main) {
            // Main App Shell with Bottom Navigation Bar
            Scaffold(
                bottomBar = {
                    BloodConnectBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            currentUser = currentUser,
                            onSearchClick = { bg, city, area ->
                                viewModel.searchBloodGroup.value = bg
                                viewModel.searchCity.value = city
                                viewModel.searchArea.value = area
                                viewModel.navigateTo(Screen.SearchResults)
                            },
                            onRequestBloodClick = { isEmergency ->
                                viewModel.startBloodRequest(isEmergency)
                            },
                            onNavigateBecomeDonor = {
                                viewModel.navigateTo(Screen.BecomeDonor)
                            },
                            onNavigateMyRequests = {
                                viewModel.selectTab(2)
                                viewModel.setRequestsSubTab(1)
                            },
                            onNavigateEmergencyRequests = {
                                viewModel.selectTab(2)
                                viewModel.setRequestsSubTab(0)
                            },
                            onDonorClick = { donor ->
                                viewModel.selectDonor(donor)
                            },
                            onRequestClick = { req ->
                                viewModel.selectRequest(req)
                            },
                            onToggleAvailability = { isAvailable ->
                                viewModel.toggleAvailability(isAvailable)
                            },
                            emergencyRequests = emergencyRequests,
                            topDonors = availableDonors,
                            onLogout = { viewModel.logout() }
                        )

                        1 -> SearchScreen(
                            initialBloodGroup = searchBloodGroup,
                            initialCity = if (searchCity.isEmpty()) "Hyderabad" else searchCity,
                            initialArea = searchArea,
                            onTriggerSearch = { bg, city, area ->
                                viewModel.searchBloodGroup.value = bg
                                viewModel.searchCity.value = city
                                viewModel.searchArea.value = area
                                viewModel.navigateTo(Screen.SearchResults)
                            }
                        )

                        2 -> RequestsScreen(
                            currentUser = currentUser,
                            emergencyRequests = emergencyRequests,
                            myRequests = myRequests,
                            donorRequests = donorRequests,
                            selectedSubTab = requestsSubTab,
                            onSubTabChanged = { viewModel.setRequestsSubTab(it) },
                            onRequestClick = { req ->
                                viewModel.selectRequest(req)
                            },
                            onAcceptRequest = { req ->
                                viewModel.acceptRequest(req.requestId)
                            },
                            onRejectRequest = { req ->
                                viewModel.rejectRequest(req.requestId)
                            },
                            onNavigateBecomeDonor = {
                                viewModel.navigateTo(Screen.BecomeDonor)
                            },
                            onNavigateCreateRequest = {
                                viewModel.startBloodRequest(false)
                            }
                        )

                        3 -> ProfileScreen(
                            currentUser = currentUser,
                            onNavigateEditProfile = { viewModel.navigateTo(Screen.EditProfile) },
                            onNavigateBecomeDonor = { viewModel.navigateTo(Screen.BecomeDonor) },
                            onNavigateMyRequests = {
                                viewModel.selectTab(2)
                                viewModel.setRequestsSubTab(1)
                            },
                            onNavigateDonorRequests = {
                                viewModel.selectTab(2)
                                viewModel.setRequestsSubTab(2)
                            },
                            onNavigateSettings = { viewModel.navigateTo(Screen.Settings) },
                            onNavigateAbout = { viewModel.navigateTo(Screen.About) },
                            onToggleAvailability = { viewModel.toggleAvailability(it) },
                            onLogout = { viewModel.logout() }
                        )
                    }
                }
            }
        } else {
            // Standalone screens (Splash, Login, Register, Forms, Details)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (currentScreen) {
                    Screen.Splash -> {
                        SplashScreen(
                            isLoggedIn = viewModel.isUserLoggedIn(),
                            onNavigateNext = { isLoggedIn ->
                                if (isLoggedIn) {
                                    viewModel.navigateTo(Screen.Main)
                                } else {
                                    viewModel.navigateTo(Screen.Login)
                                }
                            }
                        )
                    }

                    Screen.Onboarding -> {
                        OnboardingScreen(
                            onFinish = { viewModel.completeOnboarding() }
                        )
                    }

                    Screen.Login -> {
                        LoginScreen(
                            onLoginSuccess = { viewModel.navigateTo(Screen.Main) },
                            onNavigateRegister = { viewModel.navigateTo(Screen.Register) },
                            onNavigateForgotPassword = { viewModel.navigateTo(Screen.ForgotPassword) },
                            isLoading = isLoading,
                            onLoginClick = { email, cb ->
                                viewModel.login(email, onComplete = cb)
                            }
                        )
                    }

                    Screen.Register -> {
                        RegisterScreen(
                            onRegisterClick = { name, email, pass, cpass, phone, city, area, bg, cb ->
                                viewModel.register(name, email, pass, cpass, phone, city, area, bg, cb)
                            },
                            onNavigateLogin = { viewModel.navigateTo(Screen.Login) },
                            isLoading = isLoading
                        )
                    }

                    Screen.ForgotPassword -> {
                        ForgotPasswordScreen(
                            onBackToLogin = { viewModel.navigateTo(Screen.Login) }
                        )
                    }

                    Screen.SearchResults -> {
                        SearchResultsScreen(
                            bloodGroup = searchBloodGroup,
                            city = searchCity,
                            area = searchArea,
                            donors = searchResults,
                            onBack = { viewModel.navigateTo(Screen.Main) },
                            onViewProfile = { donor -> viewModel.selectDonor(donor) }
                        )
                    }

                    Screen.DonorDetails -> {
                        DonorDetailsScreen(
                            donor = selectedDonor,
                            onBack = { viewModel.navigateTo(Screen.Main) },
                            onSendBloodRequest = { donor ->
                                viewModel.startBloodRequest(false, donor)
                            }
                        )
                    }

                    Screen.BecomeDonor, Screen.EditDonorProfile -> {
                        BecomeDonorScreen(
                            currentUser = currentUser,
                            onBack = { viewModel.navigateTo(Screen.Main) },
                            onSubmitDonor = { name, age, bg, phone, city, area, date, avail, cb ->
                                viewModel.registerAsDonor(name, age, bg, phone, city, area, date, avail, cb)
                            },
                            isLoading = isLoading
                        )
                    }

                    Screen.BloodRequest -> {
                        BloodRequestScreen(
                            assignedDonor = selectedDonor,
                            initialIsEmergency = isEmergencyRequest,
                            currentUser = currentUser,
                            onBack = { viewModel.navigateTo(Screen.Main) },
                            onSubmitRequest = { bg, units, hosp, loc, city, reqDate, isEmerg, msg, patName, opDetails, credits, dId, dName, cb ->
                                viewModel.submitBloodRequest(bg, units, hosp, loc, city, reqDate, isEmerg, msg, patName, opDetails, credits, dId, dName, cb)
                            },
                            isLoading = isLoading
                        )
                    }

                    Screen.RequestConfirmation -> {
                        RequestConfirmationScreen(
                            request = lastSubmittedRequest,
                            onViewMyRequests = {
                                viewModel.selectTab(2)
                                viewModel.setRequestsSubTab(if (lastSubmittedRequest?.isEmergency == true) 0 else 1)
                                viewModel.navigateTo(Screen.Main)
                            },
                            onBackHome = {
                                viewModel.selectTab(0)
                                viewModel.navigateTo(Screen.Main)
                            }
                        )
                    }

                    Screen.RequestDetails -> {
                        RequestDetailsScreen(
                            request = selectedRequest,
                            currentUser = currentUser,
                            onBack = { viewModel.navigateTo(Screen.Main) },
                            onAcceptRequest = { reqId -> viewModel.acceptRequest(reqId) },
                            onRejectRequest = { reqId -> viewModel.rejectRequest(reqId) },
                            onCancelRequest = { reqId -> viewModel.cancelRequest(reqId) },
                            onCompleteRequest = { reqId -> viewModel.completeRequest(reqId) }
                        )
                    }

                    Screen.EditProfile -> {
                        EditProfileScreen(
                            currentUser = currentUser,
                            onBack = { viewModel.navigateTo(Screen.Main) },
                            onSaveProfile = { name, phone, city, area, bg, cb ->
                                viewModel.updateProfile(name, phone, city, area, bg, cb)
                            },
                            isLoading = isLoading
                        )
                    }

                    Screen.Settings -> {
                        SettingsScreen(
                            onBack = { viewModel.navigateTo(Screen.Main) },
                            onLogout = { viewModel.logout() }
                        )
                    }

                    Screen.About -> {
                        AboutScreen(
                            onBack = { viewModel.navigateTo(Screen.Main) }
                        )
                    }

                    Screen.Main -> { /* Handled above */ }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
