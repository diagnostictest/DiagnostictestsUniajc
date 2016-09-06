package com.tecnologiajo.diagnostictestsuniajc;


import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by ADMIN on 16/08/2016.
 */
public class GCMInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
