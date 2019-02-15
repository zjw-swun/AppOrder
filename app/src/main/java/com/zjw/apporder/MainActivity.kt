package com.zjw.apporder

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.zjw.tablayout.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabNames = ArrayList<String>()
        var fragments = ArrayList<MyFragemnt>()
        var strList = arrayListOf(
                "A卡A",
                "A卡 卡A",
                "A卡 卡 卡A"
                ,
                "A卡 卡 卡 卡A",
                "A卡 卡 卡A",
                "A卡 卡A",
                "A卡A"
        )

        strList.forEach {
            tabNames.add(it)
            fragments.add(MyFragemnt())
        }
        val adapter = PageAdapter(supportFragmentManager, tabNames, fragments)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = fragments.size

        //tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        tabLayout.tabGravity = TabLayout.GRAVITY_CENTER
        //tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        //设置tablayout 切换tab的动画
        tabLayout.isNeedSwitchAnimation = true
        //设置tablayout固定线宽 (设置的线宽大于最小tab宽度时 设置线宽失效 用默认tabLayout线宽显示逻辑)
        //tabLayout.selectedTabIndicatorWidth = dpToPx(10)
        //判断tabLayout线宽是否为默认tabLayout线宽显示逻辑 (可用于判断设置固定线宽是否失效)
        //Log.e("Tag","isDefaultIndicatorWidth "+ tabLayout.isDefaultIndicatorWidth)

        //设置tablayout 线宽为包裹内容 (与设置tablayout固定线宽 互斥 所以尽量使用一个,包裹内容优先级高于设置指定线宽)
        tabLayout.setIndicatorWidthWrapContent(true)
        //还原成原来的tablayout默认线宽 (与设置tablayout固定线宽和包裹内容 互斥 所以尽量使用一个，在不指定固定线宽和包裹内容情况下为tablayout默认线宽)
        //tabLayout.selectedTabIndicatorWidth = -1

        tabLayout.setupWithViewPager(viewPager)

        //指示器不需要显示PageAdapter 中的标题所以禁掉
        page_indicator.setPageTitleVisible(false)
        page_indicator.setupWithViewPager(viewPager)

        for (index in 0 until tabLayout.tabCount) {
            //依次获取标签
            val tab = tabLayout.getTabAt(index)
            //为每个标签设置自定义布局(如果设置了自定义view 原来系统默认的ImageView和TextView 为gone)
            tab?.setCustomView(R.layout.item_tab)
        }
        val decorView = this@MainActivity.window.decorView
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPageSelected(position: Int) {
                if (position % 2 == 0) {
                    setStatusBarTextColor(true)
                } else {
                    setStatusBarTextColor(false)
                }
            }

        })

        //ViewOutlineProvider API21开始提供的 可以用于裁剪view的形状
       /* tx.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                 outline.setOval(0, 0,100, 100)
            }
        }
        tx.clipToOutline = true*/


        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            val res = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            res?.let {
                val myContext = createPackageContext(res.activityInfo.packageName,
                        Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY)
                //myContext.bindService()
                Log.e("TAG", myContext.packageCodePath)
                Log.e("TAG", this@MainActivity.packageCodePath)

                //获取已安装的app
                val installedPackages = myContext.packageManager.getInstalledPackages(0)
                val packages = this@MainActivity.packageManager.getInstalledPackages(0)
                installedPackages.forEach {
                    Log.e("TAG", it.toString())
                }
                //启动某个apk
               /* val intent1 = Intent()
                intent1.component = ComponentName("com.souban.creams.zhongxin.debug",
                        "com.souban.creams.business.LauncherActivity")
                myContext.startActivity(intent1)*/
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        /*val myDrawable = MyDrawable(this)
        myDrawable.shadowColor = Color.parseColor("#66000000")
        myDrawable.shadowDx = dpToPx(5) * 1F
        myDrawable.shadowDy = 0F
        myDrawable.shadowRadius = dpToPx(10) * 1F
        myDrawable.shadowShape = MyDrawable.SHAPE_OVAL
        myDrawable.shadowSide = MyDrawable.ALL
        myRoot.background = myDrawable*/
        tx.setOnClickListener {
            Toast.makeText(this@MainActivity,"点击",Toast.LENGTH_SHORT).show()

            var start = System.nanoTime()
            for (i in 0..10000){
                arrayListOf(
                        "A卡A",
                        "A卡 卡A",
                        "A卡 卡 卡A"
                        ,
                        "A卡 卡 卡 卡A",
                        "A卡 卡 卡A",
                        "A卡 卡A",
                        "A卡A"
                )
            }
            var end = System.nanoTime()
            Log.e("TAG","onCreate "+(end-start)/1000000)
            Toast.makeText(this@MainActivity,"内存分配完毕",Toast.LENGTH_SHORT).show()
            forceGC()
        }

        myRoot.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                myRoot.viewTreeObserver.removeOnGlobalLayoutListener(this)
                ScreenShotUtils.shotBitmap(this@MainActivity)
            }
        })
    }

    @SuppressLint("PrivateApi")
    private fun forceGC() {
        val myClass = classLoader.loadClass("com.android.internal.os.BinderInternal")
        val newInstanceMethod = myClass.getDeclaredMethod("forceGc",String::class.java)
        newInstanceMethod.isAccessible = true
        newInstanceMethod.invoke(null, "bg")
    }


    fun cutPic(bitmap: Bitmap): Bitmap {
        var mMask: Bitmap? = null
        var mImageChanged: Bitmap? = null
        var result: Bitmap? = null
        //ic_launcher_round是你用作图工具做出的形状图片   注意 ic_launcher_round内部必须是非透明的才能达到效果
        mMask = resizeImage(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_round), 500, 500)
        val matrix = Matrix()
        matrix.postScale(mMask!!.width.toFloat() / bitmap.width.toFloat(), mMask.height.toFloat() / bitmap.height.toFloat())
        //将图片缩放到和形状图片一样的大小
        mImageChanged = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        result = Bitmap.createBitmap(mMask.width, mMask.height, Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(result!!)
        val paint = Paint()
        //paint.color = -0x1
        paint.color = Color.RED
        paint.isAntiAlias = true

        //切割模式，取交集上层
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN) as Xfermode?
        mCanvas.drawBitmap(mMask, 0f, 0f, null)
        //mCanvas.drawColor(Color.RED, PorterDuff.Mode.MULTIPLY)
        mCanvas.drawBitmap(mImageChanged!!, 0f, 0f, paint)

        paint.xfermode = null

        return result
    }

    //防止绘图时候OOM 进行压缩
    fun resizeImage(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        return Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true)
    }

    /**
     * @param: isLight true代表黑字，false代表白字
     */
    private fun setStatusBarTextColor(isLight: Boolean) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        if (isDestroyed || isFinishing) {
            return
        }
        val decorView = window.decorView
        if (isLight) {
            decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decorView.systemUiVisibility = decorView.systemUiVisibility and (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
        }
    }


    internal fun dpToPx(dps: Int): Int {
        return Math.round(resources.displayMetrics.density * dps)
    }

  /*  override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        ScreenShotUtils.shotBitmap(this@MainActivity)
    }*/

}
