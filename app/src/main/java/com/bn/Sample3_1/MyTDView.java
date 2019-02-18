package com.bn.Sample3_1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class MyTDView extends GLSurfaceView {
    final float ANGLE_SPAN = 0.375f;

    RotateThread rthread;
    SceneRenderer mRenderer;

    public MyTDView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3);
        mRenderer = new SceneRenderer();
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private class SceneRenderer implements Renderer {
        Triangle tle;

        public void onDrawFrame(GL10 gl) {
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            tle.drawSelf();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {

            GLES30.glViewport(0, 0, width, height);

            float ratio = (float) width / height;
            Matrix.frustumM(Triangle.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            Matrix.setLookAtM(Triangle.mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES30.glClearColor(0, 0, 0, 1.0f);

            tle = new Triangle(MyTDView.this);
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            rthread = new RotateThread();
            rthread.start();
        }
    }

    public class RotateThread extends Thread {
        public boolean flag = true;

        @Override
        public void run() {
            while (flag) {
                mRenderer.tle.xAngle = mRenderer.tle.xAngle + ANGLE_SPAN;
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}