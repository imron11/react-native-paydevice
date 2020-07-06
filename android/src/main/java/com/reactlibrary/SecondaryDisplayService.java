package com.reactlibrary;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import android.util.Log;

import com.reactlibrary.presentation.LogoPresentation;

public class SecondaryDisplayService extends Service {

    private static final String TAG = "SecondaryDisplayService";

    public static final int TYPE_LOGO = 1;

    CustomerListener mCustomerListener = null;

    private boolean mLogoPlaying = false;

    private MediaRouter mMediaRouter;

    private LogoPresentation mLogoPresentation;

    private final MsgBinder mBinder = new MsgBinder();

    public class MsgBinder extends Binder {
        public SecondaryDisplayService getService() {
            return SecondaryDisplayService.this;
        }
    }

    @Override
    public void onCreate() {
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d(TAG, "Received start id " + startId + ": " + intent.toString());
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public void play(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(SecondaryDisplayService.this, "no permit drawing over other apps", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        stop();
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        if (route != null) {
            Display secondaryDisplay = route.getPresentationDisplay();
            if (secondaryDisplay != null) {
                //limit extend screen size
                int secondaryScreenWidth = secondaryDisplay.getWidth();
                int secondaryScreenHeight = secondaryDisplay.getHeight();

                switch (type) {
                    case TYPE_LOGO:
                        Log.d(TAG, "Star play text!");
                        if (mLogoPresentation == null) {
                            mLogoPresentation = new LogoPresentation(getApplicationContext(), secondaryDisplay, R.style.SecondaryDisplay);
                            mLogoPresentation.getWindow().setLayout(secondaryScreenWidth,secondaryScreenHeight);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                mLogoPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
                            }
                        }
                        mLogoPresentation.show();
                        mLogoPlaying = true;
                        String amount = "100";
                        String change = "10";
                        mLogoPresentation.setData(amount, change);
                        break;
                }
            } else {
                Log.d(TAG, "Have not secondary display!");
                Toast.makeText(SecondaryDisplayService.this, "Secondary Display not Found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void stop() {
        if(mLogoPresentation != null){
            Log.d(TAG, "Stop play logo!");
            mLogoPresentation.dismiss();
            mLogoPlaying = false;
            mLogoPresentation = null;
        }
    }

    public boolean isLogoPlaying() { return mLogoPlaying; }

    public void setCustomerListener(CustomerListener l) {
        this.mCustomerListener = l;
    }

    public interface CustomerListener {
        void onConfirm(String input);
    }
}
