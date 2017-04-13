package net.sky.scweather.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class AuthenticatorService extends Service {

    private DummyAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new DummyAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }

}
