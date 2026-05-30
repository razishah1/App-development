package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MosqueFinanceAppShell(viewModel: FinanceViewModel) {
    val currentTab = remember { mutableStateOf("DASHBOARD") }
    val activeLanguage by viewModel.language.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    // Message observers
    val errorMsg by viewModel.errorMessage.collectAsState()
    val successMsg by viewModel.successMessage.collectAsState()
    val simulatedSms by viewModel.simulatedNotification.collectAsState()

    // Overlay Drawer state
    var selectedDetailedReceipt by remember { mutableStateOf<ActiveReceiptData?>(null) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideDisplay = maxWidth > 680.dp

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = LanguageManager.getString("app_title", activeLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "ROLE: $currentRole",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 10.sp
                            )
                        }
                    },
                    navigationIcon = {
                        Text("🕌", fontSize = 28.sp, modifier = Modifier.padding(start = 12.dp))
                    },
                    actions = {
                        // Quick switch language button
                        IconButton(
                            onClick = {
                                viewModel.setLanguage(
                                    if (activeLanguage == LanguageManager.Language.BENGALI)
                                        LanguageManager.Language.ENGLISH
                                    else
                                        LanguageManager.Language.BENGALI
                                )
                            }
                        ) {
                            Text(
                                text = if (activeLanguage == LanguageManager.Language.BENGALI) "EN" else "বাং",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                if (!isWideDisplay) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        tabItemsList(activeLanguage).forEach { item ->
                            NavigationBarItem(
                                selected = currentTab.value == item.route,
                                onClick = { currentTab.value = item.route },
                                icon = { Text(item.icon, fontSize = 20.sp) },
                                label = { Text(item.label, fontSize = 9.sp, maxLines = 1, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_btn_${item.route.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Wide Display Responsive Sidebar (Tablet canonical layouts helper)
                if (isWideDisplay) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier.width(135.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        tabItemsList(activeLanguage).forEach { item ->
                            NavigationRailItem(
                                selected = currentTab.value == item.route,
                                onClick = { currentTab.value = item.route },
                                icon = { Text(item.icon, fontSize = 24.sp) },
                                label = { Text(item.label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("rail_btn_${item.route.lowercase()}")
                            )
                        }
                    }
                    VerticalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }

                // Master Content Panels with animated swap-slides
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentTab.value) {
                        "DASHBOARD" -> DashboardTab(
                            viewModel = viewModel,
                            lang = activeLanguage,
                            onNavigateToTab = { currentTab.value = it },
                            onShowReceipt = { selectedDetailedReceipt = it }
                        )
                        "MEMBERS" -> MembersTab(
                            viewModel = viewModel,
                            lang = activeLanguage,
                            currentRole = currentRole
                        )
                        "DONATION" -> DonationsTab(
                            viewModel = viewModel,
                            lang = activeLanguage,
                            currentRole = currentRole,
                            onShowReceipt = { selectedDetailedReceipt = it }
                        )
                        "SUBSCRIPTION" -> SubscriptionsTab(
                            viewModel = viewModel,
                            lang = activeLanguage,
                            currentRole = currentRole,
                            onShowReceipt = { selectedDetailedReceipt = it }
                        )
                        "EXPENSES" -> ExpensesTab(
                            viewModel = viewModel,
                            lang = activeLanguage,
                            currentRole = currentRole
                        )
                        "NOTICES" -> NoticeBoardTab(
                            viewModel = viewModel,
                            lang = activeLanguage,
                            currentRole = currentRole
                        )
                        "AUDIT" -> AuditTab(
                            viewModel = viewModel,
                            lang = activeLanguage
                        )
                        "SETTINGS" -> SettingsTab(
                            viewModel = viewModel,
                            lang = activeLanguage,
                            currentRole = currentRole
                        )
                    }
                }
            }
        }

        // --- Simulated Real-time SMS / Push Alert Notification Drawer ---
        simulatedSms?.let { smsBody ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(0.9f)
                    .testTag("sim_sms_alert"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2D23)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🔔", fontSize = 24.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (activeLanguage == LanguageManager.Language.BENGALI) "অনুকারিত এসএমএস অ্যালার্ট" else "System Simulated SMS Gateway Outflow",
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = smsBody,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = { viewModel.dismissNotification() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }
            }
        }

        // --- Success alerts flow ---
        successMsg?.let { successText ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.dismissSuccess() }) {
                        Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF1B5E20)
            ) {
                Text(successText, color = Color.White)
            }
        }

        // --- Error permission denied feedback alerts ---
        errorMsg?.let { errorText ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.dismissError() }) {
                        Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFFB71C1C)
            ) {
                Text(errorText, color = Color.White)
            }
        }

        // --- Active Receipt Overlay Dialog (Authenticity Scanning display) ---
        selectedDetailedReceipt?.let { receipt ->
            ReceiptDialog(
                title = receipt.title,
                receiptNo = receipt.receiptNo,
                recipientName = receipt.recipient,
                amount = receipt.amount,
                purpose = receipt.purpose,
                paymentMethod = receipt.paymentMethod,
                dateString = receipt.dateStr,
                transactionId = receipt.trnId,
                cashierName = receipt.cashierName,
                languageToUse = activeLanguage,
                onDismiss = { selectedDetailedReceipt = null }
            )
        }
    }
}

// Navigation representation class
data class NavMenuItem(val route: String, val label: String, val icon: String)

private fun tabItemsList(language: LanguageManager.Language) = listOf(
    NavMenuItem("DASHBOARD", LanguageManager.getString("tab_dashboard", language), "📊"),
    NavMenuItem("MEMBERS", LanguageManager.getString("tab_members", language), "👥"),
    NavMenuItem("DONATION", LanguageManager.getString("tab_donations", language), "🕌"),
    NavMenuItem("SUBSCRIPTION", LanguageManager.getString("tab_subscriptions", language), "🌙"),
    NavMenuItem("EXPENSES", LanguageManager.getString("tab_expenses", language), "💸"),
    NavMenuItem("NOTICES", LanguageManager.getString("tab_notices", language), "📢"),
    NavMenuItem("AUDIT", LanguageManager.getString("tab_audit", language), "🛡️"),
    NavMenuItem("SETTINGS", LanguageManager.getString("tab_settings", language), "⚙️")
)
