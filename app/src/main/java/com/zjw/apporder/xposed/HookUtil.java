package com.zjw.apporder.xposed;

import com.zjw.apporder.HookerDispatcher;

import net.androidwing.hotxposed.HotXposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookUtil implements IXposedHookLoadPackage {
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
       // HotXposed.hook(HookerDispatcher.class, loadPackageParam);
        if (loadPackageParam.packageName.equals("com.zjw.myapplication")) {

            Class clazz = null;
            try {
                clazz = loadPackageParam.classLoader.loadClass("com.zjw.myapplication.MainActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("你已被劫持 哈哈哈");
                    XposedBridge.log("别看了，老子已经成功Hook 哈哈哈哈");
                }
            });
        }
    }
}