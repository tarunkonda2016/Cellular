package com.android.cellular;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.ViewGroup;

@SuppressLint("DrawAllocation")
public class HUDView extends ViewGroup {


    Bitmap kangoo;

    public HUDView(Context context, Bitmap kangoo) {
        super(context);

        this.kangoo=kangoo;



    }


    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);


        // delete below line if you want transparent back color, but to understand the sizes use back color
        canvas.drawColor(Color.BLACK);

        canvas.drawBitmap(kangoo,0 , 0, null);


        //canvas.drawText("Hello World", 5, 15, mLoadPaint);

    }


    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        // Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();

        return true;
    }
}
