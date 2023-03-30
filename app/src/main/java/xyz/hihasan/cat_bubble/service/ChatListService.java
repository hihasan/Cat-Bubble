package xyz.hihasan.cat_bubble.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import xyz.hihasan.cat_bubble.R;

public class ChatListService extends Service {
    private WindowManager mWindowManager;
    private View mChatHeadView;

    public ChatListService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    public void onCreate() {
        super.onCreate();
        mChatHeadView = LayoutInflater.from(ChatListService.this).inflate(R.layout.layout_app_shortend_list, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the chat head position
        params.gravity = Gravity.TOP | Gravity.RIGHT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 50;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

        closeAction();
        calenderAction();


    }

    private void calenderAction() {
        mChatHeadView.findViewById(R.id.calender_iv).setOnClickListener(v -> {
            PackageManager pm = getBaseContext().getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage("com.android.deskclock");
            getBaseContext().startActivity(launchIntent);

            action();

        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void closeAction() {
        final RelativeLayout chatHeadImage = mChatHeadView.findViewById(R.id.close_tv);
        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.

                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:

                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                            action();
                        }
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });
    }

    public void action() {
        Intent intent = new Intent(getBaseContext(), ChatHeadService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }
}
