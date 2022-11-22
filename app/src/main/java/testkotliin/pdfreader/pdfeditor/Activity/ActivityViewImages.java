package testkotliin.pdfreader.pdfeditor.Activity;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pesonal.adsdk.AppManage;

import testkotliin.pdfreader.pdfeditor.R;
import testkotliin.pdfreader.pdfeditor.Adapter.AdapterViewImages;
import testkotliin.pdfreader.pdfeditor.data.DbHelper;

import java.util.List;
import java.util.Objects;

public class ActivityViewImages extends AppCompatActivity {
    public static final String GENERATED_IMAGES_PATH = "com.example.pdfreader.GENERATED_IMAGES_PATH";
    public ProgressBar progressViewImage;
    public RecyclerView recycleViewPdfImage;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_view_images);

        LinearLayout ll_banner = findViewById(R.id.ll_banner);
        AppManage.getInstance(ActivityViewImages.this).showBanner(ll_banner, AppManage.ADMOB_B[0], AppManage.FACEBOOK_B[0]);


        recycleViewPdfImage = findViewById(R.id.recycleViewPdfImage);
        progressViewImage = findViewById(R.id.progressViewImage);

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String string = extras.getString(GENERATED_IMAGES_PATH);
            recycleViewPdfImage.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            new LoadPdfViewImages(this, string).execute();
        }
    }

    public class LoadPdfViewImages extends AsyncTask<Void, Void, Void> {
        Context context;
        String imageDirectory;
        AdapterViewImages viewImagesAdapter;

        public LoadPdfViewImages(Context context2, String str) {
            context = context2;
            imageDirectory = str;
        }

        public void onPreExecute() {
            super.onPreExecute();
        }

        public Void doInBackground(Void... voidArr) {
            List<Uri> allImages = DbHelper.getInstance(context).getAllImages(imageDirectory);
            viewImagesAdapter = new AdapterViewImages(context, allImages);
            return null;
        }

        public void onPostExecute(Void r2) {
            super.onPostExecute(r2);
            progressViewImage.setVisibility(View.GONE);
            recycleViewPdfImage.setAdapter(viewImagesAdapter);
        }
    }
}