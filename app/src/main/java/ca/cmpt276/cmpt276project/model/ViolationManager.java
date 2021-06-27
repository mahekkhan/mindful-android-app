package ca.cmpt276.cmpt276project.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViolationManager {

    private List<Violation> Violations = new ArrayList<>();

    public void add(Violation violation) {
        Violations.add(violation);
    }

    public Violation get(int index) {
        return Violations.get(index);
    }

    public Iterator<Violation> iterator() {
        return Violations.iterator();
    }

    public List<Violation> getViolations() {
        return Violations;
    }

}
