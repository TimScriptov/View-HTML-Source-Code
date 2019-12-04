package com.mcal.codeviewer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nononsenseapps.filepicker.AbstractFilePickerActivity;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static String HTMLSourceCode = ""/*BuildConfig.FLAVOR*/;
    private static String url = null;
    private final int FILE_CODE = 1337;
    private File currentFile;
    private Dialog dialog;
    private RelativeLayout dimBackground;
    private boolean firstTime = false;
    private EditText getWebPageAddress;
    private boolean inspectorMode = false;
    private Menu menu;
    private CustomWebview myWebView;
    private String path;
    private String pickedElement = null;
    private ProgressBar progressBar;
    private SharedPreferences settings;
    private String webPageAddress = null;

    public static String getHTMLSourceCode() {
        return HTMLSourceCode;
    }

    public void setHTMLSourceCode(String str) {
        HTMLSourceCode = str;
    }

    private static String removeScriptTags(String str) {
        Pattern compile = Pattern.compile("<\\s*script.*?(/\\s*>|<\\s*/\\s*script[^>]*>)");
        if (str == null) {
            return str;
        }
        Matcher matcher = compile.matcher(str);
        StringBuffer stringBuffer = new StringBuffer(str.length());
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(" "));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private int dpToPx(int i) {
        return Math.round(((float) i) * (getResources().getDisplayMetrics().xdpi / 160.0f));
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && "text/plain".equals(intent.getType()) && "android.intent.action.SEND".equals(intent.getAction())) {
            String stringExtra = getIntent().getStringExtra("android.intent.extra.TEXT");
            if (stringExtra != null) {
                getWebPageAddress.setText(stringExtra);
                myWebView.loadUrl(stringExtra);
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    @SuppressLint({"WrongConstant", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initialize() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getWebPageAddress = findViewById(R.id.searchfield);
        settings = getSharedPreferences("first_time", 0);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/website.txt";
        progressBar = findViewById(R.id.progressBar1);
        progressBar.setMax(100);
        progressBar.setVisibility(8);
        WebChromeClient websiteAnalyzerWebChromeClient = new WebsiteAnalyzerWebChromeClient(this);
        WebViewClient websiteAnalyzerWebClient = new WebsiteAnalyzerWebClient(this);
        myWebView = findViewById(R.id.webview);
        dimBackground = findViewById(R.id.bac_dim_layout);
        myWebView.setWebViewClient(websiteAnalyzerWebClient);
        myWebView.setWebChromeClient(websiteAnalyzerWebChromeClient);
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(true);
        myWebView.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");
        myWebView.loadUrl("http://" + "google.com");
        handleIntent();
        requestAdPermissions();
    }

    private void loadFile(Uri uri) {
        this.currentFile = new File(uri.getPath());
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(currentFile));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine);
                stringBuilder.append("\n");
            }
            bufferedReader.close();
        } catch (IOException ignored) {
        }
        if (currentFile.getAbsolutePath().contains("css")) {
            myWebView.loadDataWithBaseURL("file:///" + currentFile.getAbsolutePath(), HTMLSourceCode, "text/html", AsyncHttpResponseHandler.DEFAULT_CHARSET, null);
        } else if (currentFile.getAbsolutePath().contains(HTMLElementName.HTML)) {
            HTMLSourceCode = stringBuilder.toString();
            myWebView.loadDataWithBaseURL(null, HTMLSourceCode, "text/html", AsyncHttpResponseHandler.DEFAULT_CHARSET, null);
        }
    }

    @SuppressLint("WrongConstant")
    private void requestAdPermissions() {
        if (VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnActionListener() {
        getWebPageAddress.setOnTouchListener((view, motionEvent) -> {
            showMenu(Boolean.FALSE);
            return false;
        });
        this.getWebPageAddress.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i != 2) {
                return false;
            }
            hideKeyboard();
            showMenu(Boolean.TRUE);
            getWebPageAddress.clearFocus();
            webPageAddress = getWebPageAddress.getText() + ""/*BuildConfig.FLAVOR*/;
            if (webPageAddress.startsWith("http://") || webPageAddress.startsWith("https://")) {
                myWebView.loadUrl(webPageAddress);
            } else {
                webPageAddress = "http://" + getWebPageAddress.getText();
                myWebView.loadUrl(webPageAddress);
            }
            progressBar.setProgress(0);
            return true;
        });
        this.getWebPageAddress.setOnFocusChangeListener((view, z) -> {
            if (!z) {
                showMenu(Boolean.TRUE);
                hideKeyboard();
                getWebPageAddress.clearFocus();
            }
        });
    }

    @SuppressLint("WrongConstant")
    private void showMenu(Boolean bool) {
        menu.setGroupVisible(R.id.main_menu_group, bool);
        if (menu.hasVisibleItems()) {
            dimBackground.setVisibility(8);
            return;
        }
        dimBackground.bringToFront();
        dimBackground.setVisibility(0);
    }

    private void startChooseFileIntent(boolean z) {
        Intent intent = new Intent(this, FilePicker.class);
        intent.putExtra(AbstractFilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(AbstractFilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        if (z) {
            intent.putExtra(AbstractFilePickerActivity.EXTRA_MODE, 1);
        } else {
            intent.putExtra(AbstractFilePickerActivity.EXTRA_MODE, 0);
        }
        intent.putExtra(AbstractFilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, 1337);
    }

    @SuppressLint("WrongConstant")
    public void hideProgressBar() {
        progressBar.setVisibility(8);
    }

    public void loadUrl(String str) {
        myWebView.loadUrl(str);
    }

    @SuppressLint("MissingSuperCall")
    @TargetApi(16)
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i != 1337 || i2 != -1) {
            return;
        }
        if (!intent.getBooleanExtra(AbstractFilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
            loadFile(intent.getData());
        } else if (VERSION.SDK_INT >= 16) {
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                for (int i3 = 0; i3 < clipData.getItemCount(); i3++) {
                    loadFile(clipData.getItemAt(i3).getUri());
                }
            }
        } else {
            ArrayList stringArrayListExtra = intent.getStringArrayListExtra(AbstractFilePickerActivity.EXTRA_PATHS);
            if (stringArrayListExtra != null) {
                for (Object o : stringArrayListExtra) {
                    loadFile(Uri.parse((String) o));
                }
            }
        }
    }

    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main_layout);
        initialize();
        setOnActionListener();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        final View findViewById = findViewById(R.id.activity_root);
        findViewById.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (findViewById.getRootView().getHeight() - findViewById.getHeight() > dpToPx(200)) {
                if (!firstTime) {
                    firstTime = true;
                }
            } else if (firstTime) {
                showMenu(Boolean.TRUE);
                firstTime = false;
            }
        });
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint({"ClickableViewAccessibility", "WrongConstant"})
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean z = true;
        int itemId = menuItem.getItemId();
        if (itemId == R.id.inspectorMode) {
            if (settings.getBoolean("first_time", true)) {
                Toast.makeText(getBaseContext(), "This feature allows you to inspect html elements and edit them. To inspect particular html element just touch it and the app will display the html code behind that element.To go back to web browser just touch the view in browser button. ", 1).show();
                settings.edit().putBoolean("first_time", false).apply();
            }
            if (inspectorMode) {
                myWebView.setOnTouchListener(null);
                menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_touch_app));
                inspectorMode = false;
            } else {
                menu.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_touch_app_ambers));
                inspectorMode = true;
                myWebView.setOnTouchListener(new HtmlInspector((float) myWebView.getWidth(), (float) myWebView.getHeight(), this));
            }
        } else if (itemId == R.id.refresh) {
            if (webPageAddress == null || webPageAddress.equals(""/*BuildConfig.FLAVOR*/)) {
                myWebView.reload();
            } else {
                myWebView.loadUrl(webPageAddress);
            }
        } else if (itemId == R.id.savehtml) {
            shareFile(HTMLSourceCode);
        } else if (itemId == R.id.viewelements) {
            pickHTMLElement();
        } else if (itemId == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.request_desktop) {
            if (menuItem.isChecked()) {
                z = false;
            }
            menuItem.setChecked(z);
            myWebView.setDesktopMode(menuItem.isChecked());
        } else if (itemId == R.id.open) {
            startChooseFileIntent(false);
        } else if (itemId == R.id.opencss) {
            if (HTMLSourceCode == null) {
                Toast.makeText(this, "Please open HTML file first", 0).show();
            } else {
                startChooseFileIntent(false);
            }
        } else if (itemId == R.id.viewHTML) {
            if (HTMLSourceCode != null) {
                setTextViewText(HTMLSourceCode, null);
            } else {
                Toast.makeText(getBaseContext(), "Please load the page first.", 0).show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    protected void onResume() {
        super.onResume();
    }

    void pickHTMLElement() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.list_view_layout);
        final ListView listView = dialog.findViewById(R.id.lv);
        dialog.setCancelable(true);
        dialog.setTitle("HTML Tags");
        dialog.show();
        listView.setOnItemClickListener((adapterView, view, i, j) -> {
            dialog.hide();
            pickedElement = (String) listView.getItemAtPosition(i);
            setTextViewText(MainActivity.HTMLSourceCode, pickedElement);
        });
    }

    public void setEditText(String str) {
        getWebPageAddress.setText(str);
    }

    public void setProgressBar(int i) {
        progressBar.setProgress(i);
    }

    @SuppressLint("WrongConstant")
    void setTextViewText(String str) {
        if (str == null || HTMLSourceCode == null) {
            Toast.makeText(this, "Element cannot be empty", 0).show();
            return;
        }
        HTMLSourceCode = removeScriptTags(HTMLSourceCode);
        Intent intent = new Intent(this, ViewHtmlActivity.class);
        intent.putExtra("original", str);
        startActivity(intent);
    }

    @SuppressLint("WrongConstant")
    void setTextViewText(String str, String str2) {
        if (str != null) {
            HTMLSourceCode = removeScriptTags(HTMLSourceCode);
            Intent intent = new Intent(this, ViewHtmlActivity.class);
            if (str2 == null) {
                startActivity(intent);
                return;
            }
            Intent intent2 = new Intent(this, SearchTagsActivity.class);
            List<Element> allElements = new Source(str).getAllElements(str2.replace("<", ""/*BuildConfig.FLAVOR*/).replace(">", ""/*BuildConfig.FLAVOR*/));
            /*Serializable*/
            ArrayList<String> arrayList = new ArrayList<>();
            for (Element element : allElements) {
                arrayList.add(element.toString());
            }
            intent2.putExtra("elements", arrayList);
            intent2.putExtra(HTMLElementName.HTML, str);
            startActivity(intent2);
            return;
        }
        Toast.makeText(getBaseContext(), "Please load the page first.", 0).show();
    }

    public void setWebsiteAddress(String str) {
        url = str;
    }

    @SuppressLint("WrongConstant")
    public void shareFile(String str) {
        try {
            File file = new File(this.path);
            file.createNewFile();
            OutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(Jsoup.parse(str).toString());
            outputStreamWriter.close();
            fileOutputStream.close();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/*");
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
            startActivity(Intent.createChooser(intent, "Share page source:"));
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), 0).show();
        }
    }

    @SuppressLint("WrongConstant")
    public void showProgressBar() {
        progressBar.setVisibility(0);
    }

    @SuppressLint("WrongConstant")
    public void showYoutubeViolationToast() {
        Toast.makeText(this, "Google policy disallows viewing youtube videos.", 0).show();
    }
}