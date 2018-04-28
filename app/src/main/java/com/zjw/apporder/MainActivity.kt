package com.zjw.apporder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabNames = ArrayList<String>()
        var fragments = ArrayList<MyFragemnt>()
        var strList = arrayListOf<String>(
            "A卡A",
            "A卡 卡A",
            "A卡 卡 卡A",
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

        //设置tablayout 切换tab的动画
        tabLayout.isNeedSwitchAnimation = true

        //设置tablayout固定线宽
        //tabLayout.selectedTabIndicatorWidth = dpToPx(27)
        //设置tablayout 线宽为包裹内容 (与设置tablayout固定线宽 互斥 所以尽量使用一个)
        tabLayout.setIndicatorWidthWrapContent(true)
        //还原成原来的tablayout默认线宽 (与设置tablayout固定线宽和包裹内容 互斥 所以尽量使用一个)
        //tabLayout.selectedTabIndicatorWidth = -1

        tabLayout.setupWithViewPager(viewPager)
        //指示器不需要显示PageAdapter 中的标题所以禁掉
        page_indicator.setPageTitleVisible(false)
        page_indicator.setupWithViewPager(viewPager)
    }

    internal fun dpToPx(dps: Int): Int {
        return Math.round(resources.displayMetrics.density * dps)
    }
}
