package testkotliin.pdfreader.pdfeditor.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import testkotliin.pdfreader.pdfeditor.R;

@SuppressLint("CustomSplashScreen")
public class ActivityLaunchScreen extends AppCompatActivity implements OnSuccessListener<AppUpdateInfo> {
    public static final int REQUEST_CODE = 1234;
    public final int RC_APP_UPDATE = 100;
    public AppUpdateManager appUpdateManager;
    public boolean mNeedsFlexibleUpdate;
    public boolean UpdateAvailable = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        setStatusBar();

        appUpdateManager = AppUpdateManagerFactory.create(ActivityLaunchScreen.this);
        mNeedsFlexibleUpdate = false;



        new Handler(Looper.myLooper()).postDelayed(() -> {
            startActivity(new Intent(ActivityLaunchScreen.this, ActivityMain.class));
            finish();
        }, 4000);


    }

    @Override
    public void onSuccess(AppUpdateInfo appUpdateInfo) {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            startUpdate(appUpdateInfo);
        } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackBarForCompleteUpdate();
        } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdate(appUpdateInfo);
            } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                mNeedsFlexibleUpdate = true;
                showFlexibleUpdateNotification();
            }
        }
    }    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                if (appUpdateManager != null) {
                    appUpdateManager.unregisterListener(installStateUpdatedListener);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(installStateUpdatedListener);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                UpdateAvailable = true;
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, ActivityLaunchScreen.this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else {
                UpdateAvailable = false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    private void startUpdate(final AppUpdateInfo appUpdateInfo) {
        final Activity activity = this;
        new Thread(() -> {
            try {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, activity, REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void popupSnackBarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.rl_splash), "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INSTALL", view -> {
            if (appUpdateManager != null) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.appColor));
        snackbar.show();
    }

    private void showFlexibleUpdateNotification() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.rl_splash), "An update is available and accessible in More.", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                UpdateAvailable = false;
            }
        }
    }

    public void setStatusBar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 23) {
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        } else if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        } else {
            window.clearFlags(0);
        }
    }


    private void Initialize() {
/*
        ADSinit(ActivityLaunchScreen.this, getCurrentVersionCode(), new getDataListner() {
            @Override
            public void onSuccess() {
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    startActivity(new Intent(ActivityLaunchScreen.this, ActivityMain.class));
                    finish();
                }, 4000);
            }

            @Override
            public void onUpdate(String url) {
                showUpdateDialog(url);
            }

            @Override
            public void onRedirect(String url) {
                showRedirectDialog(url);
            }

            @Override
            public void onReload() {
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    startActivity(new Intent(ActivityLaunchScreen.this, ActivityMain.class));
                    finish();
                }, 4000);
            }

            @Override
            public void onGetExtradata(JSONObject extraData) {

            }
        });
*/
    }

    public void showRedirectDialog(final String url) {

        final Dialog dialog = new Dialog(ActivityLaunchScreen.this);
        dialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.installnewappdialog, null);
        dialog.setContentView(view);
        TextView update = view.findViewById(R.id.update);
        TextView txt_title = view.findViewById(R.id.txt_title);
        TextView txt_decription = view.findViewById(R.id.txt_decription);

        update.setText("Install Now");
        txt_title.setText("Install our new app now and enjoy");
        txt_decription.setText("We have transferred our server, so install our new app by clicking the button below to enjoy the new features of app.");


        update.setOnClickListener(view1 -> {
            try {
                Uri marketUri = Uri.parse(url);
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (ActivityNotFoundException ignored1) {
            }
        });

        dialog.create();

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void showUpdateDialog(final String url) {
        final Dialog dialog = new Dialog(ActivityLaunchScreen.this);
        dialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.installnewappdialog, null);
        dialog.setContentView(view);
        TextView update = view.findViewById(R.id.update);
        TextView txt_title = view.findViewById(R.id.txt_title);
        TextView txt_decription = view.findViewById(R.id.txt_decription);

        update.setText("Update Now");
        txt_title.setText("Update our new app now and enjoy");
        txt_decription.setText("");
        txt_decription.setVisibility(View.GONE);

        update.setOnClickListener(view1 -> {
            try {
                Uri marketUri = Uri.parse(url);
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            } catch (ActivityNotFoundException ignored1) {
            }
        });

        dialog.create();

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public int getCurrentVersionCode() {
        PackageManager manager = getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

}

