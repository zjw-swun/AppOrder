package com.zjw.apporder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Desc :
 * date : 2018/6/5 下午12:48
 *
 * @author : jiawei
 */
public class MyDrawable extends Drawable {
    public static final int ALL = 0x1111;

    public static final int LEFT = 0x0001;

    public static final int TOP = 0x0010;

    public static final int RIGHT = 0x0100;

    public static final int BOTTOM = 0x1000;

    public static final int SHAPE_RECTANGLE = 0x0001;

    public static final int SHAPE_OVAL = 0x0010;

    /**
     * 阴影的颜色
     */
    private int mShadowColor = Color.TRANSPARENT;

    /**
     * 阴影的大小范围
     */
    private float mShadowRadius = 0;

    /**
     * 阴影 x 轴的偏移量
     */
    private float mShadowDx = 0;

    /**
     * 阴影 y 轴的偏移量
     */
    private float mShadowDy = 0;

    /**
     * 阴影显示的边界
     */
    private int mShadowSide = ALL;

    /**
     * 阴影的形状，圆形/矩形
     */
    private int mShadowShape = SHAPE_RECTANGLE;

    private final Paint mPaint;

    private RectF mRectF;

    private final Context mContext;
    private boolean isSoftwareLayerType;
    private boolean isShadowBounds;
    private float mEffect;

    public MyDrawable(Context context) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
        setUpShadowPaint();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        setUpShadowPaint();
        if (mShadowShape == SHAPE_RECTANGLE) {
            canvas.drawRect(mRectF, mPaint);
        } else if (mShadowShape == SHAPE_OVAL) {
            canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), Math.min(mRectF.width(), mRectF.height()) / 2, mPaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    /**
     * 在view初始化并且绑定drawable之后调用 (首次调用在onMeasure之前，但是会存在多次调用的情况)
     *
     * @return
     */
    @Override
    public int getOpacity() {
        setCallBackViewOnLayout();
        return mPaint.getAlpha() < 255 ? PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }


    private void setShadowBounds(View view) {
        mEffect = mShadowRadius + dip2px(5);
        float rectLeft = 0F;
        float rectTop = 0F;
        float rectRight = view.getMeasuredWidth();
        float rectBottom = view.getMeasuredHeight();
        int paddingLeft = 0;
        int paddingTop = 0;
        int paddingRight = 0;
        int paddingBottom = 0;
        if ((mShadowSide & LEFT) == LEFT) {
            rectLeft = mEffect;
            paddingLeft = (int) mEffect;
        }
        if ((mShadowSide & TOP) == TOP) {
            rectTop = mEffect;
            paddingTop = (int) mEffect;
        }
        if ((mShadowSide & RIGHT) == RIGHT) {
            rectRight = view.getMeasuredWidth() - mEffect;
            paddingRight = (int) mEffect;
        }
        if ((mShadowSide & BOTTOM) == BOTTOM) {
            rectBottom = view.getMeasuredHeight() - mEffect;
            paddingBottom = (int) mEffect;
        }
        if (mShadowDy != 0.0f) {
            rectBottom = rectBottom - mShadowDy;
            paddingBottom = paddingBottom + (int) mShadowDy;
        }
        if (mShadowDx != 0.0f) {
            rectRight = rectRight - mShadowDx;
            paddingRight = paddingRight + (int) mShadowDx;
        }
        mRectF.left = rectLeft;
        mRectF.top = rectTop;
        mRectF.right = rectRight;
        mRectF.bottom = rectBottom;
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void setUpShadowPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
    }

    private void setCallBackViewOnLayout() {
        final Callback callback = getCallback();
        //目的是在该view onMeasure之前 setPadding ?
        if (!isShadowBounds && callback instanceof View) {
            final View view = (View) callback;
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    setCallBackViewLayoutType();
                    setShadowBounds(view);
                }
            });
        }
        isShadowBounds = true;
    }


    private void setCallBackViewLayoutType() {
        final Callback callback = getCallback();
        if (!isSoftwareLayerType && callback instanceof View) {
            // 关闭硬件加速
            ((View) callback).setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            isSoftwareLayerType = true;
        }
    }

    public int getShadowColor() {
        return mShadowColor;
    }


    public float getShadowRadius() {
        return mShadowRadius;
    }


    public float getShadowDx() {
        return mShadowDx;
    }


    public float getShadowDy() {
        return mShadowDy;
    }


    public int getShadowSide() {
        return mShadowSide;
    }


    public int getShadowShape() {
        return mShadowShape;
    }

    public MyDrawable setShadowColor(int shadowColor) {
        mShadowColor = shadowColor;
        return this;
    }

    public MyDrawable setShadowDx(float shadowDx) {
        mShadowDx = shadowDx;
        return this;
    }

    public MyDrawable setShadowRadius(float shadowRadius) {
        mShadowRadius = shadowRadius;
        return this;
    }

    public MyDrawable setShadowDy(float shadowDy) {
        mShadowDy = shadowDy;
        return this;
    }

    public MyDrawable setShadowSide(int shadowSide) {
        mShadowSide = shadowSide;
        return this;
    }

    public MyDrawable setShadowShape(int shadowShape) {
        mShadowShape = shadowShape;
        return this;
    }


    /**
     * dip2px dp 值转 px 值
     *
     * @param dpValue dp 值
     * @return px 值
     */
    private float dip2px(float dpValue) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        float scale = dm.density;
        return (dpValue * scale + 0.5F);
    }
}
