package com.uc.drawing;

import android.graphics.Matrix;
import android.graphics.PointF;

public class MatrixUtil {
    public static PointF[] mapPoints(Matrix matrix, PointF[] points){
        float[] pts=new float[points.length*2];
        int index=0;
        for(PointF pt:points){
            pts[index]=pt.x;
            pts[index+1]=pt.y;
            index+=2;
        }
        matrix.mapPoints(pts);
        PointF[] ret=new PointF[points.length];
        index=0;
        for(int i=0; i< ret.length; i++){
            ret[i]=new PointF(pts[index], pts[index+1]);
            index+=2;
        }
        return ret;
    }
}
