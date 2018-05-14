package com.zjw.apporder;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyViewPager extends ViewPager {
    private int[] mTempPoint;
    private float preLeft;
    private float currentLeft;
    private Boolean shouldIntercept;
    private MotionEvent obtain;
    private int mState;

    public MyViewPager(@NonNull Context context) {
        super(context);
        init(context);
    }


    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int state) {
                mState = state;
                /*if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    // 用户拖动ViewPager, 取消自动滑动
                } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                    // 滑动结束, 视情况是否重启自动滑动
                }*/
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //要不要走该方法由disallowIntercept决定 (down事件透传之后 子view才能设置disallowIntercept，默认可以拦截)
        View myLayout = null;
        View content = null;
        View button = null;
        if (getAdapter() instanceof PageAdapter) {
            myLayout = ((PageAdapter) getAdapter()).getCurrentPrimaryItem().getView().findViewById(R.id.myLayout);
            content = ((PageAdapter) getAdapter()).getCurrentPrimaryItem().getView().findViewById(R.id.content);
            button = ((PageAdapter) getAdapter()).getCurrentPrimaryItem().getView().findViewById(R.id.button);
        }

        if (myLayout != null && content != null && button != null) {
            final int action = ev.getAction() & MotionEvent.ACTION_MASK;
            //当down事件
            switch (action) {
                //down之后 如果不拦截 down事件透传下去,如果子view树没有消费，则走自己onTouchEvent方法
                //如果拦截 则走onTouchEvent 后续事件也交给自己处理
                case MotionEvent.ACTION_DOWN:
                    preLeft = currentLeft = 0;
                    shouldIntercept = null;
                    obtain = null;

                    //如果落点在content的左边，事件拦截交由viewpager滚动
                    if (canViewReceivePointerEvents(ev.getRawX(), ev.getRawY(), myLayout)
                            && !canViewReceivePointerEvents(ev.getRawX(), ev.getRawY(), content)) {
                        shouldIntercept = true;
                    }
                    if (canViewReceivePointerEvents(ev.getRawX(), ev.getRawY(), content)) {
                        obtain = MotionEvent.obtainNoHistory(ev);
                        shouldIntercept = false;
                        if (mState != ViewPager.SCROLL_STATE_IDLE) {
                            super.onInterceptTouchEvent(obtain);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    preLeft = currentLeft;
                    currentLeft = ev.getRawX();
                    if (obtain != null
                            && content.getLeft() == myLayout.getPaddingLeft()
                            && currentLeft - preLeft < 0
                            && mState == ViewPager.SCROLL_STATE_IDLE) {
                        shouldIntercept = true;
                        obtain.offsetLocation(-obtain.getRawX(), 0);
                        obtain.offsetLocation(currentLeft, 0);
                        super.onInterceptTouchEvent(obtain);
                        obtain = null;
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }

        if (shouldIntercept != null && !shouldIntercept) {
            return false;
        }
        boolean onInterceptTouchEvent = super.onInterceptTouchEvent(ev);
        return shouldIntercept == null ? onInterceptTouchEvent : shouldIntercept | onInterceptTouchEvent;
    }

    int dpToPx(int dps) {
        return Math.round(getResources().getDisplayMetrics().density * dps);
    }

    protected boolean isTransformedTouchPointInView(float x, float y, View child) {
        RectF rect = calcViewScreenLocation(child);
        return rect.contains(x, y);
    }

    /**
     * 计算指定的 View 在屏幕中的坐标。
     */
    public RectF calcViewScreenLocation(View view) {
        int[] location = getTempPoint();
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }


    private int[] getTempPoint() {
        if (mTempPoint == null) {
            mTempPoint = new int[2];
        }
        return mTempPoint;
    }

    private boolean canViewReceivePointerEvents(float x, float y, @NonNull View child) {
        return (child.isShown() || child.getAnimation() != null) && isTransformedTouchPointInView(x, y, child);
    }
}
