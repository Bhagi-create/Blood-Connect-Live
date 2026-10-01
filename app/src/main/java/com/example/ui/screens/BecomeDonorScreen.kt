package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.UnavailableGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeDonorScreen(
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onSubmitDonor: (
        name: String,
        age: Int,
        bloodGroup: String,
        phone: String,
        city: String,
        area: String,
        lastDonationDate: String,
        availability: Boolean,
        onComplete: (Boolean, String?) -> Unit
    ) -> Unit,
    isLoading: Boolean
) {
    val isEditMode = currentUser?.isDonor == true

    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var ageText by remember { mutableStateOf(if ((currentUser?.age ?: 0) > 0) currentUser?.age.toString() else "25") }
    var bloodGroup by remember { mutableStateOf(currentUser?.bloodGroup ?: "O+") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var city by remember { mutableStateOf(currentUser?.city?.ifEmpty { "Hyderabad" } ?: "Hyderabad") }
    var area by remember { mutableStateOf(currentUser?.area?.ifEmpty { "Madhapur" } ?: "Madhapur") }
    var lastDonationDate by remember { mutableStateOf(currentUser?.lastDonationDate ?: "") }
    var availability by remember { mutableStateOf(currentUser?.availability ?: true) }
    var agreedToContact by remember { mutableStateOf(currentUser?.agreedToContact ?: true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Donor Profile" else "Become a Blood Donor",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("become_donor_back_btn")) {
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
                .testTag("become_donor_screen")
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = if (isEditMode) "Update Your Donor Information" else "Join the BloodConnect Hero Network",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Every donation can save up to 3 lives. Help hospitals and patients during emergencies.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text("Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BloodRedPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("donor_form_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Age & Phone Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { ageText = it; errorMessage = null },
                            label = { Text("Age *") },
                            placeholder = { Text("18-65") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.7f).testTag("donor_form_age_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloodRedPrimary,
                                focusedLabelColor = BloodRedPrimary
                            )
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it; errorMessage = null },
                            label = { Text("Phone Number *") },
                            placeholder = { Text("+91 98490 12345") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BloodRedPrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f).testTag("donor_form_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloodRedPrimary,
                                focusedLabelColor = BloodRedPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Blood Group
                    Text(
                        text = "Blood Group *",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BloodGroupSelectorChips(
                        selectedGroup = bloodGroup,
                        onGroupSelected = { bloodGroup = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
                            modifier = Modifier.weight(1f).testTag("donor_form_city_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloodRedPrimary,
                                focusedLabelColor = BloodRedPrimary
                            )
                        )

                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it; errorMessage = null },
                            label = { Text("Area / Locality *") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = BloodRedPrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("donor_form_area_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloodRedPrimary,
                                focusedLabelColor = BloodRedPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Last Donation Date
                    OutlinedTextField(
                        value = lastDonationDate,
                        onValueChange = { lastDonationDate = it },
                        label = { Text("Last Donation Date (Optional)") },
                        placeholder = { Text("e.g. 15 Jan 2026, or Leave Empty if First Time") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = BloodRedPrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("donor_form_last_date_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Availability Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current Availability",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (availability) "Available for donation requests" else "Temporarily unavailable",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (availability) AvailableGreen else UnavailableGrey
                            )
                        }

                        Switch(
                            checked = availability,
                            onCheckedChange = { availability = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AvailableGreen,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = UnavailableGrey
                            ),
                            modifier = Modifier.testTag("donor_form_availability_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Consent Checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreedToContact,
                            onCheckedChange = { agreedToContact = it; errorMessage = null },
                            colors = CheckboxDefaults.colors(checkedColor = BloodRedPrimary),
                            modifier = Modifier.testTag("donor_form_consent_checkbox")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I agree to be contacted for blood donation requests and confirm my details are accurate.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
                    val age = ageText.toIntOrNull()
                    if (name.isBlank()) {
                        errorMessage = "Please enter your full name."
                        return@Button
                    }
                    if (age == null || age < 18 || age > 65) {
                        errorMessage = "Donor age must be between 18 and 65 years."
                        return@Button
                    }
                    if (phone.isBlank() || phone.length < 8) {
                        errorMessage = "Please enter a valid phone number."
                        return@Button
                    }
                    if (city.isBlank() || area.isBlank()) {
                        errorMessage = "Please enter your city and area."
                        return@Button
                    }
                    if (!agreedToContact) {
                        errorMessage = "You must agree to be contacted for blood donation requests."
                        return@Button
                    }

                    onSubmitDonor(
                        name,
                        age,
                        bloodGroup,
                        phone,
                        city,
                        area,
                        lastDonationDate,
                        availability
                    ) { success, err ->
                        if (!success) {
                            errorMessage = err ?: "Failed to save donor profile"
                        }
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("donor_form_submit_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text(
                        text = if (isEditMode) "Save Changes" else "Register as Donor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
