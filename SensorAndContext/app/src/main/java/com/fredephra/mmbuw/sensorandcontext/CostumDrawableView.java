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

import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by Ephra on 19.05.2015.
 */
public class CostumDrawableView extends View{

    private static final String TAG = "DRAW" ;

    public CostumDrawableView(Context context) {
        super(context);
    }
    public CostumDrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i(TAG, canvas.getClipBounds().toString());
        int h = canvas.getHeight();
        int w = canvas.getWidth();
        Log.i(TAG, "Height" + h + "- Width" + w);

        drawBox(canvas);
        drawLine(canvas, );
        Vector< 3> d;
    }

    // draw functions
    private void drawBox(Canvas canvas){

        Rect myRect = new Rect();
        myRect.set(0,0,500,100);
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);

        canvas.drawRect(myRect, blue);
    }

    private void drawLine(Canvas canvas, LinkedList<float[]> point_queue){
        Path p = new Path();

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        p.moveTo(20, 20);
        p.lineTo(100, 200);
        p.lineTo(200, 100);
        p.lineTo(240, 155);
        p.lineTo(250, 175);
        p.lineTo(20, 20);
        canvas.drawPath(p, paint);
    }
}
