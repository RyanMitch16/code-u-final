package com.example.grocerycodeu.grocerycloud.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GrocerySyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static GrocerySyncAdapter sGrocerySyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sGrocerySyncAdapter == null) {
                sGrocerySyncAdapter = new GrocerySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sGrocerySyncAdapter.getSyncAdapterBinder();
    }
}