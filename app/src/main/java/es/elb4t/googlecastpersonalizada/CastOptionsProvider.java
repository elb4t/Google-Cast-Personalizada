package es.elb4t.googlecastpersonalizada;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.List;

/**
 * Created by eloy on 2/7/17.
 */

public class CastOptionsProvider implements OptionsProvider {
    @Override
    public CastOptions getCastOptions(Context appContext) {
        CastOptions castOptions = new CastOptions.Builder()
                .setReceiverApplicationId("17E6BAA8")
                .build();
        return castOptions;
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}
