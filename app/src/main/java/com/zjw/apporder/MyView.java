package com.zjw.apporder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Desc :
 * date : 2018/6/11 下午2:21
 *
 * @author : jiawei
 */
public class MyView extends AppCompatImageView implements SensorEventListener {
    private Paint paint;
    private Path path;
    private SensorManager mSensorManager;
    private Sensor gSensor;
    private Sensor mSensor;
    private Sensor oSensor;
    private Sensor rSensor;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] mOrientation = new float[3];
    private float[] mLastOrientation = new float[3];

    public MyView(Context context) {
        super(context);
        init(context);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        oSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        rSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

       /* //设置Paint
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
        path.rLineTo(400, 600);*/

    }

    @Override
    protected void onAttachedToWindow() {
        //mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       // canvas.drawPath(path, paint);
    }

    private int dp2px(int dps){
       return Math.round(getResources().getDisplayMetrics().density * dps);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                lowPass(event.values.clone(), mGravity);
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                lowPass(event.values.clone(), mGeomagnetic);
            }
            float R[] = new float[9];
            float I[] = new float[9];
            if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // mOrientation[0] 记录着手机围绕** Z 轴**的旋转弧度。
                // mOrientation[1] 记录着手机围绕** X 轴**的旋转弧度。 头尾上下摆动   上负下正
                // mOrientation[2] 记录着手机围绕 Y 轴的旋转弧度。  左右倾斜  左负右正
                mOrientation[0] = (float)Math.toDegrees(orientation[0]);
                mOrientation[1] = (float)Math.toDegrees(orientation[1]);
                mOrientation[2] = (float)Math.toDegrees(orientation[2]);

                /*if (mOrientation[1] != mLastOrientation[1]) {
                    offsetTopAndBottom((int) ((mOrientation[1] - mLastOrientation[1]) * 20));
                }
                */
                if (mOrientation[2] != mLastOrientation[2]) {
                    offsetLeftAndRight((int) ((mOrientation[2] - mLastOrientation[2]) * 10));
                }
                Log.e("mOrientationx","x is "+mOrientation[0]);
                Log.e("mOrientationy","y is "+mOrientation[1]);
                Log.e("mOrientationz","z is "+mOrientation[2]);

                mLastOrientation = mOrientation.clone();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float[] lowPass(float[] input, float[] output) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + 0.03f * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (gSensor != null) mSensorManager.unregisterListener(this, gSensor);
        if (mSensor != null) mSensorManager.unregisterListener(this, mSensor);
        if (oSensor != null) mSensorManager.unregisterListener(this, oSensor);
        if (rSensor != null) mSensorManager.unregisterListener(this, rSensor);
        super.onDetachedFromWindow();
    }
}
