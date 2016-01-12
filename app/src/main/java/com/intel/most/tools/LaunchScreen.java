package com.intel.most.tools;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

/**
 *  Unused but still keep it in case
 */
public class LaunchScreen extends Activity {
    private boolean mBound;
    private ShellService mShellService;
    private TextView mTextView;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ShellService.LocalBinder binder = (ShellService.LocalBinder)iBinder;
            mShellService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        mTextView = (TextView)findViewById(R.id.progress);
        AssetManager assetManager = getAssets();
        try {
            String[] allFiles = assetManager.list("");
            for (String file : allFiles) {
                Log.e("yangjun11", file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ShellService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        // new BackgroundTask().execute();
    }

    @Override
    protected void onDestroy() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onDestroy();
    }

    private class BackgroundTask extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Long doInBackground(String... strings) {
            // install blktrace blkparse most filter.sh
            Log.e("yangjun", "install blktrace...");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // publishProgress() will invoke onProgressUpdate
        }

        @Override
        protected void onPostExecute(Long aLong) {
            // finish this activity and launch MainActivity
            Intent intent = new Intent(LaunchScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
