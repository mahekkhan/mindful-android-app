package ca.cmpt276.cmpt276project.ui.recyclerview;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ca.cmpt276.cmpt276project.model.Report;
import ca.cmpt276.cmpt276project.model.Violation;
import ca.cmpt276.cmpt276project.R;

/**
 * ViewHolder for Violation, needed for RecyclerView implementation
 */
public class ViolationHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView txtBriefDescription;
    private ImageView imgSeverityLevel;
    private Report report;

    ViolationHolder(Context context, View itemView, Report report) {
        super(itemView);
        this.context = context;
        this.report = report;
        txtBriefDescription = itemView.findViewById(R.id.violation_card_text_description);
        imgSeverityLevel = itemView.findViewById(R.id.violation_card_image_NOV);
    }

    void setDetails(Violation violation) {
        Integer violationCode = violation.getViolationCode();
        String violationNature = violation.getNature();
        String severityLevel = report.getHazardLevel();

        setBriefDescription(violationCode);
        setIcon(violationNature,severityLevel);
    }

    private void setBriefDescription(Integer violationCode) {
        String codeForBriefDescription = "v" + violationCode;
        int resourceID = context.getResources().getIdentifier(codeForBriefDescription,
                "string", context.getPackageName());
        String violationBriefDescription = context.getString(resourceID);
        txtBriefDescription.setText(violationBriefDescription);
    }

    private void setIcon(String natureOfViolation, String severityLevel){
        if(natureOfViolation != null  && natureOfViolation.equals("pest")){
            setPestImage(severityLevel);

        } else if(natureOfViolation!=null && natureOfViolation.equals("equipment")){
            setEquipmentImage(severityLevel);

        } else if(natureOfViolation!=null && natureOfViolation.equals("food")){
            setFoodImage(severityLevel);

        } else if(natureOfViolation!=null && natureOfViolation.equals("employee")){
            setEmployeeImage(severityLevel);

        } else if(natureOfViolation!=null && natureOfViolation.equals("premises")){
            setSanitaryImage(severityLevel);

        } else if(natureOfViolation!=null && natureOfViolation.equals("regulations")){
            setBuildingRegulationImage(severityLevel);
        }
    }

    private void setPestImage(String severityLevel){
        if (severityLevel.equals("Moderate")) {
            imgSeverityLevel.setImageResource(R.drawable.pests_yellow);

        } else if (severityLevel.equals("High")) {
            imgSeverityLevel.setImageResource(R.drawable.pests_red);

        } else {
            imgSeverityLevel.setImageResource(R.drawable.pests_green);
        }
    }

    private void setEquipmentImage(String severityLevel){
        if (severityLevel.equals("Moderate")) {
            imgSeverityLevel.setImageResource(R.drawable.equipment_yellow);

        } else if (severityLevel.equals("High")) {
            imgSeverityLevel.setImageResource(R.drawable.equipment_red);

        } else {
            imgSeverityLevel.setImageResource(R.drawable.equipment_green);
        }
    }

    private void setFoodImage(String severityLevel){
        if (severityLevel.equals("Moderate")) {
            imgSeverityLevel.setImageResource(R.drawable.food_yellow);

        } else if (severityLevel.equals("High")) {
            imgSeverityLevel.setImageResource(R.drawable.food_red);

        } else {
            imgSeverityLevel.setImageResource(R.drawable.food_green);
        }
    }

    private void setEmployeeImage(String severityLevel){
        if (severityLevel.equals("Moderate")) {
            imgSeverityLevel.setImageResource(R.drawable.employee_yellow);

        } else if (severityLevel.equals("High")) {
            imgSeverityLevel.setImageResource(R.drawable.employee_red);

        } else {
            imgSeverityLevel.setImageResource(R.drawable.employee_green);
        }
    }

    private void setSanitaryImage(String severityLevel){
        if (severityLevel.equals("Moderate")) {
            imgSeverityLevel.setImageResource(R.drawable.sanitation_yellow);

        } else if (severityLevel.equals("High")) {
            imgSeverityLevel.setImageResource(R.drawable.sanitation_red);

        } else {
            imgSeverityLevel.setImageResource(R.drawable.sanitation_green);
        }
    }

    private void setBuildingRegulationImage(String severityLevel){
        if (severityLevel.equals("Moderate")) {
            imgSeverityLevel.setImageResource(R.drawable.building_yellow);

        } else if (severityLevel.equals("High")) {
            imgSeverityLevel.setImageResource(R.drawable.building_red);

        } else {
            imgSeverityLevel.setImageResource(R.drawable.building_green);
        }
    }
}