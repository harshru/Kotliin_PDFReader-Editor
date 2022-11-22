package testkotliin.pdfreader.pdfeditor.Activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.itextpdf.text.html.HtmlTags;
import com.pesonal.adsdk.AppManage;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import testkotliin.pdfreader.pdfeditor.Adapter.AdapterDevicePdfs;
import testkotliin.pdfreader.pdfeditor.Adapter.AdapterRecentPdfs;
import testkotliin.pdfreader.pdfeditor.Fragments.FragmentDevicePdf;
import testkotliin.pdfreader.pdfeditor.Fragments.FragmentPDFTools;
import testkotliin.pdfreader.pdfeditor.Fragments.FragmentRecentPdf;
import testkotliin.pdfreader.pdfeditor.Fragments.FragmentSettings;
import testkotliin.pdfreader.pdfeditor.Fragments.FragmentStarredPDF;
import testkotliin.pdfreader.pdfeditor.GetSet.PdfDataType;
import testkotliin.pdfreader.pdfeditor.R;
import testkotliin.pdfreader.pdfeditor.customview.MaterialSearchView;
import testkotliin.pdfreader.pdfeditor.data.DbHelper;
import testkotliin.pdfreader.pdfeditor.utils.DataUpdatedEvent;
import testkotliin.pdfreader.pdfeditor.utils.LocaleUtils;
import testkotliin.pdfreader.pdfeditor.utils.Utils;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentRecentPdf.OnRecentPdfClickListener, AdapterDevicePdfs.OnPdfClickListener, AdapterRecentPdfs.OnHistoryPdfClickListener, MaterialSearchView.OnQueryTextListener, View.OnClickListener {
    public static final String PDF_LOCATION = "com.example.pdfreader.PDF_LOCATION";
    public static final String SHOW_REMOVE_ADS = "com.example.pdfreader.SHOW_REMOVE_ADS";
    public static String GRID_VIEW_ENABLED = "prefs_grid_view_enabled";
    public static String GRID_VIEW_NUM_OF_COLUMNS = "prefs_grid_view_num_of_columns";
    private final String TAG = ActivityMain.class.getSimpleName();
    public ViewPager pagerBrowsePdf;
    public int pdfClick = 0;
    String currLanguage;
    MenuItem menuGridNumberSize;
    MenuItem menulistOrGrid;
    Toolbar toolbarBrowsePdf;
    DrawerLayout drawerLayBrowsePdf;
    boolean gridViewEnabled;
    Menu menu;
    SubMenu menuPdfSortList;
    MaterialSearchView searchBrowsePdf;
    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigation;
    ImageView imgHome, imgRecent, imgFavourite, imgTools;


    @Override
    public boolean onQueryTextSubmit(String str) {
        return false;
    }

    @Override
    public void onRecentPdfClick(Uri uri) {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocaleUtils.setUpLanguage(this);
        setContentView(R.layout.activity_main);


        toolbarBrowsePdf = findViewById(R.id.toolbarBrowsePdf);

        drawerLayBrowsePdf = findViewById(R.id.drawerLayBrowsePdf);

        setSupportActionBar(toolbarBrowsePdf);

        AppManage.getInstance(ActivityMain.this).loadInterstitialAd(this, AppManage.ADMOB_I[0], AppManage.FACEBOOK_I[0]);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        gridViewEnabled = sharedPreferences.getBoolean(GRID_VIEW_ENABLED, false);
        currLanguage = sharedPreferences.getString(FragmentSettings.KEY_PREFS_LANGUAGE, "en");

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayBrowsePdf, toolbarBrowsePdf, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayBrowsePdf.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationSider);
        disableNavigationViewScrollbars(navigationView);
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.ll_tools).setOnClickListener(this);
        headerView.findViewById(R.id.ll_setting).setOnClickListener(this);
        headerView.findViewById(R.id.ll_share).setOnClickListener(this);
        headerView.findViewById(R.id.ll_rate).setOnClickListener(this);


        ((NavigationView) findViewById(R.id.navigationSider)).setNavigationItemSelectedListener(this);
        searchBrowsePdf = findViewById(R.id.searchBarPdf);
        searchBrowsePdf.setOnQueryTextListener(this);

        if (gridViewEnabled) {
            new Utils.BackgroundGenerateThumbnails(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        openFragment(new FragmentDevicePdf());

        imgHome = (ImageView) findViewById(R.id.imgHome);
        imgRecent = (ImageView) findViewById(R.id.imgRecent);
        imgFavourite = (ImageView) findViewById(R.id.imgFavourite);
        imgTools = (ImageView) findViewById(R.id.imgTools);


        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setItemIconTintList(null);

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            imgHome = (ImageView) findViewById(R.id.imgHome);
            imgRecent = (ImageView) findViewById(R.id.imgRecent);
            imgFavourite = (ImageView) findViewById(R.id.imgFavourite);
            imgTools = (ImageView) findViewById(R.id.imgTools);

//ECEAEC
            switch (item.getItemId()) {
                case R.id.nav_home:
                    imgHome.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_pdf));
                    imgRecent.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_recent_gray));
                    imgFavourite.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_fav_gray));
                    imgTools.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_tool_gray));
                    openFragment(new FragmentDevicePdf());
                    return true;
                case R.id.nav_recent:
                    imgHome.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_pdf_gray));
                    imgRecent.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_recent));
                    imgFavourite.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_fav_gray));
                    imgTools.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_tool_gray));
                    openFragment(new FragmentRecentPdf());
                    return true;
                case R.id.nav_favourite:
                    imgHome.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_pdf_gray));
                    imgRecent.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_recent_gray));
                    imgFavourite.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_fav));
                    imgTools.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_tool_gray));
                    openFragment(new FragmentStarredPDF());
                    return true;
                case R.id.nav_tools:
                    imgHome.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_pdf_gray));
                    imgRecent.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_recent_gray));
                    imgFavourite.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_fav_gray));
                    imgTools.setImageDrawable(ContextCompat.getDrawable(ActivityMain.this, R.drawable.ic_tab_tool));

                    openFragment(new FragmentPDFTools());
                    return true;
            }
            return false;
        });

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        drawerLayBrowsePdf.closeDrawer(GravityCompat.START);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Context applicationContext = getApplicationContext();
            switch (menuItem.getItemId()) {
                case R.id.menuRate:
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                    }
                    return;
                case R.id.menuSettings:
                    AppManage.getInstance(ActivityMain.this).showInterstitialAd(ActivityMain.this, () -> {
                        startActivity(new Intent(applicationContext, ActivitySettings.class));
                    }, "", AppManage.app_mainClickCntSwAd);
                    return;
                case R.id.menuShare:
                    Utils.startShareActivity(applicationContext);
                    return;
                case R.id.menuTools:
                    AppManage.getInstance(ActivityMain.this).showInterstitialAd(ActivityMain.this, () -> {
                        startActivity(new Intent(applicationContext, ActivityPDFTools.class));
                    }, "", AppManage.app_mainClickCntSwAd);
                    return;
                default:
            }
        }, 200);
        return true;
    }

    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu2) {
        menu = menu2;
        getMenuInflater().inflate(R.menu.main, menu2);
        menuPdfSortList = menu.findItem(R.id.menuPdfSortList).getSubMenu();
        menuPdfSortList.clearHeader();
        MenuCompat.setGroupDividerEnabled(menu2, true);
        menulistOrGrid = menu2.findItem(R.id.menulistOrGrid);
        menuGridNumberSize = menu2.findItem(R.id.menuGridNumberSize);
        if (menu2 instanceof MenuBuilder) {
            ((MenuBuilder) menu2).setOptionalIconsVisible(true);
        }
        if (gridViewEnabled) {
            menulistOrGrid.setVisible(false);
            menuGridNumberSize.setVisible(false);
        } else {
            menulistOrGrid.setVisible(false);
            menuGridNumberSize.setVisible(false);
        }
        return true;
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        NavigationMenuView navigationMenuView;
        if (navigationView != null && (navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0)) != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        Uri data;
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1 && (data = intent.getData()) != null) {
            String path = data.getPath();
            if (path.contains(":")) {
                path = path.split(":")[1];
            }
            openFilePdfViewer(new File(path).getAbsolutePath());
        }
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId != R.id.menuSearchPdf) {
            switch (itemId) {
                case R.id.menuClearRecentPdfs:
                    clearAllRecentPdf();
                    break;
                case R.id.menuFiveColumns:
                    setPdfInGridView(5);
                    break;
                case R.id.menuFourColumns:
                    setPdfInGridView(4);
                    break;
                default:
                    switch (itemId) {
                        case R.id.menuSixColumns:
                            setPdfInGridView(6);
                            break;
                        case R.id.menuSortByPdfModifiedDate:
                            pdfSortByModifiedDate();
                            break;
                        case R.id.menuSortByPdfName:
                            pdfSortByName();
                            break;
                        case R.id.menuSortByPdfSize:
                            pdfSortBySize();
                            break;
                        case R.id.menuSortPdfByAscending:
                            pdfSortByAscendingOrder();
                            break;
                        case R.id.menuSortPdfByDescending:
                            pdfSortByDescendingOrder();
                            break;
                        case R.id.menuThreeColumns:
                            setPdfInGridView(3);
                            break;
                        default:
                            switch (itemId) {
                                case R.id.menuTwoColumns:
                                    setPdfInGridView(2);
                                    break;
                                case R.id.menulistOrGrid:
                                    setPdfInListView();
                                    break;
                            }
                    }
            }
        } else {
            searchBrowsePdf.openPdfSearchData();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onPdfClicked(PdfDataType pdfDataType) {
        openFilePdfViewer(pdfDataType.getAbsolutePath());
    }

    @Override
    public void onHistoryPdfClicked(PdfDataType pdfDataType) {
        openFilePdfViewer(pdfDataType.getAbsolutePath());
    }

    public void openFilePdfViewer(String str) {
        pdfClick++;
        AppManage.getInstance(ActivityMain.this).showInterstitialAd(ActivityMain.this, () -> {
            Intent intent = new Intent(this, ActivityPDFViewer.class);
            intent.putExtra(PDF_LOCATION, str);
            StringBuilder sb = new StringBuilder();
            sb.append("PdfDataType location ");
            sb.append(str);
            startActivity(intent);
        }, "", AppManage.app_mainClickCntSwAd);
    }

    public void openBrowsePDFFiles(View view) {
        AppManage.getInstance(ActivityMain.this).showInterstitialAd(ActivityMain.this, () -> {
            startActivity(new Intent(this, ActivityFileBrowser.class));
        }, "", AppManage.app_mainClickCntSwAd);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeAppLanguage();
        if (pdfClick == 16) {
            pdfClick = 0;
        }
    }

    public void clearAllRecentPdf() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to clear recent files ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    dialog.dismiss();
                    DbHelper.getInstance(this).clearRecentPDFs();
                    Toast.makeText(this, R.string.recent_cleared, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setPdfInListView() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(GRID_VIEW_ENABLED, false);
        edit.apply();
        EventBus.getDefault().post(new DataUpdatedEvent.ToggleGridViewEvent());
        menulistOrGrid.setVisible(false);
        menuGridNumberSize.setVisible(true);
    }

    public void setPdfInGridView(int i) {
        new Utils.BackgroundGenerateThumbnails(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(GRID_VIEW_ENABLED, true);
        edit.putInt(GRID_VIEW_NUM_OF_COLUMNS, i);
        edit.apply();
        EventBus.getDefault().post(new DataUpdatedEvent.ToggleGridViewEvent());
        menulistOrGrid.setVisible(true);
        menuGridNumberSize.setVisible(false);
    }

    @Override
    public boolean onQueryTextChange(String str) {
        Log.d(TAG, str);
        return true;
    }

    public void pdfSortByName() {
        menuPdfSortList.findItem(R.id.menuSortByPdfName).setChecked(true);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(DbHelper.SORT_BY, "name");
        edit.apply();
        EventBus.getDefault().post(new DataUpdatedEvent.SortListEvent());
    }

    public void pdfSortByModifiedDate() {
        menuPdfSortList.findItem(R.id.menuSortByPdfModifiedDate).setChecked(true);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(DbHelper.SORT_BY, "date modified");
        edit.apply();
        EventBus.getDefault().post(new DataUpdatedEvent.SortListEvent());
    }

    public void pdfSortBySize() {
        menuPdfSortList.findItem(R.id.menuSortByPdfSize).setChecked(true);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(DbHelper.SORT_BY, HtmlTags.SIZE);
        edit.apply();
        EventBus.getDefault().post(new DataUpdatedEvent.SortListEvent());
    }

    public void pdfSortByAscendingOrder() {
        menuPdfSortList.findItem(R.id.menuSortPdfByAscending).setChecked(true);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(DbHelper.SORT_ORDER, "ascending");
        edit.apply();
        EventBus.getDefault().post(new DataUpdatedEvent.SortListEvent());
    }

    public void pdfSortByDescendingOrder() {
        menuPdfSortList.findItem(R.id.menuSortPdfByDescending).setChecked(true);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(DbHelper.SORT_ORDER, "descending");
        edit.apply();
        EventBus.getDefault().post(new DataUpdatedEvent.SortListEvent());
    }

    public void changeAppLanguage() {
        if (!TextUtils.equals(currLanguage, sharedPreferences.getString(FragmentSettings.KEY_PREFS_LANGUAGE, "en"))) {
            recreate();
        }
    }


    @Override
    public void onBackPressed() {
        if (searchBrowsePdf.isSearchOpen()) {
            searchBrowsePdf.closeSearchingPdfData();
        } else if (drawerLayBrowsePdf.isDrawerOpen(GravityCompat.START)) {
            drawerLayBrowsePdf.closeDrawer(GravityCompat.START);
        } else {
            showConfirmExit();
        }
    }

    @SuppressLint("ResourceType")
    private void showConfirmExit() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ActivityMain.this);
        View inflate = LayoutInflater.from(ActivityMain.this).inflate(R.layout.dialog_exit, null);
        bottomSheetDialog.setContentView(inflate);

        TextView txtExit = inflate.findViewById(R.id.txtExit);
        FrameLayout fl_native = inflate.findViewById(R.id.fl_native);
        AppManage.getInstance(ActivityMain.this).showNative(fl_native, AppManage.ADMOB_N[0], AppManage.FACEBOOK_N[0]);


        txtExit.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            finish();
        });

        bottomSheetDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_rate:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                }
                return;
            case R.id.ll_setting:
                AppManage.getInstance(ActivityMain.this).showInterstitialAd(ActivityMain.this, () -> {
                    startActivity(new Intent(ActivityMain.this, ActivitySettings.class));
                }, "", AppManage.app_mainClickCntSwAd);
                return;
            case R.id.ll_share:
                Utils.startShareActivity(ActivityMain.this);
                return;
            case R.id.ll_tools:
                AppManage.getInstance(ActivityMain.this).showInterstitialAd(ActivityMain.this, () -> {
                    startActivity(new Intent(ActivityMain.this, ActivityPDFTools.class));
                }, "", AppManage.app_mainClickCntSwAd);
                return;
            default:
        }
    }
}