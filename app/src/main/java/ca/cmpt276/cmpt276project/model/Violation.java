package ca.cmpt276.cmpt276project.model;

/**
 * Violation class is modelled based on each violation found in
 * the violations list .csv.
 * This class has a violation code, type, description of violation,
 * and whether it was repeated or not.
 */
public class Violation {
    private final Integer violationCode;
    private String violationType;
    private String violationDetails;
    private String violationRepetition;
    private String nature;

    public Violation(Integer violationCode) {
        this.violationCode = violationCode;
    }


    //// Getter and Setters ////

    protected void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getViolationType() {
        return violationType;
    }

    protected void setViolationDetails(String violationDetails) {
        this.violationDetails = violationDetails;
    }

    public String getViolationDetails() {
        return violationDetails;
    }

    protected void setViolationRepetition(String violationRepetition) {
        this.violationRepetition = violationRepetition;
    }

    public String getViolationRepetition() {
        return violationRepetition;
    }

    public Integer getViolationCode() {
        return violationCode;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getNature() {
        return nature;
    }

    /*@Override
    public int compareTo (Object otherViolation) {
        int comparison = ((Violation) otherViolation).getViolationCode().compareTo(this.getViolationCode());
        return comparison;
    }*/
}