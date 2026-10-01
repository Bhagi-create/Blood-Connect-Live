package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodRequestEntity
import com.example.data.model.UserEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.RequestCard
import com.example.ui.theme.BloodRedPrimary

import androidx.compose.material.icons.filled.Warning
import com.example.ui.theme.EmergencyOrange
import com.example.ui.theme.EmergencyOrangeBright

@Composable
fun RequestsScreen(
    currentUser: UserEntity?,
    emergencyRequests: List<BloodRequestEntity>,
    myRequests: List<BloodRequestEntity>,
    donorRequests: List<BloodRequestEntity>,
    selectedSubTab: Int, // 0 = Live Emergencies, 1 = My Requests, 2 = Donor Requests
    onSubTabChanged: (Int) -> Unit,
    onRequestClick: (BloodRequestEntity) -> Unit,
    onAcceptRequest: (BloodRequestEntity) -> Unit,
    onRejectRequest: (BloodRequestEntity) -> Unit,
    onNavigateBecomeDonor: () -> Unit,
    onNavigateCreateRequest: () -> Unit
) {
    var statusFilter by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Pending", "Accepted", "Completed")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("requests_screen")
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Blood Requests",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Live community emergencies and patient requests",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onNavigateCreateRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("requests_header_create_btn")
                    ) {
                        Text("+ Request", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Segmented 3-tab switch: [ Live Emergencies ] [ My Requests ] [ Donor Incoming ]
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        // Tab 0: Live Emergencies
                        Box(
                            modifier = Modifier
                                .weight(1.1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedSubTab == 0) EmergencyOrangeBright else Color.Transparent)
                                .clickable { onSubTabChanged(0) }
                                .padding(vertical = 8.dp)
                                .testTag("subtab_emergency_requests"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (selectedSubTab == 0) Color.White else EmergencyOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Live SOS (${emergencyRequests.size})",
                                    color = if (selectedSubTab == 0) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Tab 1: My Requests
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedSubTab == 1) BloodRedPrimary else Color.Transparent)
                                .clickable { onSubTabChanged(1) }
                                .padding(vertical = 8.dp)
                                .testTag("subtab_my_requests"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Mine (${myRequests.size})",
                                color = if (selectedSubTab == 1) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }

                        // Tab 2: Donor Incoming
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selectedSubTab == 2) BloodRedPrimary else Color.Transparent)
                                .clickable { onSubTabChanged(2) }
                                .padding(vertical = 8.dp)
                                .testTag("subtab_donor_requests"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Donor (${donorRequests.size})",
                                color = if (selectedSubTab == 2) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { opt ->
                val isSelected = statusFilter == opt
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) BloodRedPrimary else MaterialTheme.colorScheme.surface,
                    shadowElevation = if (isSelected) 0.dp else 1.dp,
                    modifier = Modifier.clickable { statusFilter = opt }
                ) {
                    Text(
                        text = opt,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Main List Content based on Selected Tab
        when (selectedSubTab) {
            0 -> {
                // Tab 0: Live Emergency Requests (Visible to all users!)
                val filtered = emergencyRequests.filter {
                    if (statusFilter == "All") true else it.status.equals(statusFilter, ignoreCase = true)
                }

                if (filtered.isEmpty()) {
                    EmptyStateView(
                        title = "No Emergency Requests",
                        description = if (emergencyRequests.isEmpty()) "There are no active emergency blood requests at this moment." else "No emergency requests with status '$statusFilter'.",
                        icon = Icons.Default.Warning,
                        actionButtonText = "Create Emergency Request",
                        onActionClick = onNavigateCreateRequest
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered, key = { "emergency_${it.requestId}" }) { req ->
                            RequestCard(
                                request = req,
                                onClick = onRequestClick,
                                onAccept = onAcceptRequest,
                                onReject = onRejectRequest,
                                isDonorView = currentUser?.isDonor == true
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }

            1 -> {
                // Tab 1: My Sent Requests
                val filtered = myRequests.filter {
                    if (statusFilter == "All") true else it.status.equals(statusFilter, ignoreCase = true)
                }

                if (filtered.isEmpty()) {
                    EmptyStateView(
                        title = "No Personal Blood Requests",
                        description = if (myRequests.isEmpty()) "You haven't posted any blood requests from this account yet. Tap below to create one." else "No requests with status '$statusFilter'.",
                        icon = Icons.Default.Assignment,
                        actionButtonText = if (myRequests.isEmpty()) "Request Blood Now" else null,
                        onActionClick = onNavigateCreateRequest
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered, key = { "my_${it.requestId}" }) { req ->
                            RequestCard(
                                request = req,
                                onClick = onRequestClick,
                                isDonorView = false
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }

            else -> {
                // Tab 2: Donor Incoming Requests
                if (currentUser?.isDonor != true) {
                    EmptyStateView(
                        title = "Register as a Donor",
                        description = "You need to be registered as an active blood donor to receive and accept targeted donation requests from patients.",
                        icon = Icons.Default.VolunteerActivism,
                        actionButtonText = "Become a Donor",
                        onActionClick = onNavigateBecomeDonor
                    )
                } else {
                    val filtered = donorRequests.filter {
                        if (statusFilter == "All") true else it.status.equals(statusFilter, ignoreCase = true)
                    }

                    if (filtered.isEmpty()) {
                        EmptyStateView(
                            title = "No Incoming Direct Requests",
                            description = "There are no pending direct donation requests matching your profile (${currentUser.bloodGroup}) right now.",
                            icon = Icons.Default.Assignment
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filtered, key = { "donor_${it.requestId}" }) { req ->
                                RequestCard(
                                    request = req,
                                    onClick = onRequestClick,
                                    onAccept = onAcceptRequest,
                                    onReject = onRejectRequest,
                                    isDonorView = true
                                )
                            }
                            item { Spacer(modifier = Modifier.height(24.dp)) }
                        }
                    }
                }
            }
        }
    }
}
