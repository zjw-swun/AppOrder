package com.zjw.apporder

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.eye.cool.permission.PermissionHelper

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects

/**
 * 截屏工具类
 * Created by Administrator on 2017/2/10.
 */
object ScreenShotUtils {
    /**
     * 进行截取屏幕
     *
     * @param pActivity
     * @return
     */
    fun takeScreenShot(pActivity: Activity): Bitmap? {
        var bitmap: Bitmap? = null
        val view = pActivity.window.decorView
        // 设置是否可以进行绘图缓存
        view.isDrawingCacheEnabled = true
        // 如果绘图缓存无法，强制构建绘图缓存
        view.buildDrawingCache()
        // 返回这个缓存视图
        bitmap = view.drawingCache
        val width = pActivity.windowManager.defaultDisplay.width
        val height = pActivity.windowManager.defaultDisplay.height
        // 根据坐标点和需要的宽和高创建bitmap
        bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, width, height)
        return bitmap
    }

    /**
     * 保存图片到sdcard中
     *
     * @param pBitmap
     */
    private fun savePic(pBitmap: Bitmap?, strName: String): Boolean {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(strName)
            if (null != fos) {
                pBitmap!!.compress(Bitmap.CompressFormat.PNG, 90, fos)
                fos.flush()
                fos.close()
                return true
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 截图
     *
     * @param pActivity
     * @return 截图并且保存sdcard成功返回true，否则返回false
     */
    fun shotBitmap(pActivity: Activity) {
        //判断是否有sdcard
        var sdDir: File? = null
        //判断sd卡是否存在
        val sdCardExist = Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED
        if (sdCardExist) {
            //权限处理
            PermissionHelper.Builder(pActivity)
                    .permissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))         // 设置需要请求的权限组
                    .permissionCallback {
                        if (it) {
                            // 请求权限成功
                            sdDir = Environment.getExternalStorageDirectory()//获取跟目录
                            ScreenShotUtils.savePic(takeScreenShot(pActivity), sdDir.toString() + "/" + "1.png")
                            Toast.makeText(pActivity, "截图保存路径：" + sdDir.toString() + "/" + "1.png", Toast.LENGTH_SHORT).show()
                        } else {
                            // 请求权限失败
                            Toast.makeText(pActivity, "无储存卡读写权限", Toast.LENGTH_SHORT).show()
                        }
                    }.build()
                    .request()
        } else {
            Toast.makeText(pActivity, "无储存卡", Toast.LENGTH_SHORT).show()
        }
    }
}