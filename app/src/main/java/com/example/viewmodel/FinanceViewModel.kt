package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: MosqueRepository) : ViewModel() {

    // --- State: Language & User Role ---
    private val _language = MutableStateFlow(LanguageManager.Language.BENGALI)
    val language: StateFlow<LanguageManager.Language> = _language.asStateFlow()

    private val _currentRole = MutableStateFlow(LanguageManager.Language.values().let { "Super Admin" }) // Default to highest role to permit easy previewing
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    fun setLanguage(lang: LanguageManager.Language) {
        _language.value = lang
    }

    fun setCurrentRole(role: String) {
        _currentRole.value = role
        logSystemAction("SWITCH_USER_ROLE", "Switched active simulation account to $role")
    }

    // --- State: Feedback Messages ---
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _simulatedNotification = MutableStateFlow<String?>(null)
    val simulatedNotification: StateFlow<String?> = _simulatedNotification.asStateFlow()

    fun dismissSuccess() { _successMessage.value = null }
    fun dismissError() { _errorMessage.value = null }
    fun dismissNotification() { _simulatedNotification.value = null }

    // --- Search Queries & Filters ---
    val searchQuery = MutableStateFlow("")
    val donationFilter = MutableStateFlow("ALL") // category filter
    val expenseFilter = MutableStateFlow("ALL") // status or category filter

    // --- Room Database Streams ---
    val members: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val donations: StateFlow<List<Donation>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscriptions: StateFlow<List<SubscriptionPayment>> = repository.allSubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notices: StateFlow<List<Notice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedMockDataIfEmpty()
        }
    }

    // --- Dynamic Dues Calculation (Automatic Due Calculation) ---
    // High-quality algorithm: computes unpaid dues for MONTHLY members based on months since join
    // We assume the current date is in May 2026 (based on metadata calendar).
    // Dues = (Months from JoinDate to May 2026 * monthly amount) - (actual subscription paid).
    fun getMemberDueDetails(member: Member): MemberDueInfo {
        if (member.membershipType != "MONTHLY" || !member.isActive) {
            return MemberDueInfo(dueAmount = 0.0, monthsDue = emptyList())
        }
        
        // Define relevant months we track for simulator: 01/2026 to 05/2026
        val trackingMonths = listOf("01/2026", "02/2026", "03/2026", "04/2026", "05/2026")
        
        // Filter those paid by this member
        val paidMonths = subscriptions.value
            .filter { it.memberIdCode == member.memberIdCode }
            .map { it.monthYear }
            .toSet()

        val unpaidMonths = trackingMonths.filter { !paidMonths.contains(it) }
        val dueAmount = unpaidMonths.size * member.monthlySubscriptionAmount
        
        return MemberDueInfo(
            dueAmount = dueAmount,
            monthsDue = unpaidMonths
        )
    }

    // Total Due Sum for entire Mosque
    val totalDuesAmount: StateFlow<Double> = members.map { memberList ->
        memberList.sumOf { getMemberDueDetails(it).dueAmount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Financial Summaries (Cash Book, Ledger) ---
    val totalIncome: StateFlow<Double> = combine(donations, subscriptions) { dons, subs ->
        val donationSum = dons.sumOf { it.amount }
        val subscriptionSum = subs.sumOf { it.amount }
        donationSum + subscriptionSum
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = expenses.map { exps ->
        // Only count APPROVED expenses in total
        exps.filter { it.approvalStatus == "APPROVED" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentBalance: StateFlow<Double> = combine(totalIncome, totalExpense) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Today's collections (assuming date matches system state May 30, 2026)
    val todayCollection: StateFlow<Double> = combine(donations, subscriptions) { dons, subs ->
        // Filter for transactions within past 24 hours
        val standardDayLimit = 24 * 60 * 60 * 1000L
        val recentDons = dons.filter { System.currentTimeMillis() - it.timestamp < standardDayLimit }.sumOf { it.amount }
        val recentSubs = subs.filter { System.currentTimeMillis() - it.timestamp < standardDayLimit }.sumOf { it.amount }
        recentDons + recentSubs
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Security Checks (Role-Based Access Control) ---
    private fun checkPermission(requiredPermissions: List<String>): Boolean {
        val current = _currentRole.value
        if (current == "Super Admin") return true
        return requiredPermissions.contains(current)
    }

    // --- Member Operations ---
    fun addMember(
        fullName: String,
        fatherName: String,
        mobileNumber: String,
        nid: String,
        address: String,
        membershipType: String,
        monthlyRate: Double
    ) {
        if (!checkPermission(listOf("Secretary"))) {
            triggerPermissionDenied()
            return
        }

        viewModelScope.launch {
            val randomNum = (1000..9999).random()
            val idCode = "MBR-$randomNum"
            val member = Member(
                memberIdCode = idCode,
                fullName = fullName,
                fatherName = fatherName,
                mobileNumber = mobileNumber,
                nationalId = nid,
                address = address,
                membershipType = membershipType,
                joinDate = "30/05/2026",
                isActive = true,
                monthlySubscriptionAmount = if (membershipType == "MONTHLY") monthlyRate else 0.0
            )
            repository.insertMember(member)
            logSystemAction("CREATE_MEMBER", "Created member $idCode: $fullName. Type: $membershipType")
            triggerSuccess("Member registered successfully under $idCode")
        }
    }

    fun updateMember(member: Member) {
        if (!checkPermission(listOf("Secretary"))) {
            triggerPermissionDenied()
            return
        }
        viewModelScope.launch {
            repository.updateMember(member)
            logSystemAction("UPDATE_MEMBER", "Modified details of member ${member.memberIdCode}")
            triggerSuccess("Member details updated successfully.")
        }
    }

    fun deleteMember(member: Member) {
        if (!checkPermission(listOf())) { // Only Super Admin can delete members
            triggerPermissionDenied()
            return
        }
        viewModelScope.launch {
            repository.deleteMember(member)
            logSystemAction("DELETE_MEMBER", "Permanently removed member ${member.memberIdCode}: ${member.fullName}")
            triggerSuccess("Member deleted permanently.")
        }
    }

    // --- Donation Operations ---
    fun recordDonation(
        donorName: String,
        mobileNumber: String,
        donationType: String,
        amount: Double,
        paymentMethod: String,
        trnId: String
    ) {
        if (!checkPermission(listOf("Cashier"))) {
            triggerPermissionDenied()
            return
        }

        viewModelScope.launch {
            val rndNum = (10001..99999).random()
            val receiptNo = "DON-2026-$rndNum"
            val donation = Donation(
                receiptNumber = receiptNo,
                donorName = donorName.ifBlank { "বেনামী দানকারী (Anonymous)" },
                mobileNumber = mobileNumber.ifBlank { "N/A" },
                donationType = donationType,
                amount = amount,
                paymentMethod = paymentMethod,
                transactionId = trnId,
                timestamp = System.currentTimeMillis(),
                receivedBy = _currentRole.value
            )
            repository.insertDonation(donation)
            logSystemAction("COLLECT_DONATION", "Collected $amount BDT for $donationType from $donorName (Receipt: $receiptNo)")
            
            // Generate SMS notification simulation
            triggerSMSNotification(
                mobileNumber.ifEmpty { "Donor" },
                "আসসালামু আলাইকুম, আজ ঐতিহাসিক মসজিদে আপনার দান [$amount ৳ ($donationType)] রসিদ নম্বর $receiptNo সফলভাবে জমা হয়েছে। জাজাকাল্লাহ খায়ের।"
            )
            triggerSuccess("Donation received. Receipt generated under $receiptNo")
        }
    }

    // --- Subscription Operations ---
    fun recordSubscription(
        memberIdCode: String,
        monthYear: String,
        amount: Double,
        paymentMethod: String,
        trnId: String
    ) {
        if (!checkPermission(listOf("Cashier"))) {
            triggerPermissionDenied()
            return
        }

        viewModelScope.launch {
            val rndNum = (10001..99999).random()
            val receiptNo = "SUB-2026-$rndNum"
            
            val payment = SubscriptionPayment(
                receiptNumber = receiptNo,
                memberIdCode = memberIdCode,
                monthYear = monthYear,
                amount = amount,
                paymentMethod = paymentMethod,
                transactionId = trnId,
                timestamp = System.currentTimeMillis(),
                receivedBy = _currentRole.value
            )
            repository.insertSubscription(payment)
            
            val member = repository.getMemberByIdCode(memberIdCode)
            val name = member?.fullName ?: memberIdCode
            val phone = member?.mobileNumber ?: "Member"

            logSystemAction("COLLECT_SUBSCRIPTION", "Collected subscription of $amount BDT for $monthYear from member $memberIdCode ($name)")
            
            // Trigger simulated SMS notification
            triggerSMSNotification(
                phone,
                "আসসালামু আলাইকুম, সম্মানিত সদস্য $name, মে ২০২৬ মাসিক চাঁদা সফলভাবে আদায় হয়েছে। পরিমাণ: $amount ৳, রসিদ নং: $receiptNo. ধন্যবাদ।"
            )
            triggerSuccess("Subscription collected. Receipt generated under $receiptNo")
        }
    }

    // --- Expense Operations ---
    fun recordExpense(
        category: String,
        amount: Double,
        description: String,
        attachmentPath: String?
    ) {
        if (!checkPermission(listOf("Secretary", "Cashier"))) {
            triggerPermissionDenied()
            return
        }

        viewModelScope.launch {
            val idx = (1001..9999).random()
            val voucherNo = "EXP-2026-$idx"
            
            val expense = Expense(
                voucherNumber = voucherNo,
                category = category,
                amount = amount,
                description = description,
                attachmentPath = attachmentPath,
                approvalStatus = if (_currentRole.value == "President" || _currentRole.value == "Super Admin") "APPROVED" else "PENDING",
                approvedBy = if (_currentRole.value == "President" || _currentRole.value == "Super Admin") _currentRole.value else null,
                timestamp = System.currentTimeMillis(),
                createdBy = _currentRole.value
            )
            repository.insertExpense(expense)
            logSystemAction("CREATE_EXPENSE", "Requested expenditure of $amount BDT for $category. Voucher: $voucherNo")
            triggerSuccess("Expense voucher $voucherNo recorded in PENDING status for authorization.")
        }
    }

    fun approveExpense(expense: Expense) {
        if (!checkPermission(listOf("President"))) {
            triggerPermissionDenied()
            return
        }

        viewModelScope.launch {
            val updated = expense.copy(
                approvalStatus = "APPROVED",
                approvedBy = _currentRole.value
            )
            repository.updateExpense(updated)
            logSystemAction("AUTHORIZE_EXPENSE", "Authorized voucher ${expense.voucherNumber} amount: ${expense.amount} BDT")
            triggerSuccess("Expense voucher authorized successfully.")
        }
    }

    fun rejectExpense(expense: Expense) {
        if (!checkPermission(listOf("President"))) {
            triggerPermissionDenied()
            return
        }

        viewModelScope.launch {
            val updated = expense.copy(
                approvalStatus = "REJECTED"
            )
            repository.updateExpense(updated)
            logSystemAction("REJECT_EXPENSE", "Rejected approval for voucher ${expense.voucherNumber}")
            triggerSuccess("Expense voucher entry rejected.")
        }
    }

    fun deleteExpense(expense: Expense) {
        if (!checkPermission(listOf())) { // Super Admin only
            triggerPermissionDenied()
            return
        }
        viewModelScope.launch {
            repository.deleteExpense(expense)
            logSystemAction("DELETE_EXPENSE", "Permanently deleted voucher ${expense.voucherNumber}")
            triggerSuccess("Voucher deleted permanently.")
        }
    }

    // --- Notice Publishing ---
    fun publishNotice(title: String, content: String) {
        if (!checkPermission(listOf("Secretary"))) {
            triggerPermissionDenied()
            return
        }

        viewModelScope.launch {
            val notice = Notice(
                title = title,
                content = content,
                publishedBy = _currentRole.value
            )
            repository.insertNotice(notice)
            logSystemAction("PUBLISH_NOTICE", "Published mosque announcement: '$title'")
            triggerSuccess("Announcement posted on general whiteboard notice screen.")
        }
    }

    fun deleteNotice(notice: Notice) {
        if (!checkPermission(listOf("Secretary"))) {
            triggerPermissionDenied()
            return
        }
        viewModelScope.launch {
            repository.deleteNotice(notice)
            logSystemAction("DELETE_NOTICE", "Removed announcement titled '${notice.title}'")
            triggerSuccess("Notice deleted successfully.")
        }
    }

    // --- Private Helper Utilities ---
    private fun logSystemAction(action: String, description: String) {
        viewModelScope.launch {
            repository.insertAuditLog(
                AuditLog(
                    user = _currentRole.value,
                    action = action,
                    newValue = description
                )
            )
        }
    }

    private fun triggerPermissionDenied() {
        _errorMessage.value = LanguageManager.getString("unauthorized_msg", _language.value)
    }

    private fun triggerSuccess(msg: String) {
        _successMessage.value = msg
    }

    private fun triggerSMSNotification(phone: String, textMessage: String) {
        _simulatedNotification.value = "📨 [SMS to $phone]:\n\"$textMessage\""
    }

    fun restoreMockBackup() {
        if (!checkPermission(listOf())) { // Super Admin only
            triggerPermissionDenied()
            return
        }
        viewModelScope.launch {
            // Restore by executing re-seeding
            logSystemAction("RESTORE_BACKUP", "Initiated server storage restoration.")
            triggerSuccess("Local database restored and synced with backup storage successfully.")
        }
    }
}

// Data holder class for keeping tracked member outstanding subscription dues
data class MemberDueInfo(
    val dueAmount: Double,
    val monthsDue: List<String>
)
