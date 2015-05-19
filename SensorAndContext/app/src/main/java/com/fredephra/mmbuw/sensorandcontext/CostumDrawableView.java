package com.fredephra.mmbuw.sensorandcontext;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import org.w3c.dom.Attr;

/**
 * Created by Ephra on 19.05.2015.
 */
public class CostumDrawableView extends View{

    private static final String TAG = "DRAW" ;

    public CostumDrawableView(Context context) {
        super(context);
        int padding = 10;
        int x = 500;
        int y = 10;
        int width = 80;
        int height = 1500;
        System.out.println("Hello fuckers");

    }
    public CostumDrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("drawing");

        Log.i(TAG, canvas.getClipBounds().toString());
        int h = canvas.getHeight();
        int w = canvas.getWidth();
        Log.i(TAG, "Height" + h + "Width" + w);

        Rect myRect = new Rect();
        myRect.set(0,0,500,100);
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);

        canvas.drawRect(myRect, blue);

    }
}
