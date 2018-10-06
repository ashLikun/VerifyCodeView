package com.ashlikun.verifycodeview.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ashlikun.verifycodeview.VerifyCodeView;

public class MainActivity extends AppCompatActivity {

    VerifyCodeView codeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        codeView = findViewById(R.id.codeView);
        codeView.setListener(new VerifyCodeView.OnCompleteListener() {
            @Override
            public void onComplete(String code) {
                Toast.makeText(MainActivity.this, code, Toast.LENGTH_LONG).show();
                codeView.cleanCode();
            }
        });
    }
}
