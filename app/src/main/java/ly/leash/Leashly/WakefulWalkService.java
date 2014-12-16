package ly.leash.Leashly;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by schwallie on 12/13/2014.
 */
public class WakefulWalkService extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
        Intent service = new Intent(context, WakefulWalkService.class);
        startWakefulService(context, service);
    }
}

