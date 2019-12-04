package com.mcal.codeviewer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.SourceFormatter;

import org.jsoup.Jsoup;

import java.lang.reflect.Method;

import br.tiagohm.codeview.CodeView;
import br.tiagohm.codeview.HightlightJs;
import br.tiagohm.codeview.HightlightJs.Languages;
import br.tiagohm.codeview.HightlightJs.Themes;
import br.tiagohm.codeview.Theme;

public class ViewHtmlActivity extends AppCompatActivity {
    private static boolean showLineNumbers = true;
    private static int textSize = 14;
    private static int theme = 1;
    private String html = null;
    private String original = null;
    private CodeView pageSource;
    private EditText searchQuery;

    public static int getTextSize() {
        return textSize;
    }

    public static void setTextSize(int i) {
        textSize = i;
    }

    public static int getThemeNo() {
        return theme;
    }

    public static void setThemeNo(int i) {
        theme = i;
    }

    public static void setShowLineNumber(boolean z) {
        showLineNumbers = z;
    }

    private Theme getThemeFromPreference() {
        return theme == 2 ? Themes.DARCULA : theme == 3 ? Themes.ATOM_ONE_LIGHT : theme == 4 ? Themes.ATOM_ONE_DARK : theme == 5 ? Themes.MONOKAI : theme == 6 ? Themes.XCODE : Themes.DEFAULT;
    }

    @SuppressLint("NewApi")
    private void highLightText(String str) {
        pageSource.findAllAsync(str);
        try {
            for (Method method : WebView.class.getDeclaredMethods()) {
                if (method.getName().equals("setFindIsUp")) {
                    method.setAccessible(true);
                    method.invoke(pageSource, Boolean.TRUE);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void setOnActionListener() {
        searchQuery.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence != null) {
                    highLightText(charSequence.toString());
                }
            }
        });
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_view_html);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        searchQuery = findViewById(R.id.searchfield);
        html = MainActivity.getHTMLSourceCode();
        original = getIntent().getStringExtra("original");
        pageSource = findViewById(R.id.code_view);
        pageSource.getSettings().setBuiltInZoomControls(true);
        pageSource.getSettings().setDisplayZoomControls(false);
        pageSource.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        Theme themeFromPreference = getThemeFromPreference();
        if (original != null) {
            SourceFormatter sourceFormatter = new SourceFormatter(new Source(original));
            sourceFormatter.setIndentString(" ");
            original = sourceFormatter.toString();
            if (original.length() < 1000) {
                pageSource.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            }
            pageSource.setSyntaxHighlighter(new HightlightJs()).setCode(original).setTheme(themeFromPreference).setLanguage(Languages.HTML).setShowLineNumber(showLineNumbers).setTextSize(textSize).apply();
        } else {
            html = Jsoup.parse(html).outerHtml();
            pageSource.setSyntaxHighlighter(new HightlightJs()).setCode(html).setTheme(themeFromPreference).setLanguage(Languages.HTML).setShowLineNumber(showLineNumbers).setTextSize(textSize).apply();
        }
        setOnActionListener();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    protected void onDestroy() {
        pageSource = null;
        searchQuery = null;
        html = null;
        original = null;
        super.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            case R.id.up:
                this.pageSource.findNext(true);
                return true;
            case R.id.down:
                pageSource.findNext(false);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    protected void onResume() {
        super.onResume();
    }
}