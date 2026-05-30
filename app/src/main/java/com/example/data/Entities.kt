package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val memberIdCode: String, // e.g. "MBR-1001"
    val fullName: String,
    val fatherName: String,
    val mobileNumber: String,
    val nationalId: String = "",
    val address: String = "",
    val photoUri: String? = null,
    val membershipType: String, // "MONTHLY", "ANNUAL", "LIFETIME"
    val joinDate: String,
    val isActive: Boolean = true,
    val monthlySubscriptionAmount: Double = 200.0
)

@Entity(tableName = "donations")
data class Donation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiptNumber: String, // e.g. "DON-10001"
    val donorName: String,
    val mobileNumber: String,
    val donationType: String, // "GENERAL", "FRIDAY", "ZAKAT", "FITRAH", "SADAQAH", "CONSTRUCTION", "SPECIAL", "OTHER"
    val amount: Double,
    val paymentMethod: String, // "CASH", "BKASH", "NAGAD", "BANK"
    val transactionId: String = "",
    val timestamp: Long,
    val receivedBy: String
)

@Entity(tableName = "subscriptions")
data class SubscriptionPayment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiptNumber: String, // e.g. "SUB-10001"
    val memberIdCode: String, // references Member.memberIdCode
    val monthYear: String, // "MM/YYYY"
    val amount: Double,
    val paymentMethod: String, // "CASH", "BKASH", "NAGAD", "BANK"
    val transactionId: String = "",
    val timestamp: Long,
    val receivedBy: String
)

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val voucherNumber: String, // e.g. "EXP-1001"
    val category: String, // "IMAM_SALARY", "MUAZZIN_SALARY", "ELECTRICITY", "WATER", "CLEANING", "CONSTRUCTION", "MAINTENANCE", "OTHER"
    val amount: Double,
    val description: String,
    val attachmentPath: String? = null,
    val approvalStatus: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val approvedBy: String? = null,
    val timestamp: Long,
    val createdBy: String
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val user: String,
    val action: String,
    val oldValue: String? = null,
    val newValue: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val ipAddress: String = "192.168.1.1"
)

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val publishedBy: String
)
