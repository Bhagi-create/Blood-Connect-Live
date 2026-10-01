package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodRequestEntity
import com.example.data.model.UserEntity
import com.example.ui.components.BloodCompatibilityDialog
import com.example.ui.components.BloodGroupBadge
import com.example.ui.components.PledgeDonationDialog
import com.example.ui.components.SafeBridgeCallDialog
import com.example.ui.components.verticalScrollbar
import com.example.ui.theme.AvailableGreen
import com.example.ui.theme.AvailableGreenBg
import com.example.ui.theme.BloodRedLight
import com.example.ui.theme.BloodRedPrimary
import com.example.ui.theme.EmergencyBg
import com.example.ui.theme.EmergencyOrangeBright
import com.example.util.BloodCompatibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailsScreen(
    request: BloodRequestEntity?,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onAcceptRequest: (String) -> Unit,
    onRejectRequest: (String) -> Unit,
    onCancelRequest: (String) -> Unit,
    onCompleteRequest: (String) -> Unit,
    onPledgeDonation: (String, Int) -> Unit = { reqId, units -> onAcceptRequest(reqId) }
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showSafeBridgeDialog by remember { mutableStateOf(false) }
    var showCompatibilityDialog by remember { mutableStateOf(false) }
    var showPledgeDialog by remember { mutableStateOf(false) }

    if (request == null) {
        onBack()
        return
    }

    val isRequester = request.requesterId == currentUser?.uid
    val isAssignedDonor = request.assignedDonorId == currentUser?.uid
    val isEligibleDonor = currentUser?.isDonor == true && !isRequester && !request.isFullyCompleted

    val isCompatible = if (currentUser != null && currentUser.bloodGroup.isNotBlank()) {
        BloodCompatibility.canDonate(currentUser.bloodGroup, request.bloodGroup)
    } else false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("req_details_back_btn")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCompatibilityDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Blood Compatibility Guide",
                            tint = BloodRedPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("request_details_screen")
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Header Card: Priority & Blood Type
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (request.isEmergency && !request.isFullyCompleted) EmergencyBg else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = request.requestId,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BloodRedPrimary
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                request.isFullyCompleted -> AvailableGreenBg
                                request.isPartiallyFulfilled -> Color(0xFFE1F5FE)
                                request.status == "Accepted" -> AvailableGreenBg
                                request.status == "Rejected" -> Color(0xFFFFEBEE)
                                request.status == "Cancelled" -> Color(0xFFEEEEEE)
                                else -> Color(0xFFFFF3E0)
                            }
                        ) {
                            Text(
                                text = when {
                                    request.isFullyCompleted -> "Completed (All Fulfilled)"
                                    request.isPartiallyFulfilled -> "Partially Fulfilled"
                                    else -> request.status
                                },
                                color = when {
                                    request.isFullyCompleted -> AvailableGreen
                                    request.isPartiallyFulfilled -> Color(0xFF0288D1)
                                    request.status == "Accepted" -> AvailableGreen
                                    request.status == "Rejected" -> Color(0xFFD32F2F)
                                    request.status == "Cancelled" -> Color(0xFF757575)
                                    else -> Color(0xFFF57C00)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (request.isEmergency && !request.isFullyCompleted) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = EmergencyOrangeBright, modifier = Modifier.size(16.dp))
                            Text(
                                text = "HIGH-PRIORITY EMERGENCY SOS",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = EmergencyOrangeBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BloodGroupBadge(
                            bloodGroup = "${request.unitsRequired} Unit${if (request.unitsRequired > 1) "s" else ""} • ${request.bloodGroup}",
                            isLarge = true
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BloodRedLight,
                            modifier = Modifier.clickable { showCompatibilityDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = BloodRedPrimary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Compatibility",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BloodRedPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // LIVE PACKET FULFILLMENT STATUS BAR
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Packets Fulfilled:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${request.unitsFulfilled} of ${request.unitsRequired} Packets Received",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (request.isFullyCompleted) AvailableGreen else BloodRedPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { request.progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp),
                                color = if (request.isFullyCompleted) AvailableGreen else BloodRedPrimary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = when {
                                    request.isFullyCompleted -> "🎉 Full Requirement Met! All ${request.unitsRequired} blood packets received."
                                    request.unitsFulfilled > 0 -> "⚠️ ${request.unitsRemaining} packet(s) still urgently needed to save the patient."
                                    else -> "⏳ 0 of ${request.unitsRequired} received. All ${request.unitsRequired} packet(s) needed immediately."
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (request.isFullyCompleted) AvailableGreen else EmergencyOrangeBright
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PATIENT & MEDICAL PROCEDURE DETAILS CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Patient & Operation Info",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    RequestDetailRow(
                        icon = Icons.Default.Person,
                        label = "Patient Name",
                        value = if (request.patientName.isNotBlank()) request.patientName else request.requesterName
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RequestDetailRow(
                        icon = Icons.Default.MedicalServices,
                        label = "Operation / Clinical Need",
                        value = if (request.operationDetails.isNotBlank()) request.operationDetails else "Emergency Medical Procedure"
                    )

                    if (currentUser != null && currentUser.isDonor) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCompatible) AvailableGreenBg else Color(0xFFFFEBEE),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCompatible) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isCompatible) AvailableGreen else Color(0xFFD32F2F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isCompatible)
                                            "Compatible Donor: Your ${currentUser.bloodGroup} can donate to ${request.bloodGroup}!"
                                        else
                                            "Group Incompatible: Your ${currentUser.bloodGroup} cannot donate to ${request.bloodGroup}.",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCompatible) AvailableGreen else Color(0xFFD32F2F)
                                    )
                                    Text(
                                        text = "Tap compatibility icon above for the full cross-match chart.",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // HOSPITAL & LIVE ROUTE NAVIGATION CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hospital & Directions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "Blood Bank Open 24/7",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AvailableGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    RequestDetailRow(
                        icon = Icons.Default.LocalHospital,
                        label = "Hospital Center",
                        value = request.hospitalName
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RequestDetailRow(
                        icon = Icons.Default.LocationOn,
                        label = "Hospital Address",
                        value = "${request.location}, ${request.city}"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RequestDetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Date / Urgency",
                        value = request.requiredDate
                    )

                    if (request.message.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        RequestDetailRow(
                            icon = Icons.Default.Info,
                            label = "Notes / Instructions",
                            value = request.message
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step-by-step route directions
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "📍 Quick Route & Reception Steps:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text("1. Head to Main Emergency Gate at ${request.hospitalName}.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("2. Present BloodConnect Token #${request.requestId} at the Blood Bank Counter.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("3. On-duty medical officer will fast-track your donation.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Route in Google Maps Button
                    Button(
                        onClick = {
                            val mapUri = Uri.parse("geo:0,0?q=" + Uri.encode("${request.hospitalName}, ${request.location}, ${request.city}"))
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            context.startActivity(mapIntent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("directions_maps_btn")
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Get Route Directions in Google Maps", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PRIVACY-PROTECTED CALL VIA SAFEBRIDGE COMPANY NUMBER
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Verified Contact",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AvailableGreen, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Private SafeBridge",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AvailableGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    RequestDetailRow(
                        icon = Icons.Default.Person,
                        label = "Requester / Family Contact",
                        value = request.requesterName
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Company hotline display
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F8E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "🔒 Number Masking Active",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AvailableGreen
                            )
                            Text(
                                text = "Direct phone numbers are never shared publicly. Tapping call dials the company switchboard (1800-200-4567) which safely connects to the attendant.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showSafeBridgeDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("safebridge_call_btn")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call Attendant via SafeBridge", fontWeight = FontWeight.Bold)
                    }

                    if (request.assignedDonorName != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        RequestDetailRow(
                            icon = Icons.Default.Check,
                            label = "Active Donor Pledged",
                            value = "${request.assignedDonorName} (${request.unitsFulfilled} Units)"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // USER'S BLOOD CREDIT VAULT STATUS
            if (currentUser != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BloodRedLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = BloodRedPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Your Emergency Blood Vault: ${currentUser.bloodCredits} Free Credits",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BloodRedPrimary
                            )
                            Text(
                                text = "Every packet you donate earns 1 credit. Free packets guaranteed by BloodConnect whenever you need them.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // CONTEXTUAL ACTION BUTTONS
            if (isEligibleDonor && !request.isFullyCompleted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onRejectRequest(request.requestId) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("details_decline_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Decline")
                    }

                    Button(
                        onClick = { showPledgeDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                            .testTag("details_pledge_btn")
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pledge Blood", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (isRequester) {
                if (!request.isFullyCompleted && request.status != "Cancelled") {
                    OutlinedButton(
                        onClick = { onCancelRequest(request.requestId) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("details_cancel_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cancel This Request", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
                if (request.unitsFulfilled > 0 && !request.isFullyCompleted) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onCompleteRequest(request.requestId) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AvailableGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("details_complete_btn")
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark Donation Completed (${request.unitsFulfilled} Packets)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // DIALOGS
    if (showSafeBridgeDialog) {
        SafeBridgeCallDialog(
            request = request,
            onDismiss = { showSafeBridgeDialog = false }
        )
    }

    if (showCompatibilityDialog) {
        BloodCompatibilityDialog(
            initialSelectedGroup = request.bloodGroup,
            onDismiss = { showCompatibilityDialog = false }
        )
    }

    if (showPledgeDialog) {
        PledgeDonationDialog(
            request = request,
            onDismiss = { showPledgeDialog = false },
            onConfirmPledge = { packetsDonated ->
                onPledgeDonation(request.requestId, packetsDonated)
            }
        )
    }
}

@Composable
private fun RequestDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BloodRedPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
