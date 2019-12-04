package com.mcal.codeviewer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.htmlparser.jericho.HTMLElementName;

import java.util.ArrayList;

public class SearchTagsActivity extends AppCompatActivity {
    String HTMLSourceCode;
    ArrayAdapter<String> adapter;
    ArrayList<String> elements;
    EditText inputSearch;
    ListView lv;
    SearchTagsActivity searchTagsActivity;
    TextView tag;
    String tagCode;
    ArrayList<String> tempArrayList;

    @SuppressLint("WrongConstant")
    public void onClick(View view) {
        this.tag = (TextView) view;
        this.tagCode = ""/*BuildConfig.FLAVOR*/ + tag.getText();
        if (HTMLSourceCode != null) {
            Intent intent = new Intent(this, ViewHtmlActivity.class);
            intent.putExtra(HTMLElementName.HTML, HTMLSourceCode);
            intent.putExtra("original", tagCode);
            startActivity(intent);
            finish();
            return;
        }
        Toast.makeText(getBaseContext(), "Element cannot be empty", 0).show();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_search_tags);
        searchTagsActivity = this;
        setSupportActionBar(findViewById(R.id.toolbar));
        tempArrayList = new ArrayList<>();
        HTMLSourceCode = getIntent().getExtras().getString(HTMLElementName.HTML);
        elements = getIntent().getExtras().getStringArrayList("elements");
        if (elements == null) {
            elements = new ArrayList<>();
            elements.add("No elements to display");
        } else if (elements.size() == 0) {
            elements.add("No elements to display");
        }
        lv = findViewById(R.id.list_view);
        inputSearch = findViewById(R.id.searchfield);
        adapter = new ArrayAdapter(this, R.layout.list_item, R.id.product_name, elements);
        lv.setAdapter(adapter);
        inputSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                tempArrayList.clear();
                int length = charSequence.length();
                for (String str : elements) {
                    if (length <= str.length() && str.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        tempArrayList.add(str);
                    }
                }
                adapter = new ArrayAdapter(searchTagsActivity, R.layout.list_item, R.id.product_name, tempArrayList);
                lv.setAdapter(adapter);
            }
        });
    }

    protected void onDestroy() {
        HTMLSourceCode = null;
        elements = null;
        adapter = null;
        lv = null;
        tempArrayList = null;
        super.onDestroy();
    }
}