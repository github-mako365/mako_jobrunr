package org.jobrunr.scheduling.exceptions;

import org.jobrunr.jobs.JobDetails;

public class JobClassNotFoundException extends JobNotFoundException {

    @SuppressWarnings("unused") // Needed for deserialization from FailedState
    public JobClassNotFoundException(String message) {
        super(message);
    }

    public JobClassNotFoundException(JobDetails jobDetails) {
        super(jobDetails);
    }
}
