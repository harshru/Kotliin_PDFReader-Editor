package testkotliin.pdfreader.pdfeditor.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import testkotliin.pdfreader.pdfeditor.R;
import testkotliin.pdfreader.pdfeditor.Adapter.AdapterBookmarks;
import testkotliin.pdfreader.pdfeditor.Adapter.AdapterContents;
import testkotliin.pdfreader.pdfeditor.Adapter.AdapterContentsPager;
import testkotliin.pdfreader.pdfeditor.GetSet.BookmarkData;

import com.pesonal.adsdk.AppManage;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.Objects;

public class ActivityContents extends AppCompatActivity implements AdapterBookmarks.OnBookmarkClickedListener, AdapterContents.OnContentClickedListener {
    ViewPager pager;
    TabLayout tabBookmarkPdf;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_contents);

        LinearLayout ll_banner = findViewById(R.id.ll_banner);
        AppManage.getInstance(ActivityContents.this).showBanner(ll_banner, AppManage.ADMOB_B[0], AppManage.FACEBOOK_B[0]);


        String stringExtra = getIntent().getStringExtra("com.example.pdfreader.CONTENTS_PDF_PATH");
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setTitle(new File(stringExtra).getName());

        tabBookmarkPdf = findViewById(R.id.tabBookmarkPdf);

        pager = findViewById(R.id.pager);

        pager.setAdapter(new AdapterContentsPager(getSupportFragmentManager(), stringExtra));
        tabBookmarkPdf.addTab(tabBookmarkPdf.newTab().setText(R.string.contents));
        tabBookmarkPdf.addTab(tabBookmarkPdf.newTab().setText(R.string.bookmarks));
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabBookmarkPdf));
        tabBookmarkPdf.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }
        });
    }

    @Override
    public void onBookmarkClicked(BookmarkData bookmarkData) {
        Intent intent = new Intent();
        intent.putExtra("com.example.pdfreader.PAGE_NUMBER", bookmarkData.getPageNumber());
        setResult(-1, intent);
        finish();
    }

    @Override
    public void onContentClicked(PdfDocument.Bookmark bookmark) {
        Intent intent = new Intent();
        intent.putExtra("com.example.pdfreader.PAGE_NUMBER", ((int) bookmark.getPageIdx()) + 1);
        setResult(-1, intent);
        finish();
    }
}