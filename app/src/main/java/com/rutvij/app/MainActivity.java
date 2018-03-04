package com.rutvij.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.rutvij.debounce.Debouncer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @SuppressWarnings("SpellCheckingInspection")
    private Debouncer debouncer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Debouncer.setDefaultDelayTime(1000);
        debouncer = Debouncer.getInstance();
    }

    public void onClick(View view) {
        debouncer.debounce("logClicked",() -> Log.d(TAG,"OnClick"));
    }

    @Override
    protected void onPause() {
        debouncer.clearAll();
        super.onPause();
    }
}

