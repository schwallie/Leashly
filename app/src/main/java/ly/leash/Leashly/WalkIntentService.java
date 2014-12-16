package ly.leash.Leashly;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Created by schwallie on 12/13/2014.
 */
public class WalkIntentService extends IntentService {
        public static final int NOTIFICATION_ID = 1;
        private NotificationManager mNotificationManager;
        NotificationCompat.Builder builder;
        public WalkIntentService() {
            super("WalkIntentService");
        }
        @Override
        protected void onHandleIntent(Intent intent) {
            Bundle extras = intent.getExtras();
            // Do the work that requires your app to keep the CPU running.
            // ...
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            WakefulWalkService.completeWakefulIntent(intent);
        }
    }

