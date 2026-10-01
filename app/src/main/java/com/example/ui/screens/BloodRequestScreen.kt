package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.BloodGroupSelectorChips
import com.example.ui.components.verticalScrollbar
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.BloodRedLight
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.EmergencyBg
import com.example.ui.theme.EmergencyOrange
import com.example.ui.theme.EmergencyOrangeBright

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodRequestScreen(
    initialBloodGroup: String = "O+",
    initialIsEmergency: Boolean = false,
    assignedDonor: UserEntity? = null,
    currentUser: UserEntity? = null,
    onBack: () -> Unit,
    onSubmitRequest: (
        bloodGroup: String,
        unitsRequired: Int,
        hospitalName: String,
        location: String,
        city: String,
        requiredDate: String,
        isEmergency: Boolean,
        message: String,
        patientName: String,
        operationDetails: String,
        creditsToRedeem: Int,
        assignedDonorId: String?,
        assignedDonorName: String?,
        onComplete: (Boolean, String?) -> Unit
    ) -> Unit,
    isLoading: Boolean
) {
    val scrollState = rememberScrollState()

    var bloodGroup by remember { mutableStateOf(assignedDonor?.bloodGroup ?: initialBloodGroup) }
    var unitsRequired by remember { mutableIntStateOf(1) }
    var patientName by remember { mutableStateOf("") }
    var operationDetails by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("Apollo Hospital") }
    var location by remember { mutableStateOf("Road No 72, Film Nagar") }
    var city by remember { mutableStateOf(assignedDonor?.city ?: currentUser?.city?.ifBlank { "Hyderabad" } ?: "Hyderabad") }
    var requiredDate by remember { mutableStateOf("Immediate / Today") }
    var isEmergency by remember(initialIsEmergency) { mutableStateOf(initialIsEmergency) }
    var message by remember { mutableStateOf("") }
    var redeemCreditsChecked by remember { mutableStateOf(false) }
    var creditsToRedeem by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val userCreditsAvailable = currentUser?.bloodCredits ?: 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Blood", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("blood_request_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("blood_request_screen")
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Target donor banner if direct request
            if (assignedDonor != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloodRedLight)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = BloodRedPrimary)
                        Column {
                            Text(
                                text = "Sending direct request to:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${assignedDonor.name} (${assignedDonor.bloodGroup})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = BloodRedPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Emergency SOS Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEmergency) EmergencyBg else MaterialTheme.colorScheme.surface
                ),
                border = if (isEmergency) CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(EmergencyOrangeBright)
                ) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isEmergency) EmergencyOrangeBright else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Emergency SOS Broadcast",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isEmergency) EmergencyOrangeBright else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Instantly notifies and alerts all available donors in $city.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isEmergency,
                        onCheckedChange = { isEmergency = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmergencyOrangeBright
                        ),
                        modifier = Modifier.testTag("emergency_switch_toggle")
                    )
                }
            }

            // FREE BLOOD CREDIT REDEMPTION VAULT CARD
            if (userCreditsAvailable > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AvailableGreen.copy(alpha = 0.10f)),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AvailableGreen)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = AvailableGreen,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Column {
                                    Text(
                                        text = "Emergency Blood Vault Credit",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = AvailableGreen
                                    )
                                    Text(
                                        text = "You have $userCreditsAvailable free packet credit(s) available!",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Checkbox(
                                checked = redeemCreditsChecked,
                                onCheckedChange = { checked ->
                                    redeemCreditsChecked = checked
                                    creditsToRedeem = if (checked) minOf(userCreditsAvailable, unitsRequired) else 0
                                },
                                colors = CheckboxDefaults.colors(checkedColor = AvailableGreen),
                                modifier = Modifier.testTag("redeem_credits_checkbox")
                            )
                        }

                        if (redeemCreditsChecked) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "🎁 Redeeming ${creditsToRedeem.coerceAtLeast(1)} credit(s): Our team will coordinate and provide free blood packet(s) for your patient emergency!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AvailableGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Details Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Patient & Operation Information *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Patient Name
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it; errorMessage = null },
                        label = { Text("Patient Name *") },
                        placeholder = { Text("e.g. Master Aarav Sharma, Rajesh Roy") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BloodRedPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("req_patient_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Operation / Medical Procedure
                    OutlinedTextField(
                        value = operationDetails,
                        onValueChange = { operationDetails = it; errorMessage = null },
                        label = { Text("Operation / Clinical Need *") },
                        placeholder = { Text("e.g. Cardiac Bypass Surgery, Thalassemia Transfusion") },
                        leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null, tint = BloodRedPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("req_operation_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Required Blood Group *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    BloodGroupSelectorChips(
                        selectedGroup = bloodGroup,
                        onGroupSelected = { bloodGroup = it }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Units Required
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Units Required",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "$unitsRequired Unit${if (unitsRequired > 1) "s" else ""} (~${unitsRequired * 450} ml)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(36.dp),
                                onClick = {
                                    if (unitsRequired > 1) {
                                        unitsRequired--
                                        if (redeemCreditsChecked) {
                                            creditsToRedeem = minOf(userCreditsAvailable, unitsRequired)
                                        }
                                    }
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "$unitsRequired",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BloodRedLight,
                                modifier = Modifier.size(36.dp),
                                onClick = {
                                    if (unitsRequired < 10) {
                                        unitsRequired++
                                        if (redeemCreditsChecked) {
                                            creditsToRedeem = minOf(userCreditsAvailable, unitsRequired)
                                        }
                                    }
                                }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BloodRedPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hospital Name
                    OutlinedTextField(
                        value = hospitalName,
                        onValueChange = { hospitalName = it; errorMessage = null },
                        label = { Text("Hospital Name *") },
                        placeholder = { Text("e.g. Apollo Hospital, Jubilee Hills") },
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = BloodRedPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("req_hospital_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // City & Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it; errorMessage = null },
                            label = { Text("City *") },
                            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = BloodRedPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("req_city_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloodRedPrimary,
                                focusedLabelColor = BloodRedPrimary
                            )
                        )

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it; errorMessage = null },
                            label = { Text("Location / Area *") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = BloodRedPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("req_location_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloodRedPrimary,
                                focusedLabelColor = BloodRedPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Required Date
                    OutlinedTextField(
                        value = requiredDate,
                        onValueChange = { requiredDate = it; errorMessage = null },
                        label = { Text("Required Date *") },
                        placeholder = { Text("e.g. Today, Tomorrow, 24 Sep 2026") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BloodRedPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("req_date_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Additional Message
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Additional Message / Instructions") },
                        placeholder = { Text("e.g. Room number, Attendant contact instructions...") },
                        leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = BloodRedPrimary) },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("req_message_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (patientName.isBlank()) {
                        errorMessage = "Please enter the patient's name."
                        return@Button
                    }
                    if (operationDetails.isBlank()) {
                        errorMessage = "Please enter operation or clinical procedure details."
                        return@Button
                    }
                    if (hospitalName.isBlank()) {
                        errorMessage = "Please enter the hospital name."
                        return@Button
                    }
                    if (city.isBlank() || location.isBlank()) {
                        errorMessage = "Please enter hospital city and location."
                        return@Button
                    }
                    if (requiredDate.isBlank()) {
                        errorMessage = "Please specify when the blood is required."
                        return@Button
                    }

                    onSubmitRequest(
                        bloodGroup,
                        unitsRequired,
                        hospitalName,
                        location,
                        city,
                        requiredDate,
                        isEmergency,
                        message,
                        patientName,
                        operationDetails,
                        if (redeemCreditsChecked) creditsToRedeem.coerceAtLeast(1) else 0,
                        assignedDonor?.uid,
                        assignedDonor?.name
                    ) { success, err ->
                        if (!success) {
                            errorMessage = err ?: "Failed to submit request"
                        }
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEmergency) EmergencyOrangeBright else BloodRedPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_blood_request_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = if (isEmergency) "Broadcast Emergency SOS" else "Send Blood Request",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
