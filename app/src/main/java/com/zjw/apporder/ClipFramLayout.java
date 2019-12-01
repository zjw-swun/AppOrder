package com.zjw.apporder;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import static com.zjw.apporder.ClipFramLayout.Direction.LEFT_TO_RIGHT;

/**
 * Desc :
 * date : 2019/12/1 21:42
 *
 * @author : zhoujiawei
 */
public class ClipFramLayout extends FrameLayout {
    private Direction mDirection = LEFT_TO_RIGHT;
    private float mCurrentProgress = 0.0f;
    private Rect rect = new Rect();

    public enum Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LIFT
    }

    public ClipFramLayout(@NonNull Context context) {
        super(context);
    }

    public ClipFramLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipFramLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDirection(Direction direction) {
        this.mDirection = direction;
    }

    public void setCurrentProgress(float currentProgress) {
        this.mCurrentProgress = currentProgress;
        if (mDirection == LEFT_TO_RIGHT) {
            rect.set(0, 0, (int) (getWidth() * mCurrentProgress), getHeight());
        } else {
            rect.set((int) (getWidth() * (1 - mCurrentProgress)), 0, getWidth(), getHeight());
        }
        setClipBounds(rect);
    }
}
