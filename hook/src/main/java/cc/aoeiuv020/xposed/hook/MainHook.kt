package cc.aoeiuv020.xposed.hook

import android.accessibilityservice.AccessibilityServiceInfo
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class MainHook : IXposedHookZygoteInit {
    @Throws(Throwable::class)
    override fun initZygote(startupParam: StartupParam) {
        XposedBridge.log("initZygote: " + startupParam.modulePath)
        XposedHelpers.findAndHookMethod(
            "android.view.accessibility.AccessibilityManager",
            null,
            "getEnabledAccessibilityServiceList",
            Int::class.javaPrimitiveType,
            XC_MethodReplacement.returnConstant(emptyList<AccessibilityServiceInfo>())
        )
    }
}