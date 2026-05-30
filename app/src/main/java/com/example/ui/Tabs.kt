package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.viewmodel.*

// --- Global Receipt Helper Holder ---
data class ActiveReceiptData(
    val title: String,
    val receiptNo: String,
    val recipient: String,
    val amount: Double,
    val purpose: String,
    val paymentMethod: String,
    val dateStr: String,
    val trnId: String,
    val cashierName: String
)

@Composable
fun DashboardTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language,
    onNavigateToTab: (String) -> Unit,
    onShowReceipt: (ActiveReceiptData) -> Unit
) {
    val totalMbrs by viewModel.members.collectAsState()
    val activeMbrs = totalMbrs.filter { it.isActive }.size
    
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val currentBalance by viewModel.currentBalance.collectAsState()
    val totalDuesAmount by viewModel.totalDuesAmount.collectAsState()
    val todayColl by viewModel.todayCollection.collectAsState()

    val recentDonations by viewModel.donations.collectAsState()
    val recentSubs by viewModel.subscriptions.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Balanced Financial Metrics Cards Grid ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = LanguageManager.getString("total_income", lang),
                value = "${totalIncome.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                color = Color(0xFF2E7D32),
                icon = "📈",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = LanguageManager.getString("total_expense", lang),
                value = "${totalExpense.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                color = Color(0xFFC62828),
                icon = "📉",
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = LanguageManager.getString("current_balance", lang),
                value = "${currentBalance.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                color = Color(0xFF005C3E), // Highly prominent Theme Emerald Green
                icon = "🕌",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = LanguageManager.getString("due_amount", lang),
                value = "${totalDuesAmount.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                color = Color(0xFFE65100),
                icon = "⚠️",
                modifier = Modifier.weight(1f)
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard(
                title = LanguageManager.getString("today_collection", lang),
                value = "${todayColl.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                color = Color(0xFF1565C0),
                icon = "💰",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = LanguageManager.getString("active_members", lang),
                value = "$activeMbrs / ${totalMbrs.size}",
                color = Color(0xFF455A64),
                icon = "👥",
                modifier = Modifier.weight(1f)
            )
        }

        // --- 2. Embedded Native Canvas-Rendered Charts (Direct visual scans) ---
        // Pre-aggregate categories for donations pie segments
        val donationPieData = remember(recentDonations) {
            recentDonations.groupBy { it.donationType }.map { group ->
                val localizedType = when (group.key) {
                    "GENERAL" -> "General"
                    "FRIDAY" -> "Friday Box"
                    "ZAKAT" -> "Zakat"
                    "FITRAH" -> "Fitrah"
                    "SADAQAH" -> "Sadaqah"
                    "MOSQUE_CONSTRUCTION" -> "Construction"
                    "SPECIAL" -> "Special"
                    else -> "Other"
                }
                localizedType to group.value.sumOf { it.amount }
            }.take(4)
        }

        if (donationPieData.isNotEmpty()) {
            CanvasPieChart(
                title = if (lang == LanguageManager.Language.BENGALI) "দানসমূহের অনুপাত বিশ্লেষণ" else "Donation Segment Analysis",
                data = donationPieData,
                languageToUse = lang
            )
        }

        // Bar chart displaying historic values
        CanvasBarChart(
            title = if (lang == LanguageManager.Language.BENGALI) "আয় বনাম ব্যয় তুলনামূলক চিত্র" else "Financial Ratio (Income/Expense)",
            categories = listOf("Inflow", "Outflow", "Balance", "Oust. Dues"),
            values = listOf(totalIncome.toFloat(), totalExpense.toFloat(), currentBalance.toFloat(), totalDuesAmount.toFloat()),
            languageToUse = lang
        )

        // --- 3. Quick Action Shortcuts ---
        Text(
            text = if (lang == LanguageManager.Language.BENGALI) "সহজ নেভিগেশন লিংকসমূহ" else "Administrative Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onNavigateToTab("DONATION") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(LanguageManager.getString("btn_add_donation", lang), fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onNavigateToTab("SUBSCRIPTION") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(LanguageManager.getString("btn_collect_sub", lang), fontSize = 11.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
        }

        // --- 4. Streamed Recent Payments Register ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // Tailwind's border-slate-200
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (lang == LanguageManager.Language.BENGALI) "সাম্প্রতিক জমাকৃত রসিদ সমূহ" else "Latest Real-time Receipts Log",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Interleaved combination of latest 3 donations & 3 subscriptions
                val mergedList = remember(recentDonations, recentSubs) {
                    val formattedDons = recentDonations.map {
                        ActiveReceiptData("Donation Receipt", it.receiptNumber, it.donorName, it.amount, it.donationType, it.paymentMethod, "Recently", it.transactionId, it.receivedBy)
                    }
                    val formattedSubs = recentSubs.map {
                        ActiveReceiptData("Subscription Receipt", it.receiptNumber, "Member: ${it.memberIdCode}", it.amount, "Sub for ${it.monthYear}", it.paymentMethod, "Recently", it.transactionId, it.receivedBy)
                    }
                    (formattedDons + formattedSubs).sortedBy { it.receiptNo }.take(4)
                }

                if (mergedList.isEmpty()) {
                    Text(
                        text = if (lang == LanguageManager.Language.BENGALI) "এখনো কোন পেমেন্ট রেকর্ড করা হয়নি।" else "No financial payments stored yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    mergedList.forEach { receipt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onShowReceipt(receipt) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🧾", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(receipt.recipient, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(receipt.receiptNo, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("+${receipt.amount.toInt()} ${LanguageManager.getString("currency_symbol", lang)}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text(receipt.purpose, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

// Beautifully Polished Metrics Card template
@Composable
fun MetricCard(title: String, value: String, color: Color, icon: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // Tailwind's border-slate-200
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B), // Custom professional Slate-color text
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Colored icon box background
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 15.sp)
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

// --- MAIN MEMBERS REGISTRY INTERFACE ---
@Composable
fun MembersTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language,
    currentRole: String
) {
    val rawMembers by viewModel.members.collectAsState()
    var searchBy by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") }

    var showForm by remember { mutableStateOf(false) }

    // Form states
    var fullName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var nid by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var membershipType by remember { mutableStateOf("MONTHLY") }
    var monthlyRate by remember { mutableStateOf("500") }

    val filteredMembers = remember(rawMembers, searchBy, filterType) {
        rawMembers.filter { member ->
            val matchesQuery = member.fullName.contains(searchBy, ignoreCase = true) ||
                    member.memberIdCode.contains(searchBy, ignoreCase = true) ||
                    member.mobileNumber.contains(searchBy)
            val matchesFilter = filterType == "ALL" || member.membershipType == filterType
            matchesQuery && matchesFilter
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Search & Add Box Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchBy,
                onValueChange = { searchBy = it },
                label = { Text(if (lang == LanguageManager.Language.BENGALI) "নাম অথবা আইডি দিয়ে খুঁজুন" else "Search name or ID") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            if (currentRole == "Super Admin" || currentRole == "Secretary") {
                Button(
                    onClick = { showForm = !showForm },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.getString("btn_add_member", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Insert member form collapsible block
        if (showForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (lang == LanguageManager.Language.BENGALI) "নতুন সদস্য নিবন্ধন ফর্ম" else "Member Enrollment Form",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(LanguageManager.getString("lbl_full_name", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fatherName,
                            onValueChange = { fatherName = it },
                            label = { Text(LanguageManager.getString("lbl_father_name", lang)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            label = { Text(LanguageManager.getString("lbl_mobile_number", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nid,
                            onValueChange = { nid = it },
                            label = { Text(LanguageManager.getString("lbl_national_id", lang)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text(LanguageManager.getString("lbl_address", lang)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Membership Type Drop-down simulation rows
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(LanguageManager.getString("lbl_membership_type", lang) + ":", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        listOf("MONTHLY", "ANNUAL", "LIFETIME").forEach { type ->
                            ElevatedFilterChip(
                                selected = membershipType == type,
                                onClick = { membershipType = type },
                                label = { Text(type, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    if (membershipType == "MONTHLY") {
                        OutlinedTextField(
                            value = monthlyRate,
                            onValueChange = { monthlyRate = it },
                            label = { Text(LanguageManager.getString("lbl_monthly_amount", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && mobileNumber.isNotBlank()) {
                                viewModel.addMember(
                                    fullName = fullName,
                                    fatherName = fatherName,
                                    mobileNumber = mobileNumber,
                                    nid = nid,
                                    address = address,
                                    membershipType = membershipType,
                                    monthlyRate = monthlyRate.toDoubleOrNull() ?: 200.0
                                )
                                // Clear inputs
                                fullName = ""
                                fatherName = ""
                                mobileNumber = ""
                                nid = ""
                                address = ""
                                showForm = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(LanguageManager.getString("btn_save", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Member list directory
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredMembers) { member ->
                val duesData = viewModel.getMemberDueDetails(member)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(member.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "${LanguageManager.getString("lbl_member_id", lang)}: ${member.memberIdCode} • ${member.mobileNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            // Active / Inactive switch clickable for Secretary/Admin roles
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = member.isActive,
                                    onCheckedChange = {
                                        viewModel.updateMember(member.copy(isActive = it))
                                    },
                                    enabled = currentRole == "Super Admin" || currentRole == "Secretary",
                                    thumbContent = {
                                        Icon(
                                            imageVector = if (member.isActive) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize)
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Type, FatherName and Dues Overview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                ValueBadge(
                                    labelText = if (member.membershipType == "MONTHLY")
                                        LanguageManager.getString("type_monthly", lang)
                                    else if (member.membershipType == "ANNUAL")
                                        LanguageManager.getString("type_annual", lang)
                                    else
                                        LanguageManager.getString("type_lifetime", lang),
                                    bgColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                                if (member.fatherName.isNotBlank()) {
                                    Text(
                                        "${LanguageManager.getString("lbl_father_name", lang)}: ${member.fatherName}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            if (member.membershipType == "MONTHLY") {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (lang == LanguageManager.Language.BENGALI) "বকেয়া চাঁদা" else "Outstanding Arrears",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "${duesData.dueAmount.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (duesData.dueAmount > 0) Color(0xFFC62828) else Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Quick Badge UI block
@Composable
fun ValueBadge(labelText: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(labelText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

// --- DONATIONS HUB INTERFACE ---
@Composable
fun DonationsTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language,
    currentRole: String,
    onShowReceipt: (ActiveReceiptData) -> Unit
) {
    val items by viewModel.donations.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    // Forms
    var donorName by remember { mutableStateOf("") }
    var mobileNo by remember { mutableStateOf("") }
    var donationType by remember { mutableStateOf("GENERAL") }
    var amount by remember { mutableStateOf("") }
    var trnId by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("CASH") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (lang == LanguageManager.Language.BENGALI) "ঐচ্ছিক দান ও সদাকা রসিদ সমূহ" else "Voluntary Donations Directory",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (currentRole == "Super Admin" || currentRole == "Cashier") {
                Button(
                    onClick = { showForm = !showForm },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.getString("btn_add_donation", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (lang == LanguageManager.Language.BENGALI) "দান বা সদাকা রসিদ দাখিল ফর্ম" else "Record Charity Receipt Form",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = donorName,
                        onValueChange = { donorName = it },
                        label = { Text(LanguageManager.getString("lbl_donor_name", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mobileNo,
                            onValueChange = { mobileNo = it },
                            label = { Text(LanguageManager.getString("lbl_mobile_number", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text(LanguageManager.getString("lbl_amount", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Simulated selection for payment types
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(LanguageManager.getString("lbl_payment_method", lang) + ":", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        listOf("CASH", "BKASH", "NAGAD", "BANK").forEach { method ->
                            ElevatedFilterChip(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method },
                                label = { Text(method, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    if (paymentMethod != "CASH") {
                        OutlinedTextField(
                            value = trnId,
                            onValueChange = { trnId = it },
                            label = { Text(LanguageManager.getString("lbl_transaction_id", lang)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Category
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(LanguageManager.getString("lbl_donation_type", lang) + ":", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            var expandedCat by remember { mutableStateOf(false) }
                            Button(onClick = { expandedCat = true }) {
                                Text(donationType, fontSize = 11.sp)
                            }
                            DropdownMenu(expanded = expandedCat, onDismissRequest = { expandedCat = false }) {
                                listOf("GENERAL", "FRIDAY", "ZAKAT", "FITRAH", "SADAQAH", "MOSQUE_CONSTRUCTION", "SPECIAL", "OTHER").forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type, style = MaterialTheme.typography.bodySmall) },
                                        onClick = {
                                            donationType = type
                                            expandedCat = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val amtDouble = amount.toDoubleOrNull() ?: 0.0
                            if (amtDouble > 0) {
                                viewModel.recordDonation(
                                    donorName = donorName,
                                    mobileNumber = mobileNo,
                                    donationType = donationType,
                                    amount = amtDouble,
                                    paymentMethod = paymentMethod,
                                    trnId = trnId
                                )
                                donorName = ""
                                mobileNo = ""
                                amount = ""
                                trnId = ""
                                showForm = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(LanguageManager.getString("btn_save", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onShowReceipt(
                                ActiveReceiptData(
                                    title = "Donation Receipt",
                                    receiptNo = item.receiptNumber,
                                    recipient = item.donorName,
                                    amount = item.amount,
                                    purpose = item.donationType,
                                    paymentMethod = item.paymentMethod,
                                    dateStr = "May 30, 2026",
                                    trnId = item.transactionId,
                                    cashierName = item.receivedBy
                                )
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.donorName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "No: ${item.receiptNumber} • ${item.paymentMethod}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "+${item.amount.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32)
                            )
                            ValueBadge(item.donationType, MaterialTheme.colorScheme.primaryContainer)
                        }
                    }
                }
            }
        }
    }
}

// --- SUBSCRIPTIONS CALCULATION & MONTHLY LEDGERS ---
@Composable
fun SubscriptionsTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language,
    currentRole: String,
    onShowReceipt: (ActiveReceiptData) -> Unit
) {
    val activeMembersList by viewModel.members.collectAsState()
    val collectionsList by viewModel.subscriptions.collectAsState()

    var showPaymentForm by remember { mutableStateOf(false) }

    // Forms inputs
    var selectedMemberIdCode by remember { mutableStateOf("") }
    var amountValue by remember { mutableStateOf("") }
    var trnIdValue by remember { mutableStateOf("") }
    var paymentChannel by remember { mutableStateOf("CASH") }
    var selectedMonthYear by remember { mutableStateOf("05/2026") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (lang == LanguageManager.Language.BENGALI) "চাঁদা সংগ্রহ ও বকেয়া নিরীক্ষা" else "Subscription Ledger Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (currentRole == "Super Admin" || currentRole == "Cashier") {
                Button(
                    onClick = { showPaymentForm = !showPaymentForm },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = if (showPaymentForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.getString("btn_collect_sub", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showPaymentForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (lang == LanguageManager.Language.BENGALI) "মাসিক/বার্ষিক চাঁদা রসিদ এন্ট্রি" else "New Member Subscription Receipt",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Autocomplete selector or simple text code input
                    OutlinedTextField(
                        value = selectedMemberIdCode,
                        onValueChange = { selectedMemberIdCode = it },
                        label = { Text(if (lang == LanguageManager.Language.BENGALI) "সদস্য আইডি কোড (উদা: MBR-1001)" else "Member ID Code (e.g. MBR-1001)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = selectedMonthYear,
                            onValueChange = { selectedMonthYear = it },
                            label = { Text(LanguageManager.getString("lbl_month_year", lang)) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = amountValue,
                            onValueChange = { amountValue = it },
                            label = { Text(LanguageManager.getString("lbl_amount", lang)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(LanguageManager.getString("lbl_payment_method", lang) + ":", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        listOf("CASH", "BKASH", "NAGAD", "BANK").forEach { channel ->
                            ElevatedFilterChip(
                                selected = paymentChannel == channel,
                                onClick = { paymentChannel = channel },
                                label = { Text(channel, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    if (paymentChannel != "CASH") {
                        OutlinedTextField(
                            value = trnIdValue,
                            onValueChange = { trnIdValue = it },
                            label = { Text(LanguageManager.getString("lbl_transaction_id", lang)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = {
                            val amt = amountValue.toDoubleOrNull() ?: 0.0
                            if (selectedMemberIdCode.isNotBlank() && amt > 0) {
                                viewModel.recordSubscription(
                                    memberIdCode = selectedMemberIdCode,
                                    monthYear = selectedMonthYear,
                                    amount = amt,
                                    paymentMethod = paymentChannel,
                                    trnId = trnIdValue
                                )
                                selectedMemberIdCode = ""
                                amountValue = ""
                                trnIdValue = ""
                                showPaymentForm = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(LanguageManager.getString("btn_save", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // List showing dynamic outstanding member dues with Quick Alert SMS actions!
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (lang == LanguageManager.Language.BENGALI) "চাঁদা আদায়ের বিবরণী ও সাম্প্রতিক রসিদ" else "Historical Collection Ledger Indexes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(260.dp)) {
                    items(collectionsList) { col ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onShowReceipt(
                                        ActiveReceiptData(
                                            title = "Subscription Receipt",
                                            receiptNo = col.receiptNumber,
                                            recipient = col.memberIdCode,
                                            amount = col.amount,
                                            purpose = "Month: ${col.monthYear}",
                                            paymentMethod = col.paymentMethod,
                                            dateStr = "May 30, 2026",
                                            trnId = col.transactionId,
                                            cashierName = col.receivedBy
                                        )
                                    )
                                }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("No: ${col.receiptNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Member: ${col.memberIdCode} • Month: ${col.monthYear}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text(
                                "+${col.amount.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 13.sp
                            )
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

// --- EXPENSE AUDIT TRACKER WITH ROLE-BASED APPROVALS ---
@Composable
fun ExpensesTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language,
    currentRole: String
) {
    val items by viewModel.expenses.collectAsState()
    var showForm by remember { mutableStateOf(false) }

    // Forms
    var category by remember { mutableStateOf("IMAM_SALARY") }
    var amountText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (lang == LanguageManager.Language.BENGALI) "খরচ এবং অনুমোদনযোগ্য ভাউচার সমূহ" else "Expenses & Vouchers Ledger",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (currentRole == "Super Admin" || currentRole == "Secretary" || currentRole == "Cashier") {
                Button(
                    onClick = { showForm = !showForm },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = if (showForm) Icons.Default.Close else Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.getString("btn_add_expense", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showForm) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        if (lang == LanguageManager.Language.BENGALI) "নতুন ভাউচার পেশকরণ ফর্ম" else "Voucher Request Form",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text(LanguageManager.getString("lbl_amount", lang)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descText,
                        onValueChange = { descText = it },
                        label = { Text(LanguageManager.getString("lbl_description", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(LanguageManager.getString("lbl_expense_category", lang) + ":", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            var expandedEx by remember { mutableStateOf(false) }
                            Button(onClick = { expandedEx = true }) {
                                Text(category, fontSize = 11.sp)
                            }
                            DropdownMenu(expanded = expandedEx, onDismissRequest = { expandedEx = false }) {
                                listOf("IMAM_SALARY", "MUAZZIN_SALARY", "ELECTRICITY_BILL", "WATER_BILL", "CLEANING_EXPENSE", "CONSTRUCTION_EXPENSE", "MAINTENANCE_EXPENSE", "OTHER_EXPENSE").forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item, style = MaterialTheme.typography.bodySmall) },
                                        onClick = {
                                            category = item
                                            expandedEx = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                viewModel.recordExpense(
                                    category = category,
                                    amount = amt,
                                    description = descText,
                                    attachmentPath = null
                                )
                                amountText = ""
                                descText = ""
                                showForm = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(LanguageManager.getString("btn_save", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "Voucher: ${item.voucherNumber} • By: ${item.createdBy}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                "-${item.amount.toInt()} ${LanguageManager.getString("currency_symbol", lang)}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC62828)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ValueBadge(
                                item.category,
                                MaterialTheme.colorScheme.secondaryContainer
                            )

                            // Status Pill
                            val (badgeBg, badgeText) = when (item.approvalStatus) {
                                "APPROVED" -> Color(0xFFE8F5E9) to (if (lang == LanguageManager.Language.BENGALI) "অনুমোদিত" else "APPROVED")
                                "REJECTED" -> Color(0xFFFFEBEE) to (if (lang == LanguageManager.Language.BENGALI) "প্রত্যাখ্যাত" else "REJECTED")
                                else -> Color(0xFFFFF3E0) to (if (lang == LanguageManager.Language.BENGALI) "অনুমোদন পেন্ডিং" else "PENDING")
                            }

                            Box(
                                modifier = Modifier
                                    .background(badgeBg, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(badgeText, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
                            }
                        }

                        // Role Based Authorization Button Console
                        if (item.approvalStatus == "PENDING" && (currentRole == "Super Admin" || currentRole == "President")) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { viewModel.rejectExpense(item) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFC62828))
                                ) {
                                    Text(LanguageManager.getString("btn_reject", lang), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.approveExpense(item) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(LanguageManager.getString("btn_approve", lang), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SECURE SYSTEM AUDIT LOGGER LOGS VIEW ---
@Composable
fun AuditTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language
) {
    val items by viewModel.auditLogs.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = if (lang == LanguageManager.Language.BENGALI) "সিস্টেম কার্যক্রম ও অডিট ট্র্যাকিং" else "Unified Security Audit Traces",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(items) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(log.action, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(
                                text = "IP: ${log.ipAddress}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(log.newValue ?: "", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "User: ${log.user}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Time: recently",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- WHITEBOARD GENERAL NOTICES BOARD ---
@Composable
fun NoticeBoardTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language,
    currentRole: String
) {
    val noticesList by viewModel.notices.collectAsState()
    var showNoticeCreator by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = LanguageManager.getString("tab_notices", lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (currentRole == "Super Admin" || currentRole == "Secretary") {
                Button(
                    onClick = { showNoticeCreator = !showNoticeCreator },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = if (showNoticeCreator) Icons.Default.Close else Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.getString("btn_publish_notice", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showNoticeCreator) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text(if (lang == LanguageManager.Language.BENGALI) "নোটিশের শিরোনাম" else "Announcement Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contentInput,
                        onValueChange = { contentInput = it },
                        label = { Text(if (lang == LanguageManager.Language.BENGALI) "বিস্তারিত নোটিশ" else "Notice Content") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (titleInput.isNotBlank() && contentInput.isNotBlank()) {
                                viewModel.publishNotice(titleInput, contentInput)
                                titleInput = ""
                                contentInput = ""
                                showNoticeCreator = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(LanguageManager.getString("btn_save", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(noticesList) { notice ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(notice.title, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = Color(0xFF005C3E))
                            if (currentRole == "Super Admin" || currentRole == "Secretary") {
                                IconButton(onClick = { viewModel.deleteNotice(notice) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Published by: ${notice.publishedBy} • May 30, 2026",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(notice.content, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

// --- GENERAL APP SETTINGS & SIMULATION SWITCHER ---
@Composable
fun SettingsTab(
    viewModel: FinanceViewModel,
    lang: LanguageManager.Language,
    currentRole: String
) {
    var expandedRoles by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = LanguageManager.getString("switch_role", lang),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (lang == LanguageManager.Language.BENGALI)
                        "অ্যাসার্ট লেজার সিস্টেমটি রোল এবং প্রমিশন দিয়ে সুরক্ষিত। এখানে বিভিন্ন ভূমিকা পরিবর্তন করে প্রতিটি পেইজের অনুমোদন অ্যাক্টিভিটি ম্যানিপুলেট করে দেখুন।"
                    else
                        "Test role behaviors in real-time. Switching the simulated user profile unlocks respective forms and blocks actions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedRoles = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${LanguageManager.getString("current_role", lang)}: [ $currentRole ]",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = expandedRoles,
                        onDismissRequest = { expandedRoles = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        listOf("Super Admin", "President", "Secretary", "Cashier", "Auditor", "Member").forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.setCurrentRole(role)
                                    expandedRoles = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Language toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (lang == LanguageManager.Language.BENGALI) "ভাষা পরিবর্তন করুন" else "Change Language Support",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.setLanguage(LanguageManager.Language.BENGALI) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == LanguageManager.Language.BENGALI) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f)
                        )
                    ) {
                        Text("বাংলা (Default)", fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = { viewModel.setLanguage(LanguageManager.Language.ENGLISH) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == LanguageManager.Language.ENGLISH) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f)
                        )
                    ) {
                        Text("English", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // System Recovery action Simulation for Admin
        if (currentRole == "Super Admin") {
            Button(
                onClick = { viewModel.restoreMockBackup() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (lang == LanguageManager.Language.BENGALI) "সিস্টেম ডাটাবেস রিস্টোর করুন" else "Restore Database Backup", fontWeight = FontWeight.Black)
            }
        }
    }
}
