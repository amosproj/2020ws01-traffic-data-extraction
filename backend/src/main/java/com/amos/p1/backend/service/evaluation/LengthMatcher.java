package com.amos.p1.backend.service.evaluation;

import com.amos.p1.backend.data.Incident;

// Thesis page 40
public class LengthMatcher extends Matcher {
    public LengthMatcher(Incident incident1, Incident incident2) {
        super(incident1, incident2);
        throw new IllegalStateException("Needs to be implemented");
    }

    @Override
    public boolean isDropped() {
        throw new IllegalStateException("Needs to be implemented");
    }

    @Override
    public String getDescription() {
        throw new IllegalStateException("Needs to be implemented");
    }

    @Override
    public int getConfidence() {
        throw new IllegalStateException("Needs to be implemented");
    }

    public double getLengthDifference() {
        throw new IllegalStateException("Needs to be implemented");
    }
}
