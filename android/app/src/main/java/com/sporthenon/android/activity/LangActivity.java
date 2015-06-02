package com.sporthenon.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sporthenon.android.R;

import java.util.Locale;

public class LangActivity extends Activity implements View.OnClickListener {

    protected RadioButton langEN;
    protected RadioButton langFR;

    protected void onCreate(Bundle state) {
        super.onCreate(state);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = prefs.getString("lang", null);
        lang = null; // TEST
        if (lang != null) {
            setLocale(lang);
            nextActivity();
        }
        else {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
            setContentView(R.layout.activity_lang);
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

            ((TextView) findViewById(R.id.title)).setText(R.string.select_language);
            findViewById(R.id.back_icon).setVisibility(View.GONE);
            findViewById(R.id.search_icon).setVisibility(View.GONE);
            langEN = (RadioButton) findViewById(R.id.langEN);
            langFR = (RadioButton) findViewById(R.id.langFR);
            findViewById(R.id.ok).setOnClickListener(this);
        }
        /*ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Thinking...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        dialog.hide();*/
    }

    public void nextActivity() {
        Intent i = new Intent(this, SportActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        String lang = langFR.isChecked() ? "fr" : "en";
        editor.putString("lang", lang);
        editor.apply();
        setLocale(lang);
        nextActivity();
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

 }