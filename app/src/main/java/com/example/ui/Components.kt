package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*

// --- Custom Finance UI Components ---

@Composable
fun CanvasPieChart(
    title: String,
    data: List<Pair<String, Double>>,
    languageToUse: LanguageManager.Language,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.second }.toFloat()
    val colors = listOf(
        Color(0xFF1E5631), // Primary Teal - Polish theme
        Color(0xFF2D7D46), // Soft Emerald - Polish theme
        Color(0xFFD4AF37), // Golden Bronze
        Color(0xFF9E7E1D), // Deep Gold
        Color(0xFF2D3436), // Slate Gray
        Color(0xFFE5C158), // Pale Gold
        Color(0xFF43A047), // Green
        Color(0xFF81C784)  // Minty Light
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // Tailwind's border-slate-200
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (total == 0f) {
                Box(
                    modifier = Modifier.height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (languageToUse == LanguageManager.Language.BENGALI) "কোন তথ্য নেই" else "No Data Available",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Pie Ring Drawing block
                    Canvas(modifier = Modifier.size(120.dp)) {
                        var startAngle = 0f
                        data.forEachIndexed { index, pair ->
                            val sweepAngle = (pair.second.toFloat() / total) * 360f
                            val color = colors[index % colors.size]
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 24f, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Legend Block
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        data.forEachIndexed { index, pair ->
                            val color = colors[index % colors.size]
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${pair.first}: ${pair.second.toInt()} ${LanguageManager.getString("currency_symbol", languageToUse)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CanvasBarChart(
    title: String,
    categories: List<String>,
    values: List<Float>,
    languageToUse: LanguageManager.Language,
    modifier: Modifier = Modifier
) {
    val maxValue = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // Tailwind's border-slate-200
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { idx, value ->
                    val normalizedHeightFraction = value / maxValue
                    val label = categories.getOrNull(idx) ?: ""

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = value.toInt().toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxHeight((normalizedHeightFraction * 0.8f).coerceIn(0f, 1f))
                                .width(18.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                        )
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// Beautiful simulated PDF Invoice / Digital Receipt with a live QR authenticity lookups
@Composable
fun ReceiptDialog(
    title: String,
    receiptNo: String,
    recipientName: String,
    amount: Double,
    purpose: String,
    paymentMethod: String,
    dateString: String,
    transactionId: String = "",
    cashierName: String,
    languageToUse: LanguageManager.Language,
    onDismiss: () -> Unit
) {
    var isQRScanned by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("receipt_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.dp, Color(0xFFD4AF37)) // Golden Border for authentic visual weighting
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (Mosque Watermark Emblem Top-Bar)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFF1F9F6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🕌", fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = if (languageToUse == LanguageManager.Language.BENGALI) "ঐতিহাসিক বায়তুল মোকাররম মসজিদ" else "Baitul Mukarram Historic Mosque",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF005C3E),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (languageToUse == LanguageManager.Language.BENGALI) "মিরপুর রোড, ঢাকা, বাংলাদেশ" else "Mirpur Road, Dhaka, Bangladesh",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Receipt Title Tag
                Box(
                    modifier = Modifier
                        .background(Color(0xFF005C3E), RoundedCornerShape(4.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Details Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAF9F6), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReceiptRow(
                        if (languageToUse == LanguageManager.Language.BENGALI) "রসিদ নম্বর" else "Receipt No:",
                        receiptNo
                    )
                    ReceiptRow(
                        if (languageToUse == LanguageManager.Language.BENGALI) "নাম/দাতা" else "Received From:",
                        recipientName
                    )
                    ReceiptRow(
                        if (languageToUse == LanguageManager.Language.BENGALI) "টাকার পরিমাণ" else "Amount Paid:",
                        "$amount ${LanguageManager.getString("currency_symbol", languageToUse)}",
                        isHighlighted = true
                    )
                    ReceiptRow(
                        if (languageToUse == LanguageManager.Language.BENGALI) "উদ্দেশ্য/খাত" else "Purpose:",
                        purpose
                    )
                    ReceiptRow(
                        if (languageToUse == LanguageManager.Language.BENGALI) "পেমেন্ট মাধ্যম" else "Payment Method:",
                        paymentMethod
                    )
                    if (transactionId.isNotBlank()) {
                        ReceiptRow(
                            if (languageToUse == LanguageManager.Language.BENGALI) "ট্রানজেকশন আইডি" else "Txn ID:",
                            transactionId
                        )
                    }
                    ReceiptRow(
                        if (languageToUse == LanguageManager.Language.BENGALI) "তারিখ ও সময়" else "Payment Date:",
                        dateString
                    )
                    ReceiptRow(
                        if (languageToUse == LanguageManager.Language.BENGALI) "আদায়কারী ক্যাশিয়ার" else "Collected By:",
                        cashierName
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Authentic QR-Code Simulation
                Text(
                    text = if (languageToUse == LanguageManager.Language.BENGALI) "ডিজিটাল সরকারি যাচাইকরণ কিউআর কোড" else "Verifiable Audit QR Code",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { isQRScanned = !isQRScanned }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dynamic QR Grid pattern using tiny canvases
                    Canvas(modifier = Modifier.size(90.dp)) {
                        val squareSize = size.width / 5
                        // Draw simulated QR anchor cubes in corners
                        drawRect(Color.Black, Offset(0f, 0f), Size(squareSize * 2, squareSize * 2), style = Stroke(width = 6f))
                        drawRect(Color.Black, Offset(squareSize * 0.5f, squareSize * 0.5f), Size(squareSize, squareSize))

                        drawRect(Color.Black, Offset(size.width - squareSize * 2, 0f), Size(squareSize * 2, squareSize * 2), style = Stroke(width = 6f))
                        drawRect(Color.Black, Offset(size.width - squareSize * 1.5f, squareSize * 0.5f), Size(squareSize, squareSize))

                        drawRect(Color.Black, Offset(0f, size.height - squareSize * 2), Size(squareSize * 2, squareSize * 2), style = Stroke(width = 6f))
                        drawRect(Color.Black, Offset(squareSize * 0.5f, size.height - squareSize * 1.5f), Size(squareSize, squareSize))

                        // Random bits
                        drawRect(Color.DarkGray, Offset(squareSize * 2.5f, squareSize * 0.5f), Size(squareSize * 0.8f, squareSize * 0.8f))
                        drawRect(Color.Black, Offset(squareSize * 3f, squareSize * 2.5f), Size(squareSize * 0.6f, squareSize * 1.2f))
                        drawRect(Color.DarkGray, Offset(squareSize * 1.2f, squareSize * 2.8f), Size(squareSize * 1.5f, squareSize * 0.4f))
                        drawRect(Color.Black, Offset(squareSize * 2.5f, squareSize * 4f), Size(squareSize * 1.5f, squareSize * 0.5f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (languageToUse == LanguageManager.Language.BENGALI) "ক্লিক করে আসল যাচাই করুন" else "Tap to check authentic status",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Interactive verify modal expansion
                if (isQRScanned) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (languageToUse == LanguageManager.Language.BENGALI) "রসিদটি শতভাগ সত্য ও অনুমোদিত।" else "Receipt Authenticity verified by Mosque Council Ledger.",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Digital Signature line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "B26_SGN_SEC",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color.LightGray
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(1.dp)
                                .background(Color.LightGray)
                        )
                        Text(
                            text = if (languageToUse == LanguageManager.Language.BENGALI) "অনুমোদিত খতিব/সেক্রেটারি" else "Executive Council",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = cashierName,
                            fontFamily = FontFamily.Cursive,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF005C3E)
                        )
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(1.dp)
                                .background(Color.LightGray)
                        )
                        Text(
                            text = if (languageToUse == LanguageManager.Language.BENGALI) "আদায়কারী স্বাক্ষর" else "Authorized Cashier",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share Buttons list
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { /* simulated print dialog trigger */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005C3E))
                    ) {
                        Text(
                            text = if (languageToUse == LanguageManager.Language.BENGALI) "প্রিন্ট রসিদ" else "Print Receipt",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = { /* Simulated Share drawer */ },
                        border = BorderStroke(1.dp, Color(0xFF005C3E))
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp), tint = Color(0xFF005C3E))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (languageToUse == LanguageManager.Language.BENGALI) "শেয়ার" else "Share PDF",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF005C3E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String, isHighlighted: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = if (isHighlighted) 14.sp else 12.sp,
            color = if (isHighlighted) Color(0xFF005C3E) else Color.Black,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
