package ca.cmpt276.cmpt276project.ui.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.cmpt276project.R;
import ca.cmpt276.cmpt276project.model.Report;
import ca.cmpt276.cmpt276project.model.Violation;

/**
 * Violation adapter, needed for RecyclerView implementation
 * Also handles OnClick events
 */
public class ViolationAdapter extends RecyclerView.Adapter<ViolationHolder> {
    private Context context;
    private List<Violation> violations;
    private RecyclerView recyclerView;
    private Report report;

    private final View.OnClickListener violationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickEvent(v);
        }
    };

    private void clickEvent(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        Violation violation = violations.get(position);
        String msg = violation.getViolationDetails();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public ViolationAdapter(Context context, List<Violation> violations, Report report) {
        this.context = context;
        this.violations = violations;
        this.report = report;
    }

    @NonNull
    @Override
    public ViolationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_violation_card, parent,
                false);
        view.setOnClickListener(violationClickListener);
        return new ViolationHolder(context, view, report);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationHolder holder, int position) {
        Violation violation = violations.get(position);
        holder.setDetails(violation);
    }

    @Override
    public int getItemCount() {
        return violations.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }
}