package vn.edu.tdmu.mintam.doit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

import vn.edu.tdmu.mintam.doit.R;

public class InformationActivity extends AppCompatActivity {

    Animation logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        getSupportActionBar().hide();
        logo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_animation);
        findViewById(R.id.logo).setAnimation(logo);
        findViewById(R.id.fb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent ;
                String id="110070322502688";
                try {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/" + id));
                } catch (Exception e) {
                    intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + id));
                }
            }
        });
        findViewById(R.id.Exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}