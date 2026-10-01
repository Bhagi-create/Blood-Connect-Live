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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BloodGroupBadge
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.BloodRedLight
import com.example.ui.theme.BloodRedPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Blood Guide & Eligibility", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("about_back_btn")) {
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
                .testTag("about_screen")
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // About App Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = BloodRedPrimary)
                        Text("BloodConnect Mission", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "BloodConnect is a life-saving mobile platform bridging the gap between individuals needing blood during medical emergencies and willing, verified blood donors.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Blood Compatibility Chart
            Text(
                text = "Blood Compatibility Chart",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CompatibilityRow(type = "O-", canGiveTo = "All Blood Groups (Universal Donor)", canReceiveFrom = "O- only")
                    CompatibilityRow(type = "O+", canGiveTo = "O+, A+, B+, AB+", canReceiveFrom = "O+, O-")
                    CompatibilityRow(type = "A-", canGiveTo = "A-, A+, AB-, AB+", canReceiveFrom = "A-, O-")
                    CompatibilityRow(type = "A+", canGiveTo = "A+, AB+", canReceiveFrom = "A+, A-, O+, O-")
                    CompatibilityRow(type = "B-", canGiveTo = "B-, B+, AB-, AB+", canReceiveFrom = "B-, O-")
                    CompatibilityRow(type = "B+", canGiveTo = "B+, AB+", canReceiveFrom = "B+, B-, O+, O-")
                    CompatibilityRow(type = "AB-", canGiveTo = "AB-, AB+", canReceiveFrom = "AB-, A-, B-, O-")
                    CompatibilityRow(type = "AB+", canGiveTo = "AB+ only", canReceiveFrom = "All Blood Groups (Universal Recipient)")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Donor Eligibility Guidelines
            Text(
                text = "Who Can Donate Blood?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    EligibilityItem(text = "Age between 18 and 65 years old")
                    EligibilityItem(text = "Weight at least 45 kg (or 50 kg for certain procedures)")
                    EligibilityItem(text = "Hemoglobin level at least 12.5 g/dL")
                    EligibilityItem(text = "Pulse rate 60 to 100 beats per minute, regular")
                    EligibilityItem(text = "At least 3 months gap since your last whole blood donation")
                    EligibilityItem(text = "No major surgery within the last 6 months")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Emergency Helplines
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BloodRedLight)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = BloodRedPrimary)
                    Column {
                        Text("National Emergency Blood Helpline", fontWeight = FontWeight.Bold, color = BloodRedPrimary)
                        Text("Dial 108 or 104 (24x7 Ambulance & Blood Support)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CompatibilityRow(type: String, canGiveTo: String, canReceiveFrom: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BloodGroupBadge(bloodGroup = type)
            Text(text = "Compatibility", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "• Can donate to: $canGiveTo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        Text(text = "• Can receive from: $canReceiveFrom", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EligibilityItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AvailableGreen, modifier = Modifier.size(18.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
