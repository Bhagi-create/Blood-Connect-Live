package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BloodGroupSelectorChips
import com.example.ui.theme.BloodRedPrimary

@Composable
fun SearchScreen(
    initialBloodGroup: String = "Any",
    initialCity: String = "Hyderabad",
    initialArea: String = "",
    onTriggerSearch: (bloodGroup: String, city: String, area: String) -> Unit
) {
    var selectedBloodGroup by remember { mutableStateOf(initialBloodGroup) }
    var city by remember { mutableStateOf(initialCity) }
    var area by remember { mutableStateOf(initialArea) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_screen")
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
    ) {
        item(key = "header") {
            Text(
                text = "Search Blood Donors",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Find active, verified donors by blood group and neighborhood.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Search Criteria Form
        item(key = "form") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Required Blood Group",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    BloodGroupSelectorChips(
                        selectedGroup = selectedBloodGroup,
                        onGroupSelected = { selectedBloodGroup = it },
                        includeAny = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Location Details",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // City
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        placeholder = { Text("e.g. Hyderabad, Bangalore, Mumbai") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = BloodRedPrimary) },
                        trailingIcon = {
                            if (city.isNotEmpty()) {
                                IconButton(onClick = { city = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_input_city"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Area
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        label = { Text("Area / Neighborhood") },
                        placeholder = { Text("e.g. Madhapur, Gachibowli, Film Nagar") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = BloodRedPrimary) },
                        trailingIcon = {
                            if (area.isNotEmpty()) {
                                IconButton(onClick = { area = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_input_area"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloodRedPrimary,
                            focusedLabelColor = BloodRedPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onTriggerSearch(selectedBloodGroup, city, area)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BloodRedPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("search_submit_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Search Donors", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Quick Search Suggestions
        item(key = "suggestions") {
            Text(
                text = "Popular Quick Locations",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(label = "Hyderabad (Madhapur)") {
                    city = "Hyderabad"
                    area = "Madhapur"
                    onTriggerSearch(selectedBloodGroup, "Hyderabad", "Madhapur")
                }
                SuggestionChip(label = "Hyderabad (Gachibowli)") {
                    city = "Hyderabad"
                    area = "Gachibowli"
                    onTriggerSearch(selectedBloodGroup, "Hyderabad", "Gachibowli")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(label = "Hyderabad (Banjara Hills)") {
                    city = "Hyderabad"
                    area = "Banjara Hills"
                    onTriggerSearch(selectedBloodGroup, "Hyderabad", "Banjara Hills")
                }
                SuggestionChip(label = "Bangalore (Central)") {
                    city = "Bangalore"
                    area = "Central"
                    onTriggerSearch(selectedBloodGroup, "Bangalore", "Central")
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
