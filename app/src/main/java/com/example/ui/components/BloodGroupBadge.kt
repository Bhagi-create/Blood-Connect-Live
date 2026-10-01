package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BloodRedAccent
import com.example.ui.theme.BloodRedLight
import com.example.ui.theme.BloodRedPrimary

val BLOOD_GROUPS = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
val BLOOD_GROUPS_WITH_ANY = listOf("Any", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

@Composable
fun BloodGroupBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (isLarge) 16.dp else 10.dp),
        color = BloodRedLight,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isLarge) 14.dp else 10.dp,
                vertical = if (isLarge) 8.dp else 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = BloodRedPrimary,
                modifier = Modifier.size(if (isLarge) 18.dp else 14.dp)
            )
            Text(
                text = bloodGroup,
                color = BloodRedPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = if (isLarge) 16.sp else 13.sp
            )
        }
    }
}

@Composable
fun BloodGroupAvatarBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(BloodRedLight)
            .border(2.dp, BloodRedPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = bloodGroup,
            color = BloodRedPrimary,
            fontWeight = FontWeight.Black,
            fontSize = 17.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BloodGroupSelectorChips(
    selectedGroup: String,
    onGroupSelected: (String) -> Unit,
    includeAny: Boolean = false,
    modifier: Modifier = Modifier
) {
    val options = if (includeAny) BLOOD_GROUPS_WITH_ANY else BLOOD_GROUPS

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { group ->
            val isSelected = (group == selectedGroup) || (includeAny && group == "Any" && (selectedGroup == "Any" || selectedGroup.isEmpty() || selectedGroup == "Any Blood Group"))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) BloodRedPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onGroupSelected(group) }
            ) {
                Text(
                    text = group,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
