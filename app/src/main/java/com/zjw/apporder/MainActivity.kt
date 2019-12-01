package com.zjw.apporder

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.FrameLayout
import com.zjw.tablayout.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_tab_end.view.*
import kotlinx.android.synthetic.main.item_tab.view.*


class MainActivity : AppCompatActivity() {

    private var eventTime: Long = 0

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
            if (index == tabLayout.tabCount - 1) {
                tab?.setCustomView(R.layout.item_tab_end)
                val customView = tab?.customView as FrameLayout
                customView.clipEndTv.text = strList[strList.size - 1]
            } else {
                //为每个标签设置自定义布局(如果设置了自定义view 原来系统默认的ImageView和TextView 为gone)
                tab?.setCustomView(R.layout.item_tab)
                val customView = tab?.customView as FrameLayout
                customView.clipTv.text = strList[index]
            }
        }
        tx.setOnClickListener {
            createMotionEvent()
        }
    }

    /**
     * 使用自定义事件实现左滑一页和右滑一页(当复写PageAdapter的getPageWidth时viewpager的setCurrentItem失效)
     */
    private fun createMotionEvent() {
        //模拟滑动viewpager
        val resources = this.resources
        val dm = resources.displayMetrics
        val widthPixels = dm.widthPixels
        val ds = widthPixels
        //x最大值尽量保持在 240以内 为了兼容低分辨率手机
        //index减小
        val x = arrayListOf<Float>(
                16.992188f,
                18.999023f,
                36.025677f,
                63.36489f,
                87.826065f,
                110.195786f,
                154.99512f,
                154.99512f
        )

        //index减大
        /* val x = arrayListOf<Float>(
                 212.98828f,
                 210.98145f,
                 200.78564f,
                 181.64513f,
                 155.28862f,
                 65.821724f,
                 0.0f,
                 0.0f
         )*/
        val y = arrayListOf<Float>(
                110f,
                110f,
                110f,
                110f,
                110f,
                110f,
                110f,
                110f
        )

        val downTime = SystemClock.uptimeMillis()
        eventTime = downTime

        val motionEventList = arrayListOf<MotionEvent>(
                MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x[0], y[0], 0),
                MotionEvent.obtain(downTime, getNewEventTime(), MotionEvent.ACTION_MOVE, x[1], y[1], 0),
                MotionEvent.obtain(downTime, getNewEventTime(), MotionEvent.ACTION_MOVE, x[2], y[2], 0),
                MotionEvent.obtain(downTime, getNewEventTime(), MotionEvent.ACTION_MOVE, x[3], y[3], 0),
                MotionEvent.obtain(downTime, getNewEventTime(), MotionEvent.ACTION_MOVE, x[4], y[4], 0),
                MotionEvent.obtain(downTime, getNewEventTime(), MotionEvent.ACTION_MOVE, x[5], y[5], 0),
                MotionEvent.obtain(downTime, getNewEventTime(), MotionEvent.ACTION_MOVE, x[6], y[6], 0),
                MotionEvent.obtain(downTime, getNewEventTime(), MotionEvent.ACTION_UP, x[7], y[7], 0)
        )
        motionEventList.forEach {
            viewPager.dispatchTouchEvent(it)
        }
    }

    private fun getNewEventTime(): Long {
        eventTime += 15
        return eventTime
    }

    internal fun dpToPx(dps: Int): Int {
        return Math.round(resources.displayMetrics.density * dps)
    }
}
