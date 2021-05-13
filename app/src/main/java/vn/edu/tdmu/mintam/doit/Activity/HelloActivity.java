package vn.edu.tdmu.mintam.doit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdmu.mintam.doit.R;

public class HelloActivity extends AppCompatActivity {
    Animation iconapp, nameapp;
    TextView txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_hello);
        getSupportActionBar().hide();
        iconapp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.icon_animation);
        nameapp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_animation);
        findViewById(R.id.iconBookStore).setAnimation(iconapp);
        findViewById(R.id.textHello).setAnimation(nameapp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new
                        Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, 2500);
    }
}