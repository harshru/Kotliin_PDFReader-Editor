package testkotliin.pdfreader.pdfeditor.Activity;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import testkotliin.pdfreader.pdfeditor.R;
import testkotliin.pdfreader.pdfeditor.Fragments.FragmentSettings;
import testkotliin.pdfreader.pdfeditor.utils.LocaleUtils;

public class ActivitySettings extends AppCompatActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocaleUtils.setUpLanguage(this);
        setContentView(R.layout.activity_settings);

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameSettings, new FragmentSettings()).commit();
    }
}
