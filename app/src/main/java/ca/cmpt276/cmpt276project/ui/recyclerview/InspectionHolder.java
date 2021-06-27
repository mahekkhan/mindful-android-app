package ca.cmpt276.cmpt276project.ui.recyclerview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.cmpt276project.model.Report;
import ca.cmpt276.cmpt276project.R;

/**
 * ViewHolder for Report, needed for RecyclerView implementation
 */
public class InspectionHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView txtResCritIssues;
    private TextView txtResNonCritIssues;
    private TextView txtInspectionDate;
    private ImageView imgHazardIcon;

    InspectionHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        txtResCritIssues = itemView.findViewById(R.id.singleRes_card_text_crit_issue);
        txtResNonCritIssues = itemView.findViewById(R.id.singleRes_card_text_noncrit_issue);
        txtInspectionDate = itemView.findViewById(R.id.singleRes_card_text_inspect_date);
        imgHazardIcon = itemView.findViewById(R.id.singleRes_card_image_hazard);
    }

    void setDetails(Report report) {
        String critIssues = context.getString(R.string.singleRes_crit_issue,
                report.getCritIssues());
        txtResCritIssues.setText(critIssues);

        String nonCritIssues = context.getString(R.string.singleRes_noncrit_issue,
                report.getNonCritIssues());
        txtResNonCritIssues.setText(nonCritIssues);

        String inspectionDate = context.getString(R.string.singleRes_inspection_date,
                report.getHowLongAgoOccurredFormatted());
        txtInspectionDate.setText(inspectionDate);

        setHazardIcon(report.getHazardLevel());
    }

    private void setHazardIcon(String hazardLevel) {
        if (hazardLevel.equals("Moderate")) {
            imgHazardIcon.setImageResource(R.drawable.hazard_level_yellow);
        } else if (hazardLevel.equals("High")) {
            imgHazardIcon.setImageResource(R.drawable.hazard_level_red);
        } else {
            imgHazardIcon.setImageResource(R.drawable.hazard_level_green);
        }
    }
}