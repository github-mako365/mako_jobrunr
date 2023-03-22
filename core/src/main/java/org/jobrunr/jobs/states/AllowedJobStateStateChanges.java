package org.jobrunr.jobs.states;

import static org.jobrunr.jobs.states.StateName.*;

public class AllowedJobStateStateChanges {

    private AllowedJobStateStateChanges() {

    }

    public static boolean isIllegalStateChange(StateName from, StateName to) {
        return !isAllowedStateChange(from, to);
    }

    public static boolean isAllowedStateChange(StateName from, StateName to) {
        switch (from) {
            case SCHEDULED:
                return to != PROCESSING;
            case ENQUEUED:
                return to != ENQUEUED;
            case PROCESSING:
                return to == SUCCEEDED || to == FAILED || to == DELETED;
            case FAILED:
            case SUCCEEDED:
                return to == SCHEDULED || to == ENQUEUED || to == DELETED;
            case DELETED:
                return to == SCHEDULED || to == ENQUEUED;
            default:
                throw new UnsupportedOperationException("Unknown state " + from);
        }
    }
}
