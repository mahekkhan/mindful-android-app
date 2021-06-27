package ca.cmpt276.cmpt276project.ui.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.cmpt276project.model.Report;
import ca.cmpt276.cmpt276project.model.Restaurant;
import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.ui.DisplayDetailsActivity;

/**
 * Report adapter, needed for RecyclerView implementation
 * Also handles OnClick events
 */
public class InspectionAdapter extends RecyclerView.Adapter<InspectionHolder> {
    private int restaurantIndex;
    private Context context;
    private List<Report> inspections;
    private RecyclerView recyclerView;
    private final View.OnClickListener inspectionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickEvent(v);
        }
    };

    private void clickEvent(View v) {
        int inspectionIndex = recyclerView.getChildAdapterPosition(v);
        Report report = inspections.get(inspectionIndex);

        if(report.getCritIssues()==0 && report.getNonCritIssues()==0){
            String noViolations = "No Violations";
            Toast.makeText(context, noViolations, Toast.LENGTH_SHORT).show();

        } else {
            Intent singleInspecIntent = DisplayDetailsActivity.makeIntent(context);
            singleInspecIntent.putExtra(Report.REPORT_KEY, inspectionIndex);
            singleInspecIntent.putExtra(Restaurant.RESTAURANT_KEY, restaurantIndex);
            context.startActivity(singleInspecIntent);
        }
    }

    public InspectionAdapter(Context context, List<Report> reports, int restaurantIndex) {
        this.context = context;
        this.inspections = reports;
        this.restaurantIndex = restaurantIndex;
    }

    @NonNull
    @Override
    public InspectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_inspection_card, parent,
                false);
        view.setOnClickListener(inspectionClickListener);
        return new InspectionHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull InspectionHolder holder, int position) {
        Report report = inspections.get(position);
        holder.setDetails(report);
    }

    @Override
    public int getItemCount() {
        return inspections.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
}