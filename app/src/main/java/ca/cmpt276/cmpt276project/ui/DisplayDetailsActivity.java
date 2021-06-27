package ca.cmpt276.cmpt276project.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.cmpt276project.model.Report;
import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.model.RestaurantManager;
import ca.cmpt276.cmpt276project.model.Violation;
import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.ui.recyclerview.ViolationAdapter;

public class DisplayDetailsActivity extends AppCompatActivity {
    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_details);
        setupBackButton();
        loadIntentData();
        setTextViews();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Violation> violations = report.getViolations();
        ViolationAdapter adapter = new ViolationAdapter(this, violations, report);
        recyclerView.setAdapter(adapter);
    }

    private void setupBackButton() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadIntentData() {
        Bundle data = getIntent().getExtras();
        if (data != null) {
            int inspectionIndex = data.getInt(Report.REPORT_KEY);
            int restaurantIndex = data.getInt(Restaurant.RESTAURANT_KEY);
            Restaurant restaurant =
                    RestaurantManager.getManagerInstance().getRestaurants().get(restaurantIndex);
            report = restaurant.getReportsList().get(inspectionIndex);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextViews() {
        TextView textDate = findViewById(R.id.singleViolation_InspecDate);
        TextView textHazardLevel = findViewById(R.id.singleViolation_text_hazardLevel);
        TextView textTypeOfViolation = findViewById(R.id.singleViolation_Type);
        TextView textNonCritIssue = findViewById(R.id.singleViolation_nonCriticalIssues);
        TextView textCritIssue = findViewById(R.id.singleViolation_criticalIssuesFound);
        ImageView imgHazardLvl = findViewById(R.id.singleViolation_hazardLevelIcon);

        String inspectionDate = report.getFullDate();
        int criticalIssues = report.getCritIssues();
        int nonCriticalIssues = report.getNonCritIssues();
        String hazardLevel = report.getHazardLevel();
        String typeViolation = report.getType();


        textDate.setText(inspectionDate);
        textTypeOfViolation.setText(typeViolation);
        // textCritIssue.setText("Critical: " + criticalIssues);
        // textNonCritIssue.setText("Non-critical: " + nonCriticalIssues);
        String critical = getResources().getString(R.string.details_crit);
        String nonCritical = getResources().getString(R.string.details_noncrit);

        textCritIssue.setText(critical + " " + criticalIssues);
        textNonCritIssue.setText(nonCritical + " " + nonCriticalIssues);

        textHazardLevel.setText(hazardLevel);

        if (hazardLevel.equals("Moderate")) {
            imgHazardLvl.setImageResource(R.drawable.hazard_level_yellow);
        } else if (hazardLevel.equals("High")) {
            imgHazardLvl.setImageResource(R.drawable.hazard_level_red);
        } else {
            imgHazardLvl.setImageResource(R.drawable.hazard_level_green);
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, DisplayDetailsActivity.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}