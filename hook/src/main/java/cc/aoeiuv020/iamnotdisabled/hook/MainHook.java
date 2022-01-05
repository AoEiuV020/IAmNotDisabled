package cc.aoeiuv020.iamnotdisabled.hook;

import java.util.Collections;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by AoEiuV020 on 2022.01.06-03:25:32.
 */
@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookZygoteInit {
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
    }
}
