package udacity.hugo.myappportfolio.util;

import android.app.ActivityManager;
import android.content.Context;

import udacity.hugo.myappportfolio.R;

/**
 * Created by hugo on 6/29/15.
 */
public class Utils {
    public static boolean isServiceRunning(Class<?> serviceClass,
                                           Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        boolean isServiceRunning = false;
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        return isServiceRunning;
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.is_tablet);
    }
}
