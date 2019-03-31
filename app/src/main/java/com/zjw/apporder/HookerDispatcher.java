package com.zjw.apporder;

import net.androidwing.hotxposed.IHookerDispatcher;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created  on 2018/3/30.
 */
public class HookerDispatcher implements IHookerDispatcher {
    @Override
    public void dispatch(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if (loadPackageParam.packageName.equals("com.zjw.apporder")) {

            Class clazz = null;
            try {
                clazz = loadPackageParam.classLoader.loadClass("com.zjw.apporder.MainActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("你已被劫持 哈哈哈哈");
                    XposedBridge.log("别看了，老子已经成功Hook 哈哈哈哈");
                }
            });
        }
    }
}