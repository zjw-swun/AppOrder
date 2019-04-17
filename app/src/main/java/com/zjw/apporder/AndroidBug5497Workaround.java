package com.zjw.apporder;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * 正确解决webview不能adjustResize的处理类(包含了沉浸式状态栏以及虚拟按键等情况的适配设置)
 */
public class AndroidBug5497Workaround {
    public static void assistActivity(Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Activity activity;
    private int statusBarHeight;

    private AndroidBug5497Workaround(Activity activity) {
        //获取状态栏的高度
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);

        //界面出现变动都会调用这个监听事件
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });

        frameLayoutParams = (FrameLayout.LayoutParams)
                mChildOfContent.getLayoutParams();
    }

    private boolean mIsFirstResize = true;
    private int mChildOfContentSize = 0;
    private int usableHeightNow;

    private void possiblyResizeChildOfContent() {
        //错误做法拿到usableHeightNow
        // int usableHeightNow = computeUsableHeight();
        if (mIsFirstResize) {
            //保留原来系统默认算好的mChildOfContent高度(根据flag和系统状态栏和下面虚拟按键和谐相处的正确值)
            mChildOfContentSize = mChildOfContent.getHeight();
            mIsFirstResize = false;
        }
        usableHeightNow = mChildOfContent.getHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                //这里不能单纯使用frameLayoutParams.height = usableHeightNow;  因为usableHeightNow和一些flag有关，网上frameLayoutParams.height = usableHeightSansKeyboard 直接就错了，因为usableHeightSansKeyboard恒为全屏高度
                //1.当使用 window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN) 全屏时getWindowVisibleDisplayFrame高度为全屏(系统状态栏和下面虚拟按键呼出悬浮在上面)
                //2.当使用FLAG_TRANSLUCENT_STATUS 的时候getWindowVisibleDisplayFrame拿到的是不包含系统状态栏的高度但是又顶上了系统栏的位置，导致出错
                //综合上述几点正确做法为当前代码实现,即使用系统默认算好的mChildOfContent高度，当键盘唤起，保留当前mChildOfContent高度，键盘消失再设置回去
                frameLayoutParams.height = mChildOfContentSize;
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = frameLayoutParams.height;
        }
    }

    /**
     * 计算mChildOfContent可见高度     ** @return
     */
   /* private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
*/
}