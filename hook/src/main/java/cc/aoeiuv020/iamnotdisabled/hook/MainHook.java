package cc.aoeiuv020.iamnotdisabled.hook;

import android.content.ContentResolver;
import android.provider.Settings;

import java.util.Collections;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by AoEiuV020 on 2022.01.06-03:25:32.
 */
@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.processName);
        XposedHelpers.findAndHookMethod("android.provider.Settings$Secure", lpparam.classLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam methodHookParam2) throws Throwable {
                if (Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES.equals((String) methodHookParam2.args[1])) {
                    methodHookParam2.setResult("");
                }
            }
        });
        XposedHelpers.findAndHookMethod("android.provider.Settings$Secure", lpparam.classLoader, "getInt", ContentResolver.class, String.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam methodHookParam2) throws Throwable {
                if (Settings.Secure.ACCESSIBILITY_ENABLED.equals((String) methodHookParam2.args[1])) {
                    methodHookParam2.setResult(0);
                }
            }
        });
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedBridge.log("initZygote: " + startupParam.modulePath);
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "getEnabledAccessibilityServiceList",
                int.class,
                XC_MethodReplacement.returnConstant(Collections.emptyList())
        );
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "isEnabled",
                XC_MethodReplacement.returnConstant(false)
        );
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "isTouchExplorationEnabled",
                XC_MethodReplacement.returnConstant(false)
        );
    }
}
