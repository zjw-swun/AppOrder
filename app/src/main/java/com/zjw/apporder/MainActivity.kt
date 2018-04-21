package com.zjw.apporder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in 0..5) {
            tabLayout1.addTab(tabLayout1.newTab().setText("卡$i"))
            tabLayout2.addTab(tabLayout2.newTab().setText("卡$i"))
        }

        var tabNames = ArrayList<String>()
        var fragments = ArrayList<MyFragemnt>()
        for (i in 0..9) {
            tabLayout3.addTab(tabLayout3.newTab().setText("卡$i"))
           // tabLayout4.addTab(tabLayout4.newTab().setText("卡$i"))
            tabNames.add("卡$i")
            fragments.add(MyFragemnt())
        }

        val mVpAndTLAdapter = VPAndTLAdapter(supportFragmentManager, tabNames, fragments)
        viewPager.adapter = mVpAndTLAdapter
        tabLayout4.setupWithViewPager(viewPager)
        page_indicator.setPageTitleVisible(false)
        page_indicator.setupWithViewPager(viewPager)
    }
}
