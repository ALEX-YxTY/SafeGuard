package com.exercise.yxty.safeguard.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/25.
 */
public class LostFoundService extends Service {

    final int COMMAND_ALARM = 0;
    final int COMMAND_LOCATION = 1;
    final int COMMAND_WIPE_DATA = 2;
    final int COMMAND_LOCK_SCREEN = 3;

    MediaPlayer mp;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = intent.getIntExtra("command", -1);
        switch (command) {
            case COMMAND_ALARM:
                playmusic();
                break;
            case COMMAND_LOCATION:
                getLocation();
                break;
            case COMMAND_WIPE_DATA:
                wipeData();
                break;
            case COMMAND_LOCK_SCREEN:
                lockScreen();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void playmusic() {
        mp = MediaPlayer.create(this, R.raw.ylzs);
        mp.setVolume(1f, 1f);
        mp.setLooping(true);
        mp.start();
    }

    protected void getLocation() {
//        Criteria criteria = new Criteria();
//        criteria.setCostAllowed(true);
//        criteria.setAccuracy();
//        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        String provider = lm.getBestProvider()
//        lm.requestLocationUpdates();
//        stopSelf();
    }

    protected void wipeData() {

    }

    protected void lockScreen() {

    }

    @Override
    public void onDestroy() {
        mp.release();
        super.onDestroy();
    }
}
