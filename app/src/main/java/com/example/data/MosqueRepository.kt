package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class MosqueRepository(private val dao: MosqueDao) {

    val allMembers: Flow<List<Member>> = dao.getAllMembers()
    val allDonations: Flow<List<Donation>> = dao.getAllDonations()
    val allSubscriptions: Flow<List<SubscriptionPayment>> = dao.getAllSubscriptions()
    val allExpenses: Flow<List<Expense>> = dao.getAllExpenses()
    val allAuditLogs: Flow<List<AuditLog>> = dao.getAllAuditLogs()
    val allNotices: Flow<List<Notice>> = dao.getAllNotices()

    suspend fun getMemberByIdCode(idCode: String): Member? {
        return dao.getMemberByIdCode(idCode)
    }

    suspend fun insertMember(member: Member): Long {
        return dao.insertMember(member)
    }

    suspend fun updateMember(member: Member) {
        dao.updateMember(member)
    }

    suspend fun deleteMember(member: Member) {
        dao.deleteMember(member)
    }

    suspend fun insertDonation(donation: Donation): Long {
        return dao.insertDonation(donation)
    }

    suspend fun insertSubscription(subscription: SubscriptionPayment): Long {
        return dao.insertSubscription(subscription)
    }

    fun getSubscriptionsForMember(memberIdCode: String): Flow<List<SubscriptionPayment>> {
        return dao.getSubscriptionsForMember(memberIdCode)
    }

    suspend fun insertExpense(expense: Expense): Long {
        return dao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        dao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense)
    }

    suspend fun insertAuditLog(log: AuditLog): Long {
        return dao.insertAuditLog(log)
    }

    suspend fun insertNotice(notice: Notice): Long {
        return dao.insertNotice(notice)
    }

    suspend fun deleteNotice(notice: Notice) {
        dao.deleteNotice(notice)
    }

    // Automatically seed data if the database is empty
    suspend fun seedMockDataIfEmpty() {
        val existingMembers = allMembers.first()
        if (existingMembers.isNotEmpty()) return // Already has data

        // -- Seed Members --
        val membersToSeed = listOf(
            Member(
                memberIdCode = "MBR-1001",
                fullName = "মো: রফিকুল ইসলাম (Md. Rafiqul Islam)",
                fatherName = "মৃত আব্দুল হাকিম",
                mobileNumber = "01712345678",
                nationalId = "5546271928",
                address = "রোড ৪, সেকশন ১২, মিরপুর, ঢাকা",
                membershipType = "MONTHLY",
                joinDate = "10/01/2024",
                isActive = true,
                monthlySubscriptionAmount = 500.0
            ),
            Member(
                memberIdCode = "MBR-1002",
                fullName = "মো: তারিকুল হাসান (Md. Tarikul Hasan)",
                fatherName = "মো: আবুল কাসেম",
                mobileNumber = "01823456789",
                nationalId = "9876543210",
                address = "ধানমন্ডি ৩২, ঢাকা",
                membershipType = "MONTHLY",
                joinDate = "15/02/2024",
                isActive = true,
                monthlySubscriptionAmount = 400.0
            ),
            Member(
                memberIdCode = "MBR-1003",
                fullName = "আলহাজ্ব মোস্তফা কামাল (Haji Mostafa Kamal)",
                fatherName = "মৃত মোকাররম আলী",
                mobileNumber = "01934567890",
                nationalId = "2468135790",
                address = "রোড ৮, উত্তরা তেসরা সেক্টর, ঢাকা",
                membershipType = "LIFETIME",
                joinDate = "01/01/2023",
                isActive = true,
                monthlySubscriptionAmount = 0.0
            ),
            Member(
                memberIdCode = "MBR-1004",
                fullName = "মো: আনিসুর রহমান (Anisur Rahman)",
                fatherName = "মো: লুৎফর রহমান",
                mobileNumber = "01545678901",
                nationalId = "1357924680",
                address = "মোহাম্মদপুর, ঢাকা",
                membershipType = "ANNUAL",
                joinDate = "01/03/2024",
                isActive = true,
                monthlySubscriptionAmount = 5000.0 // Annual
            ),
            Member(
                memberIdCode = "MBR-1005",
                fullName = "মো: জাহিদুল বারী (Zahidul Bari)",
                fatherName = "মো: ফজলে বারী",
                mobileNumber = "01656789012",
                nationalId = "",
                address = "খিলগাঁও, ঢাকা",
                membershipType = "MONTHLY",
                joinDate = "12/04/2024",
                isActive = false, // Inactive member
                monthlySubscriptionAmount = 300.0
            )
        )

        for (member in membersToSeed) {
            dao.insertMember(member)
        }

        // -- Seed Donations --
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L
        val donationsToSeed = listOf(
            Donation(
                receiptNumber = "DON-2026-00001",
                donorName = "বেনামী দানকারী (Anonymous)",
                mobileNumber = "N/A",
                donationType = "FRIDAY",
                amount = 12500.0,
                paymentMethod = "CASH",
                timestamp = now - 1 * oneDayMs,
                receivedBy = "Cashier"
            ),
            Donation(
                receiptNumber = "DON-2026-00002",
                donorName = "মো: আমিনুল ইসলাম (Md. Aminul Islam)",
                mobileNumber = "01755555555",
                donationType = "ZAKAT",
                amount = 50000.0,
                paymentMethod = "BANK",
                transactionId = "TXN9928183",
                timestamp = now - 2 * oneDayMs,
                receivedBy = "Cashier"
            ),
            Donation(
                receiptNumber = "DON-2026-00003",
                donorName = "আব্দুর রহিম (Abdur Rahim)",
                mobileNumber = "01811112222",
                donationType = "MOSQUE_CONSTRUCTION",
                amount = 25000.0,
                paymentMethod = "BKASH",
                transactionId = "BK88274A92",
                timestamp = now - 4 * oneDayMs,
                receivedBy = "Cashier"
            ),
            Donation(
                receiptNumber = "DON-2026-00004",
                donorName = "সাজেদা বেগম (Sajeda Begum)",
                mobileNumber = "01944445555",
                donationType = "SADAQAH",
                amount = 2000.0,
                paymentMethod = "NAGAD",
                transactionId = "NG112233AA",
                timestamp = now - 5 * oneDayMs,
                receivedBy = "Cashier"
            ),
            Donation(
                receiptNumber = "DON-2026-00005",
                donorName = "সাধারণ জুমার দান (Friday Box)",
                mobileNumber = "",
                donationType = "FRIDAY",
                amount = 18600.0,
                paymentMethod = "CASH",
                timestamp = now - 8 * oneDayMs,
                receivedBy = "System Admin"
            )
        )

        for (donation in donationsToSeed) {
            dao.insertDonation(donation)
        }

        // -- Seed Subscription Payments --
        val subscriptionPayments = listOf(
            SubscriptionPayment(
                receiptNumber = "SUB-2026-00001",
                memberIdCode = "MBR-1001",
                monthYear = "04/2026",
                amount = 500.0,
                paymentMethod = "CASH",
                timestamp = now - 3 * oneDayMs,
                receivedBy = "Cashier"
            ),
            SubscriptionPayment(
                receiptNumber = "SUB-2026-00002",
                memberIdCode = "MBR-1001",
                monthYear = "05/2026",
                amount = 500.0,
                paymentMethod = "BKASH",
                transactionId = "BK66442299",
                timestamp = now - 1 * oneDayMs,
                receivedBy = "Cashier"
            ),
            SubscriptionPayment(
                receiptNumber = "SUB-2026-00002",
                memberIdCode = "MBR-1002",
                monthYear = "05/2026",
                amount = 400.0,
                paymentMethod = "NAGAD",
                transactionId = "NG992812",
                timestamp = now - 2 * oneDayMs,
                receivedBy = "Cashier"
            ),
            SubscriptionPayment(
                receiptNumber = "SUB-2026-00004",
                memberIdCode = "MBR-1004",
                monthYear = "2026 (Annual)",
                amount = 5000.0,
                paymentMethod = "BANK",
                transactionId = "BANK882991",
                timestamp = now - 10 * oneDayMs,
                receivedBy = "Cashier"
            )
        )

        for (sub in subscriptionPayments) {
            dao.insertSubscription(sub)
        }

        // -- Seed Expenses --
        val expenses = listOf(
            Expense(
                voucherNumber = "EXP-2026-0001",
                category = "IMAM_SALARY",
                amount = 22000.0,
                description = "মে ২০২৬ ইমাম সাহেবের সম্মানিত ভাতা",
                approvalStatus = "APPROVED",
                approvedBy = "President",
                timestamp = now - 5 * oneDayMs,
                createdBy = "Secretary"
            ),
            Expense(
                voucherNumber = "EXP-2026-0002",
                category = "ELECTRICITY_BILL",
                amount = 9450.0,
                description = "এপ্রিল ২০২৬ ডেসকো বিদ্যুৎ বিল",
                approvalStatus = "APPROVED",
                approvedBy = "President",
                timestamp = now - 6 * oneDayMs,
                createdBy = "Secretary"
            ),
            Expense(
                voucherNumber = "EXP-2026-0003",
                category = "MUAZZIN_SALARY",
                amount = 14000.0,
                description = "মে ২০২৬ মোয়াজ্জিন সাহেবের সম্মানিত ভাতা",
                approvalStatus = "APPROVED",
                approvedBy = "President",
                timestamp = now - 5 * oneDayMs,
                createdBy = "Secretary"
            ),
            Expense(
                voucherNumber = "EXP-2026-0004",
                category = "CLEANING_EXPENSE",
                amount = 3500.0,
                description = "মসজিদের ওজুখান পরিষ্কারের সাবান ও ব্লিচিং পাউডার ক্রয়",
                approvalStatus = "PENDING", // Needs President approval
                timestamp = now - 1 * oneDayMs,
                createdBy = "Secretary"
            )
        )

        for (exp in expenses) {
            dao.insertExpense(exp)
        }

        // -- Seed Notice --
        val notices = listOf(
            Notice(
                title = "পবিত্র ঈদুল আযহা ২০২৬ জামাত ঘোষণা",
                content = "আসন্ন ঈদুল আযহার নামাজের প্রথম জামাত সকাল ৭:০০ টায় এবং দ্বিতীয় জামাত সকাল ৮:০০ টায় অনুষ্ঠিত হবে। নামাজের ইমামতি করবেন সম্মানিত খতিব হাফেজ মাওলানা আব্দুল করিম সাহেব। সকলকে ওজু করে জায়নামাজ সঙ্গে নিয়ে আসার অনুরোধ করা যাচ্ছে।",
                publishedBy = "Secretary"
            ),
            Notice(
                title = "মসজিদ সংস্কার কাজ সম্পর্কিত নোটিশ",
                content = "মসজিদের দ্বিতীয় তলার ছাদ ঢালাইয়ের কাজ খুব শীঘ্রই শুরু হতে যাচ্ছে। তহবিল গঠনে মুক্তহস্তে দান করুন এবং সদাকায়ে জারীয়ার অংশীদার হন। দান মসজিদে সরাসরি রসিদের মাধ্যমে বা ব্যাংক অ্যাকাউন্টে জমা করতে পারেন।",
                publishedBy = "Secretary"
            )
        )

        for (notice in notices) {
            dao.insertNotice(notice)
        }

        // -- Seed Audit Logs --
        val auditLogs = listOf(
            AuditLog(
                user = "Super Admin",
                action = "SYSTEM_INITIALIZATION",
                oldValue = null,
                newValue = "Database instantiated and seed mock statistics imported successfully.",
                timestamp = now - 12 * oneDayMs
            ),
            AuditLog(
                user = "Secretary",
                action = "CREATE_MEMBER",
                oldValue = null,
                newValue = "Registered member MBR-1001: Md. Rafiqul Islam",
                timestamp = now - 10 * oneDayMs
            ),
            AuditLog(
                user = "Cashier",
                action = "COLLECT_DONATION",
                oldValue = null,
                newValue = "Received Donation DON-2026-00002 of 50000 BDT from Md. Aminul Islam",
                timestamp = now - 2 * oneDayMs
            )
        )

        for (log in auditLogs) {
            dao.insertAuditLog(log)
        }
    }
}
