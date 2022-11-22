package testkotliin.pdfreader.pdfeditor.Ads;

import android.app.Application;
import android.content.Context;
import android.os.Process;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    public static boolean isFromGooglePlay = true;
    public static MyApplication application;

    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        boolean z = true;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        checkAppReplacingState();
        String installerPackageName = getPackageManager().getInstallerPackageName(getPackageName());
        if (installerPackageName == null || !installerPackageName.equals("com.android.vending")) {
            z = false;
        }
        isFromGooglePlay = z;

    }
    public MyApplication() {
        application = this;
    }

    public static synchronized MyApplication getInstance() {
        return application;
    }


    public static MyApplication getApp() {
        if (application == null) {
            application = new MyApplication();
        }
        return application;
    }

    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    private void checkAppReplacingState() {
        if (getResources() == null) {
            Process.killProcess(Process.myPid());
        }
    }
}
