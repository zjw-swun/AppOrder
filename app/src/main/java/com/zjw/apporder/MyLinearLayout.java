package com.zjw.apporder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import static android.support.v4.widget.ViewDragHelper.EDGE_LEFT;

@SuppressLint("AppCompatCustomView")
public class MyLinearLayout extends LinearLayout {
    private ViewDragHelper mDragHelper;
    private int offsetRange;
    private View mGridLayout;
    private View mDragContetView;

    public MyLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mDragHelper = ViewDragHelper.create(this, mCallBack);
        mDragHelper.setEdgeTrackingEnabled(EDGE_LEFT);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragContetView = getChildAt(0);
        mGridLayout = mDragContetView.findViewById(R.id.gridLayout);
    }

    public int getOffsetRange() {
        return offsetRange;
    }

    public void setOffsetRange(int offsetRange) {
        this.offsetRange = offsetRange;
    }

    public void setOffsetRangeWithRequestLayout(int offsetRange) {
        this.offsetRange = offsetRange;
        requestLayout();
    }

    private Callback mCallBack = new Callback() {

        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (mDragContetView == null) {
                return;
            }
            int finalLeft;
            if (mDragContetView.getLeft() < offsetRange / 2.0f) {
                finalLeft = getPaddingLeft();
            } else {
                finalLeft = offsetRange;
            }
            mDragHelper.settleCapturedViewAt(finalLeft, releasedChild.getTop());
            invalidate();
        }


        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        }


        public int getViewHorizontalDragRange(@NonNull View child) {
            return offsetRange;
        }


        public int getViewVerticalDragRange(@NonNull View child) {
            return 0;
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }


        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (left < getPaddingLeft()) {
                left = getPaddingLeft();
            }
            if (left > offsetRange) {
                left = offsetRange;
            }
            return left;
        }


        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return 0;
        }
    };


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mDragContetView.layout(offsetRange, getPaddingTop(), offsetRange + getMeasuredWidth(), getPaddingTop() + mDragContetView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        offsetRange = mGridLayout.getMeasuredWidth() + getPaddingLeft();
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
