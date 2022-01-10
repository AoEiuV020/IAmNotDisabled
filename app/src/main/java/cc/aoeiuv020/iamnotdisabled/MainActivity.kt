package cc.aoeiuv020.iamnotdisabled

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.tvResult).setOnClickListener {
            checkDisabled()
        }
        checkDisabled()
        addListener()
    }

    private fun addListener() {
        val accessibilityManager =
            ContextCompat.getSystemService(this, AccessibilityManager::class.java)
                ?: error("unreachable")
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            private val handler = Handler(Looper.getMainLooper())
            private val listener = AccessibilityManager.AccessibilityStateChangeListener {
                updateDisabled(listOf("监听到无障碍服务 " + getEnabledString(it)))
            }
            private val touchExplorationListener =
                AccessibilityManager.TouchExplorationStateChangeListener {
                    updateDisabled(listOf("监听到读屏服务 " + getEnabledString(it)))
                }

            override fun onCreate(owner: LifecycleOwner) {
                accessibilityManager.addAccessibilityStateChangeListener(listener)
                accessibilityManager.addAccessibilityStateChangeListener(listener, handler)
                accessibilityManager.addTouchExplorationStateChangeListener(touchExplorationListener)
                accessibilityManager.addTouchExplorationStateChangeListener(
                    touchExplorationListener,
                    handler
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                accessibilityManager.removeAccessibilityStateChangeListener(listener)
                accessibilityManager.removeTouchExplorationStateChangeListener(touchExplorationListener)
            }
        })
    }

    private fun getEnabledString(enabled: Boolean) = if (enabled) "启用" else "禁用"

    private fun checkDisabled() {
        val serviceList = getFromAccessibilityManager() + getFromSettingsSecure()
        updateDisabled(serviceList)
    }

    @SuppressLint("SetTextI18n")
    private fun updateDisabled(
        serviceList: List<String>
    ) {
        val tvResult: TextView = findViewById(R.id.tvResult)
        val tvReason: TextView = findViewById(R.id.tvReason)
        if (serviceList.isNotEmpty()) {
            tvResult.text = "你是残疾人！"
            tvReason.text = "因为：\n" + serviceList.joinToString("\n")
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
        val nameList = serviceList.map {
            packageManager.getApplicationLabel(it.resolveInfo.serviceInfo.applicationInfo)
                .toString()
        }.toMutableList()
        if (accessibilityManager.isEnabled) {
            nameList.add("AccessibilityManager.isEnabled")
        }
        if (accessibilityManager.isTouchExplorationEnabled) {
            nameList.add("AccessibilityManager.isTouchExplorationEnabled")
        }
        return nameList
    }

    private fun getFromSettingsSecure(): List<String> {
        val settingValue = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val nameList = if (settingValue.isEmpty()) {
            emptyList()
        } else {
            settingValue.split(':')
        }.toMutableList()
        val enabled = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        if (enabled != 0) {
            nameList.add("ACCESSIBILITY_ENABLED == $enabled")
        }
        return nameList
    }
}