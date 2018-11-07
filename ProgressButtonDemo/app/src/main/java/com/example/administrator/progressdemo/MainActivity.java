package com.example.administrator.progressdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressButton progressButton;
    private ProgressThread pt;
    private Boolean isPause = false;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressButton.setProgress(msg.arg1);
        }
    };
    private static int z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressButton = (ProgressButton) findViewById(R.id.btn);
        progressButton.setTag(0);
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 防止开启多个异步线程
                if ((Integer) progressButton.getTag() == 0) {
                    Log.e("11111","11111");
                    pt = new ProgressThread();
                    pt.start();
                    progressButton.setTag(1);
                }
                if (!progressButton.isFinish()) {
                    progressButton.toggle();
                }

            }
        });
        progressButton.setOnStateListener(new ProgressButton.OnStateListener() {
            @Override
            public void onFinish() {
                isPause = true;
                synchronized (this) {
                    pt.interrupt();
                }
                progressButton.setText("完 成");
            }

            @Override
            public void onStop() {
                Log.i("zz", "stop");
                progressButton.setText("继 续");
                isPause = true;
            }

            @Override
            public void onContinue() {
                Log.i("zz", "continue");

                isPause = false;
            }
        });
    }

    public class ProgressThread extends Thread {

        //private int z = 0;

        @Override
        public void run() {
            while (true) {
                synchronized (this) {
                    if (!isPause) {
                        z += 2;
                        Message msg = Message.obtain();
                        msg.arg1 = z;
                        handler.sendMessage(msg);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
