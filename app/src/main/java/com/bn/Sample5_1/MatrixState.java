package com.bn.Sample5_1;

import android.opengl.Matrix;

//存储系统矩阵状态的类
public class MatrixState 
{
	private static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
    private static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵
    private static float[] mMVPMatrix;//最终的总变换矩阵
    
    //设置摄像机的方法
    public static void setCamera
    (
    		float cx,	
    		float cy,   
    		float cz,  
    		float tx,   
    		float ty,   
    		float tz,  
    		float upx,  
    		float upy,  
    		float upz   	
    )
    {
    	Matrix.setLookAtM
        (
        		mVMatrix, 	//存储生成矩阵元素的float[]类型数组
        		0, 			//填充起始偏移量
        		cx,cy,cz,	//摄像机位置的X、Y、Z坐标
        		tx,ty,tz,	//观察目标点X、Y、Z坐标
        		upx,upy,upz	//up向量在X、Y、Z轴上的分量
        );
    }
    
    //设置正交投影的方法
    public static void setProjectOrtho
    (
    	float left,		
    	float right,    
    	float bottom,  
    	float top,      
    	float near,		
    	float far       
    )
    {    	
    	Matrix.orthoM
    	(
    			mProjMatrix, 	//存储生成矩阵元素的float[]类型数组
    			0, 				//填充起始偏移量
    			left, right,	//near面的left、right 
    			bottom, top, 	//near面的bottom、top
    			near, far		//near面、far面与视点的距离
    	);
    }   
   
    //获取具体物体的总变换矩阵
    public static float[] getFinalMatrix(float[] spec)//生成物体总变换矩阵的方法  
    {
    	mMVPMatrix=new float[16];//创建用来存放最终变换矩阵的数组
    	Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0); //将摄像机矩阵乘以变换矩阵
    	//将投影矩阵乘以上一步的结果矩阵得到最终变换矩阵
    	Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }
}
