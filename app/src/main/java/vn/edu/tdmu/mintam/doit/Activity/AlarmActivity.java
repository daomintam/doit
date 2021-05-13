package vn.edu.tdmu.mintam.doit.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.alarm.AlarmWorker;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvLocation;
    private TextView tvType;
    private MediaPlayer player;
    private AppCompatImageView imBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getSupportActionBar().hide();
        ToDoModel toDoModel = (ToDoModel) getIntent().getExtras().getSerializable(ToDoModel.class.getName());
        tvTitle = findViewById(R.id.tv_title);
        tvDesc = findViewById(R.id.tv_desc);
        tvType = findViewById(R.id.tv_type);
        tvLocation = findViewById(R.id.tv_location);
        imBack = findViewById(R.id.im_back);

        tvType.setText(toDoModel.getType());
        tvTitle.setText(toDoModel.getTask());
        tvLocation.setText(toDoModel.getLocation());
        tvDesc.setText(toDoModel.getSubtitle());
        player = MediaPlayer.create(this, R.raw.alarm);
        player.setLooping(true);
        player.start();

        imBack.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.release();
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
