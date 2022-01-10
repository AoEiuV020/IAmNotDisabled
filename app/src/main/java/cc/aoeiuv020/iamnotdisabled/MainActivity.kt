package cc.aoeiuv020.iamnotdisabled

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.tvResult).setOnClickListener {
            checkDisabled()
        }
        checkDisabled()
    }

    @SuppressLint("SetTextI18n")
    private fun checkDisabled() {
        val tvResult: TextView = findViewById(R.id.tvResult)
        val tvReason: TextView = findViewById(R.id.tvReason)

        val serviceList = getFromAccessibilityManager() + getFromSettingsSecure()
        if (serviceList.isNotEmpty()) {
            tvResult.text = "你是残疾人！"
            tvReason.text = "因为你正在使用：\n" + serviceList.joinToString("\n")
        } else {
            tvResult.text = "你很健康"
            tvReason.text = ""
        }
    }

    private fun getFromAccessibilityManager(): List<String> {
        val accessibilityManager =
            ContextCompat.getSystemService(this, AccessibilityManager::class.java)
                ?: error("unreachable")
        val serviceList: List<AccessibilityServiceInfo> =
            accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
                ?: emptyList()
        return serviceList.map {
            packageManager.getApplicationLabel(it.resolveInfo.serviceInfo.applicationInfo)
                .toString()
        }
    }

    private fun getFromSettingsSecure(): List<String> {
        val settingValue = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return if (settingValue.isEmpty()) {
            emptyList()
        } else {
            settingValue.split(':')
        }
    }
}