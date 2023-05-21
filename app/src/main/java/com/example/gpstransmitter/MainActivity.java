package com.example.gpstransmitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button button1;
    private TextView txtResult;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
        txtResult = (TextView)findViewById(R.id.txtResult);

        button1.setBackgroundColor(Color.BLUE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    stopTransfer();
                }else {
                    startTransfer();
                }
            }
        });

    }

    private void startTransfer() {
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);

        button1.setText("산책 종료");
        button1.setBackgroundColor(Color.RED);

        check = true;
    }

    private void stopTransfer() {
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        stopService(serviceIntent);

        button1.setText("산책 시작");
        button1.setBackgroundColor(Color.BLUE);

        check = false;
    }
}
