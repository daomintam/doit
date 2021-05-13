package vn.edu.tdmu.mintam.doit.Activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener, ListTaskDialog.OnDialogDismissListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private PieChart pieChart;
    private DatabaseHandler db;
    private ArrayList<ToDoModel> done = new ArrayList<>();
    private ArrayList<ToDoModel> notDone = new ArrayList<>();
    private TextView tvStart;
    private TextView tvEnd;
    private Button btnFilter;
    private DatePickerDialog dpStart;
    private DatePickerDialog dpEnd;
    private ImageButton exit;

    private Calendar cStart;
    private Calendar cEnd;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getSupportActionBar().hide();
        pieChart = findViewById(R.id.pieChart_view);
        tvStart = findViewById(R.id.tv_start);
        tvEnd = findViewById(R.id.tv_end);
        btnFilter = findViewById(R.id.btn_filter);

        tvEnd.setOnClickListener(this);
        tvStart.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        findViewById(R.id.Exit).setOnClickListener(this);
        dpStart = new DatePickerDialog(this);
        dpEnd = new DatePickerDialog(this);
        dpStart.setOnDateSetListener(this);
        dpEnd.setOnDateSetListener(this);

        pieChart.setOnChartValueSelectedListener(this);
        db = new DatabaseHandler(this);
    }

    private void loadData() {
        done.clear();
        notDone.clear();
        List<ToDoModel> arr = db.byDate(cStart.getTime(), cEnd.getTime());
        for (ToDoModel t: arr) {
            if (t.getStatus() == 0) {
                notDone.add(t);
            } else {
                done.add(t);
            }
        }



        showPieChart();
    }

    private void showPieChart() {

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Đã hoàn thành",done.size());
        typeAmountMap.put("Chưa hoàn thành",notDone.size());

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#0A306A"));
        colors.add(Color.parseColor("#9e9e9e"));
        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        //setting text size of the value
        pieDataSet.setValueTextSize(15f);
        pieDataSet.setFormSize(15);

        pieDataSet.setFormLineWidth(20);
        pieDataSet.setSliceSpace(2);

        pieDataSet.setSelectionShift(10);
        pieDataSet.setValueTextColor(Color.WHITE);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);
        Description desc = new Description();
        desc.setText("");
        pieChart.setEntryLabelTextSize(8);
        pieChart.setDescription(desc);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        ListTaskDialog dialog = ListTaskDialog.newInstance();
        if (h.getX() == 0) {
            dialog.show(getSupportFragmentManager(), done, this);
        } else {
            dialog.show(getSupportFragmentManager(), notDone, this);
        }
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onDismiss() {
        loadData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_start:
                dpStart.show();
                break;
            case R.id.tv_end:
                dpEnd.show();
                break;
            case R.id.btn_filter:
                if (cStart == null || cEnd == null) {
                    Toast.makeText(this, "Chưa chọn thời gian", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadData();
                break;
            case R.id.Exit:
                finish();
                break;

        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        if (dpStart.getDatePicker() == datePicker) {
            cStart = Calendar.getInstance();
            cStart.set(i, i1, i2, 0, 0);
            tvStart.setText(format.format(cStart.getTime()));
        } else {
            cEnd = Calendar.getInstance();
            cEnd.set(i, i1, i2, 23, 59);
            tvEnd.setText(format.format(cEnd.getTime()));
        }
    }
}
