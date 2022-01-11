package cc.aoeiuv020.iamnotdisabled.hook;

import android.content.ContentResolver;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

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
                if (Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES.equals(methodHookParam2.args[1])) {
                    methodHookParam2.setResult("");
                }
            }
        });
        XposedHelpers.findAndHookMethod("android.provider.Settings$Secure", lpparam.classLoader, "getInt", ContentResolver.class, String.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam methodHookParam2) throws Throwable {
                if (Settings.Secure.ACCESSIBILITY_ENABLED.equals(methodHookParam2.args[1])) {
                    methodHookParam2.setResult(0);
                }
            }
        });
    }

    private boolean doNotHack(Throwable throwable) {
        for (int i = 0; i < throwable.getStackTrace().length; i++) {
            StackTraceElement stackTraceElement = throwable.getStackTrace()[i];
            if (i == 3 && (
                    stackTraceElement.getClassName().startsWith("android.")
                            || stackTraceElement.getClassName().startsWith("androidx.")
                            || stackTraceElement.getClassName().startsWith("com.android.")
                            || stackTraceElement.getClassName().startsWith("org.chromium.content.browser.")
            )) {
                return true;
            }
        }
        return false;
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
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Throwable throwable = new Throwable();
                        if (doNotHack(throwable)) return;
                        param.setResult(false);
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "isTouchExplorationEnabled",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Throwable throwable = new Throwable();
                        if (doNotHack(throwable)) return;
                        param.setResult(false);
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addAccessibilityStateChangeListener",
                AccessibilityManager.AccessibilityStateChangeListener.class,
                XC_MethodReplacement.returnConstant(true)
        );
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addAccessibilityStateChangeListener",
                AccessibilityManager.AccessibilityStateChangeListener.class,
                Handler.class,
                XC_MethodReplacement.returnConstant(null)
        );
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addTouchExplorationStateChangeListener",
                AccessibilityManager.TouchExplorationStateChangeListener.class,
                XC_MethodReplacement.returnConstant(true)
        );
        XposedHelpers.findAndHookMethod(
                "android.view.accessibility.AccessibilityManager",
                null,
                "addTouchExplorationStateChangeListener",
                AccessibilityManager.TouchExplorationStateChangeListener.class,
                Handler.class,
                XC_MethodReplacement.returnConstant(null)
        );
    }
}
