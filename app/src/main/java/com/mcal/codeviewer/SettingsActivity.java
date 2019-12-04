package com.mcal.codeviewer;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private void setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.settings_activity);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(R.id.content, new MyPreferenceFragment()).commit();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.preference_screen);
            final ListPreference listPreference = (ListPreference) findPreference("theme_preference");
            listPreference.setTitle(getActivity().getResources().getStringArray(R.array.themes)[ViewHtmlActivity.getThemeNo() - 1]);
            listPreference.setOnPreferenceChangeListener((preference, obj) -> {
                String str = (String) obj;
                CharSequence[] entries = listPreference.getEntries();
                int findIndexOfValue = listPreference.findIndexOfValue(str);
                listPreference.setTitle(entries[findIndexOfValue]);
                ViewHtmlActivity.setThemeNo(findIndexOfValue + 1);
                return true;
            });
            findPreference("line_numbers").setOnPreferenceChangeListener((preference, obj) -> {
                ViewHtmlActivity.setShowLineNumber((Boolean) obj);
                return true;
            });
            final ListPreference listPreference2 = (ListPreference) findPreference("text_size_preference");
            listPreference2.setTitle(String.valueOf(ViewHtmlActivity.getTextSize()));
            listPreference2.setOnPreferenceChangeListener((preference, obj) -> {
                ViewHtmlActivity.setTextSize(Integer.valueOf((String) obj));
                listPreference2.setTitle((String) obj);
                return true;
            });
        }
    }
}