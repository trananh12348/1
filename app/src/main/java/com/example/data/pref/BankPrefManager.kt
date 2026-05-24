package com.example.data.pref

import android.content.Context
import android.content.SharedPreferences

class BankPrefManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("com_ngon_bank_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BANK_NAME = "key_bank_name"
        private const val KEY_ACCOUNT_NUMBER = "key_account_number"
        private const val KEY_ACCOUNT_OWNER = "key_account_owner"
        private const val KEY_USE_CUSTOM_QR = "key_use_custom_qr"
        private const val KEY_CUSTOM_QR_PATH = "key_custom_qr_path"
    }

    var bankName: String
        get() = prefs.getString(KEY_BANK_NAME, "MB") ?: "MB"
        set(value) = prefs.edit().putString(KEY_BANK_NAME, value).apply()

    var accountNumber: String
        get() = prefs.getString(KEY_ACCOUNT_NUMBER, "0999999999") ?: "0999999999"
        set(value) = prefs.edit().putString(KEY_ACCOUNT_NUMBER, value).apply()

    var accountOwner: String
        get() = prefs.getString(KEY_ACCOUNT_OWNER, "NGUYEN VAN A") ?: "NGUYEN VAN A"
        set(value) = prefs.edit().putString(KEY_ACCOUNT_OWNER, value).apply()

    var useCustomQr: Boolean
        get() = prefs.getBoolean(KEY_USE_CUSTOM_QR, false)
        set(value) = prefs.edit().putBoolean(KEY_USE_CUSTOM_QR, value).apply()

    var customQrPath: String
        get() = prefs.getString(KEY_CUSTOM_QR_PATH, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_QR_PATH, value).apply()

    var appTheme: String
        get() = prefs.getString("key_app_theme", "foodgo") ?: "foodgo"
        set(value) = prefs.edit().putString("key_app_theme", value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("key_is_sound_enabled", true)
        set(value) = prefs.edit().putBoolean("key_is_sound_enabled", value).apply()

    var categories: List<String>
        get() = prefs.getString("key_categories", "Món chính,Ăn nhẹ,Thức uống")?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: listOf("Món chính", "Ăn nhẹ", "Thức uống")
        set(value) = prefs.edit().putString("key_categories", value.joinToString(",")).apply()
}
