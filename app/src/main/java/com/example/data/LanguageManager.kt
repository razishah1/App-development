package com.example.data

object LanguageManager {

    enum class Language {
        BENGALI, ENGLISH
    }

    private val translations = mapOf(
        "app_title" to mapOf(
            Language.BENGALI to "মসজিদ অর্থায়ন ও হিসাব ব্যবস্থাপনা",
            Language.ENGLISH to "Mosque Finance & Management"
        ),
        "tagline" to mapOf(
            Language.BENGALI to "মসজিদ হিসাব রক্ষণ ও সদস্য চাঁদা ও দান ব্যবস্থা",
            Language.ENGLISH to "Mosque Accounting & Subscription Ledger"
        ),
        // Navigation / Tabs
        "tab_dashboard" to mapOf(
            Language.BENGALI to "ড্যাশবোর্ড",
            Language.ENGLISH to "Dashboard"
        ),
        "tab_members" to mapOf(
            Language.BENGALI to "সদস্যবৃন্দ",
            Language.ENGLISH to "Members"
        ),
        "tab_donations" to mapOf(
            Language.BENGALI to "দানসমূহ",
            Language.ENGLISH to "Donations"
        ),
        "tab_subscriptions" to mapOf(
            Language.BENGALI to "মাসিক চাঁদা",
            Language.ENGLISH to "Subscriptions"
        ),
        "tab_expenses" to mapOf(
            Language.BENGALI to "ব্যয়সমূহ",
            Language.ENGLISH to "Expenses"
        ),
        "tab_reports" to mapOf(
            Language.BENGALI to "রিপোর্টস",
            Language.ENGLISH to "Reports"
        ),
        "tab_notices" to mapOf(
            Language.BENGALI to "নোটিশ বোর্ড",
            Language.ENGLISH to "Notice Board"
        ),
        "tab_audit" to mapOf(
            Language.BENGALI to "অডিট লগ",
            Language.ENGLISH to "Audit Logs"
        ),
        "tab_settings" to mapOf(
            Language.BENGALI to "সেটিংস",
            Language.ENGLISH to "Settings"
        ),
        // Dashboard cards
        "total_members" to mapOf(
            Language.BENGALI to "মোট সদস্য",
            Language.ENGLISH to "Total Members"
        ),
        "active_members" to mapOf(
            Language.BENGALI to "সক্রিয় সদস্য",
            Language.ENGLISH to "Active Members"
        ),
        "inactive_members" to mapOf(
            Language.BENGALI to "নিষ্ক্রিয় সদস্য",
            Language.ENGLISH to "Inactive Members"
        ),
        "today_collection" to mapOf(
            Language.BENGALI to "আজকের সংগ্রহ",
            Language.ENGLISH to "Today's Collection"
        ),
        "monthly_collection" to mapOf(
            Language.BENGALI to "চলতি মাসের চাঁদা",
            Language.ENGLISH to "Monthly Subs Collection"
        ),
        "annual_collection" to mapOf(
            Language.BENGALI to "চলতি বছরের সংগ্রহ",
            Language.ENGLISH to "Annual Collection"
        ),
        "total_income" to mapOf(
            Language.BENGALI to "মোট আয়",
            Language.ENGLISH to "Total Income"
        ),
        "total_expense" to mapOf(
            Language.BENGALI to "মোট ব্যয়",
            Language.ENGLISH to "Total Expense"
        ),
        "current_balance" to mapOf(
            Language.BENGALI to "বর্তমান তহবিল",
            Language.ENGLISH to "Current Balance"
        ),
        "due_amount" to mapOf(
            Language.BENGALI to "মোট বকেয়া চাঁদা",
            Language.ENGLISH to "Total Due Subscriptions"
        ),
        // Currency Symbol
        "currency_symbol" to mapOf(
            Language.BENGALI to "৳",
            Language.ENGLISH to "BDT"
        ),
        // Buttons
        "btn_add_member" to mapOf(
            Language.BENGALI to "নতুন সদস্য যোগ করুন",
            Language.ENGLISH to "Add New Member"
        ),
        "btn_add_donation" to mapOf(
            Language.BENGALI to "দান / রসিদ সংগ্রহ",
            Language.ENGLISH to "New Donation Receipt"
        ),
        "btn_collect_sub" to mapOf(
            Language.BENGALI to "চাঁদা আদায় করুন",
            Language.ENGLISH to "Collect Subscription"
        ),
        "btn_add_expense" to mapOf(
            Language.BENGALI to "নতুন ভাউচার / খরচ",
            Language.ENGLISH to "New Expense Voucher"
        ),
        "btn_publish_notice" to mapOf(
            Language.BENGALI to "নোটিশ প্রকাশ করুন",
            Language.ENGLISH to "Publish Notice"
        ),
        "btn_search" to mapOf(
            Language.BENGALI to "অনুসন্ধান",
            Language.ENGLISH to "Search"
        ),
        "btn_filter" to mapOf(
            Language.BENGALI to "ফিল্টার",
            Language.ENGLISH to "Filter"
        ),
        "btn_view" to mapOf(
            Language.BENGALI to "দেখুন",
            Language.ENGLISH to "View"
        ),
        "btn_edit" to mapOf(
            Language.BENGALI to "সম্পাদনা",
            Language.ENGLISH to "Edit"
        ),
        "btn_delete" to mapOf(
            Language.BENGALI to "ডিলিট",
            Language.ENGLISH to "Delete"
        ),
        "btn_approve" to mapOf(
            Language.BENGALI to "অনুমোদন করুন",
            Language.ENGLISH to "Approve"
        ),
        "btn_reject" to mapOf(
            Language.BENGALI to "প্রত্যাখ্যান",
            Language.ENGLISH to "Reject"
        ),
        "btn_download" to mapOf(
            Language.BENGALI to "ডাউনলোড",
            Language.ENGLISH to "Download"
        ),
        "btn_print" to mapOf(
            Language.BENGALI to "প্রিন্ট",
            Language.ENGLISH to "Print"
        ),
        "btn_share" to mapOf(
            Language.BENGALI to "শেয়ার",
            Language.ENGLISH to "Share"
        ),
        "btn_close" to mapOf(
            Language.BENGALI to "বন্ধ করুন",
            Language.ENGLISH to "Close"
        ),
        "btn_verify" to mapOf(
            Language.BENGALI to "যাচাই করুন",
            Language.ENGLISH to "Verify"
        ),
        "btn_save" to mapOf(
            Language.BENGALI to "সংরক্ষণ করুন",
            Language.ENGLISH to "Save"
        ),
        "btn_cancel" to mapOf(
            Language.BENGALI to "বাতিল",
            Language.ENGLISH to "Cancel"
        ),
        // Auth and roles
        "current_role" to mapOf(
            Language.BENGALI to "আপনার ভূমিকা",
            Language.ENGLISH to "Active Role"
        ),
        "switch_role" to mapOf(
            Language.BENGALI to "ভূমিকা পরিবর্তন করুন (সিমুলেশন)",
            Language.ENGLISH to "Switch User Account (RBAC Simulation)"
        ),
        "role_super_admin" to mapOf(
            Language.BENGALI to "সুপার এডমিন (Super Admin)",
            Language.ENGLISH to "Super Admin"
        ),
        "role_president" to mapOf(
            Language.BENGALI to "সভাপতি (President)",
            Language.ENGLISH to "President"
        ),
        "role_secretary" to mapOf(
            Language.BENGALI to "সেক্রেটারি (Secretary)",
            Language.ENGLISH to "Secretary"
        ),
        "role_cashier" to mapOf(
            Language.BENGALI to "ক্যাশিয়ার (Cashier)",
            Language.ENGLISH to "Cashier"
        ),
        "role_auditor" to mapOf(
            Language.BENGALI to "অডিটর (Auditor)",
            Language.ENGLISH to "Auditor"
        ),
        "role_member" to mapOf(
            Language.BENGALI to "সাধারণ সদস্য (Mosque Member)",
            Language.ENGLISH to "Mosque Member"
        ),
        // Forms & Labels
        "lbl_full_name" to mapOf(
            Language.BENGALI to "পূর্ণ নাম",
            Language.ENGLISH to "Full Name"
        ),
        "lbl_father_name" to mapOf(
            Language.BENGALI to "পিতার নাম",
            Language.ENGLISH to "Father's Name"
        ),
        "lbl_mobile_number" to mapOf(
            Language.BENGALI to "মোবাইল নম্বর",
            Language.ENGLISH to "Mobile Number"
        ),
        "lbl_national_id" to mapOf(
            Language.BENGALI to "জাতীয় পরিচয়পত্র নম্বর",
            Language.ENGLISH to "National ID (NID)"
        ),
        "lbl_address" to mapOf(
            Language.BENGALI to "ঠিকানা",
            Language.ENGLISH to "Address"
        ),
        "lbl_membership_type" to mapOf(
            Language.BENGALI to "সদস্যপদের ধরন",
            Language.ENGLISH to "Membership Type"
        ),
        "lbl_monthly_amount" to mapOf(
            Language.BENGALI to "সদস্য চাঁদার হার (৳)",
            Language.ENGLISH to "Monthly Subscription Rate (৳)"
        ),
        "lbl_join_date" to mapOf(
            Language.BENGALI to "যোগদানের তারিখ",
            Language.ENGLISH to "Join Date"
        ),
        "lbl_status" to mapOf(
            Language.BENGALI to "অবস্থা",
            Language.ENGLISH to "Status"
        ),
        "lbl_member_id" to mapOf(
            Language.BENGALI to "সদস্য আইডি",
            Language.ENGLISH to "Member ID"
        ),
        "lbl_receipt_number" to mapOf(
            Language.BENGALI to "রসিদ নম্বর",
            Language.ENGLISH to "Receipt Number"
        ),
        "lbl_donor_name" to mapOf(
            Language.BENGALI to "দাতা / দানকারীর নাম",
            Language.ENGLISH to "Donor Name"
        ),
        "lbl_donation_type" to mapOf(
            Language.BENGALI to "দানের ধরন",
            Language.ENGLISH to "Donation Type"
        ),
        "lbl_amount" to mapOf(
            Language.BENGALI to "টাকার পরিমাণ (৳)",
            Language.ENGLISH to "Amount (৳)"
        ),
        "lbl_payment_method" to mapOf(
            Language.BENGALI to "পেমেন্ট মাধ্যম",
            Language.ENGLISH to "Payment Method"
        ),
        "lbl_transaction_id" to mapOf(
            Language.BENGALI to "ট্রানজেকশন আইডি",
            Language.ENGLISH to "Transaction ID"
        ),
        "lbl_month_year" to mapOf(
            Language.BENGALI to "মাস ও বছর (উদা: 05/2026)",
            Language.ENGLISH to "Month/Year (e.g. 05/2026)"
        ),
        "lbl_voucher_number" to mapOf(
            Language.BENGALI to "ভাউচার নম্বর",
            Language.ENGLISH to "Voucher Number"
        ),
        "lbl_expense_category" to mapOf(
            Language.BENGALI to "ব্যয়ের খাত",
            Language.ENGLISH to "Expense Category"
        ),
        "lbl_description" to mapOf(
            Language.BENGALI to "বিবরণ",
            Language.ENGLISH to "Description"
        ),
        "lbl_attachment" to mapOf(
            Language.BENGALI to "সংযুক্ত ভাউচার / ফাইল",
            Language.ENGLISH to "Attachment File"
        ),
        "lbl_approval_status" to mapOf(
            Language.BENGALI to "অনুমোদনের অবস্থা",
            Language.ENGLISH to "Approval Status"
        ),
        "lbl_approved_by" to mapOf(
            Language.BENGALI to "অনুমোদনকারী",
            Language.ENGLISH to "Approved By"
        ),
        "lbl_date" to mapOf(
            Language.BENGALI to "তারিখ",
            Language.ENGLISH to "Date"
        ),
        "lbl_created_by" to mapOf(
            Language.BENGALI to "তৈরি করেছেন",
            Language.ENGLISH to "Creator"
        ),
        // Types
        "type_monthly" to mapOf(
            Language.BENGALI to "মাসিক সদস্য",
            Language.ENGLISH to "Monthly Member"
        ),
        "type_annual" to mapOf(
            Language.BENGALI to "বার্ষিক সদস্য",
            Language.ENGLISH to "Annual Member"
        ),
        "type_lifetime" to mapOf(
            Language.BENGALI to "আজীবন সদস্য",
            Language.ENGLISH to "Lifetime Member"
        ),
        "status_active" to mapOf(
            Language.BENGALI to "সক্রিয়",
            Language.ENGLISH to "Active"
        ),
        "status_inactive" to mapOf(
            Language.BENGALI to "নিষ্ক্রিয়",
            Language.ENGLISH to "Inactive"
        ),
        // Donation Categories
        "don_general" to mapOf(
            Language.BENGALI to "সাধারণ দান (General)",
            Language.ENGLISH to "General Donation"
        ),
        "don_friday" to mapOf(
            Language.BENGALI to "জুমার দিনের দান (Friday)",
            Language.ENGLISH to "Friday Box Donation"
        ),
        "don_zakat" to mapOf(
            Language.BENGALI to "যাকাত তহবিল (Zakat)",
            Language.ENGLISH to "Zakat"
        ),
        "don_fitrah" to mapOf(
            Language.BENGALI to "ফিতরা তহবিল (Fitrah)",
            Language.ENGLISH to "Fitrah"
        ),
        "don_sadaqah" to mapOf(
            Language.BENGALI to "সদকা (Sadaqah)",
            Language.ENGLISH to "Sadaqah"
        ),
        "don_mosque_construction" to mapOf(
            Language.BENGALI to "মসজিদ নির্মাণ তহবিল",
            Language.ENGLISH to "Construction Fund"
        ),
        "don_special" to mapOf(
            Language.BENGALI to "বিশেষ দান (Special)",
            Language.ENGLISH to "Special Donation"
        ),
        "don_other" to mapOf(
            Language.BENGALI to "অন্যান্য দান (Other)",
            Language.ENGLISH to "Other Donation"
        ),
        // Expense types
        "exp_imam_salary" to mapOf(
            Language.BENGALI to "ইমাম সাহেবের সম্মানিত ভাতা",
            Language.ENGLISH to "Imam Salary"
        ),
        "exp_muazzin_salary" to mapOf(
            Language.BENGALI to "মোয়াজ্জিন সাহেবের সম্মানী",
            Language.ENGLISH to "Muazzin Salary"
        ),
        "exp_electricity_bill" to mapOf(
            Language.BENGALI to "বিদ্যুৎ বিল (DESCO)",
            Language.ENGLISH to "Electricity Bill"
        ),
        "exp_water_bill" to mapOf(
            Language.BENGALI to "পানি ও সুয়ারেজ বিল",
            Language.ENGLISH to "Water Bill"
        ),
        "exp_cleaning_expense" to mapOf(
            Language.BENGALI to "পরিচ্ছন্নতা ও রক্ষণাবেক্ষণ খরচ",
            Language.ENGLISH to "Cleaning Expense"
        ),
        "exp_construction_expense" to mapOf(
            Language.BENGALI to "নির্মাণ ও সংস্কার ব্যয়",
            Language.ENGLISH to "Construction Expense"
        ),
        "exp_maintenance_expense" to mapOf(
            Language.BENGALI to "অন্যান্য যন্ত্রপাতি ও সংস্কার বিল",
            Language.ENGLISH to "Maintenance Expense"
        ),
        "exp_other_expense" to mapOf(
            Language.BENGALI to "অন্যান্য ব্যয়",
            Language.ENGLISH to "Other Expense"
        ),
        // Success alerts
        "success_msg" to mapOf(
            Language.BENGALI to "সফলভাবে সম্পন্ন হয়েছে!",
            Language.ENGLISH to "Action Completed Successfully!"
        ),
        "receipt_gen_noti" to mapOf(
            Language.BENGALI to "রসিদ ও পিডিএফ তৈরি করা হয়েছে এবং মোবাইল ফোনে পাঠানো হয়েছে।",
            Language.ENGLISH to "Receipt generated and simulated SMS/Email notification sent."
        ),
        "notice_published" to mapOf(
            Language.BENGALI to "নতুন নোটিশ প্রকাশিত হয়েছে।",
            Language.ENGLISH to "Important announcements notice published successfully."
        ),
        "unauthorized_msg" to mapOf(
            Language.BENGALI to "দুঃখিত! আপনার এই কাজটি করার প্রয়োজনীয় অনুমতি বা ভূমিকা নেই।",
            Language.ENGLISH to "Unauthorized operation for your current Role-Based Access controls."
        )
    )

    fun getString(key: String, language: Language): String {
        return translations[key]?.get(language) ?: key
    }
}
