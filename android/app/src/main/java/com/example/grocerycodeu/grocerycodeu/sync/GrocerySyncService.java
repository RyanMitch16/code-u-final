package com.example.grocerycodeu.grocerycodeu.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GrocerySyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static GrocerySyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("TestXXX", "onCreate - GroceySyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new GrocerySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}