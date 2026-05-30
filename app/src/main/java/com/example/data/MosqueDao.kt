package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MosqueDao {

    // --- Members ---
    @Query("SELECT * FROM members ORDER BY id DESC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE memberIdCode = :idCode LIMIT 1")
    suspend fun getMemberByIdCode(idCode: String): Member?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    // --- Donations ---
    @Query("SELECT * FROM donations ORDER BY timestamp DESC")
    fun getAllDonations(): Flow<List<Donation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: Donation): Long

    // --- Subscriptions ---
    @Query("SELECT * FROM subscriptions ORDER BY timestamp DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionPayment>>

    @Query("SELECT * FROM subscriptions WHERE memberIdCode = :memberIdCode ORDER BY timestamp DESC")
    fun getSubscriptionsForMember(memberIdCode: String): Flow<List<SubscriptionPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionPayment): Long

    // --- Expenses ---
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long

    // --- Notices ---
    @Query("SELECT * FROM notices ORDER BY timestamp DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice): Long

    @Delete
    suspend fun deleteNotice(notice: Notice)
}
