package com.android.cellular;

import android.annotation.SuppressLint;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class HepUstte extends Service {
    HUDView mView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getBaseContext(),"onCreate", Toast.LENGTH_LONG).show();

        final Bitmap kangoo = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher_background);


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                kangoo.getWidth(),
                kangoo.getHeight(),
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);



        params.gravity = Gravity.LEFT | Gravity.BOTTOM;
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);



        mView = new HUDView(this,kangoo);

        mView.setOnTouchListener(new OnTouchListener() {


            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(arg1.getX()<kangoo.getWidth() & arg1.getY()>0)
                {
                    Log.d("tıklandı", "touch me");
                }
                return false;
            }
        });


        wm.addView(mView, params);



    }



    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}

