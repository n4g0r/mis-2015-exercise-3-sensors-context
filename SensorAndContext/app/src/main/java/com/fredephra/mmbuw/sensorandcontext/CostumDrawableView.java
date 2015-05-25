package com.fredephra.mmbuw.sensorandcontext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;




import org.w3c.dom.Attr;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


public class CostumDrawableView extends View{

    private static final String TAG = "DRAW" ;
    private Canvas myCanvas= new Canvas();


    public CostumDrawableView(Context context) {
        super(context);
    }
    public CostumDrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private LinkedList<float[]> point_queue;
    private double[] fftdata=new double[0];
    private static final int width= 1080;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBox(canvas);
        drawLines(canvas);
    }

    // draw functions
    private void drawBox(Canvas canvas){
        //System.out.println("drawbox: "+Arrays.toString(fftdata));
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.FILL);
        int bot=1350;
        for(int i = 0; i < fftdata.length; i++) {
            Rect myRect = new Rect();

            int top=bot-(int)fftdata[i];
            int left=i*(width/fftdata.length);
            int right=(i+1)*(width/fftdata.length);
            myRect.set(left, top, right, bot);


            canvas.drawRect(myRect, blue);
        }
    }

    private void drawLines(Canvas canvas){
        Path grey = new Path();
        Path red = new Path();
        Path green = new Path();
        Path blue = new Path();
        Path black = new Path();
        grey.moveTo(0, 200);
        grey.lineTo(width, 200);
        grey.moveTo(0, 400);
        grey.lineTo(width, 400);
        grey.moveTo(0, 700);
        grey.lineTo(width, 700);
        grey.moveTo(0, 1200);
        grey.lineTo(width, 1200);

        red.moveTo(point_queue.get(0)[0],300);
        green.moveTo(point_queue.get(0)[1], 600);
        blue.moveTo(point_queue.get(0)[2], 900);
        black.moveTo(point_queue.get(0)[3], 1150);
        for (int i = 0; i < point_queue.size();i++){
            float[] point = point_queue.get(i);
            red.lineTo(i+2, point[0] * -10 + 300);
            green.lineTo(i+2, point[1] * -10 + 600);
            blue.lineTo(i+2, point[2] * -10 + 900);
            black.lineTo(i+2, point[3] * -10 + 1200);
        }

        Paint color = new Paint();
        color.setStyle(Paint.Style.STROKE);
        color.setColor(Color.BLACK);
        canvas.drawPath(grey,color);
        color.setColor(Color.RED);
        canvas.drawPath(red, color);
        color.setColor(Color.GREEN);
        canvas.drawPath(green, color);
        color.setColor(Color.BLUE);
        canvas.drawPath(blue, color);
        color.setColor(Color.BLACK);
        canvas.drawPath(black, color);
        myCanvas = canvas;
    }

    public void setQueue(LinkedList<float[]> queue){
        point_queue = queue;
        invalidate();
    }

    public void setFFT(double[] fftresult){
        fftdata = fftresult;
        //invalidate();
    }

    public Canvas getMyCanvas(){
        return myCanvas;
    }
}
