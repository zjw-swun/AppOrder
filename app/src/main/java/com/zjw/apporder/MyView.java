package com.zjw.apporder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Desc :
 * date : 2018/6/11 下午2:21
 *
 * @author : jiawei
 */
public class MyView extends View {
    private Paint paint;
    private Path path;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //设置Paint
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        //设置Path
        path = new Path();
         //屏幕左上角（0,0）到（200,400）画一条直线
        path.lineTo(dp2px(200), dp2px(200));
        //(200, 400)到（400,600）画一条直线
        path.lineTo(400, 600);
        //以（400,600）为起始点（0,0）偏移量为（400,600）画一条直线，
        //其终点坐标实际在屏幕的位置为（800,1200）
        path.rLineTo(400, 600);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    private int dp2px(int dps){
       return Math.round(getResources().getDisplayMetrics().density * dps);
    }
}
