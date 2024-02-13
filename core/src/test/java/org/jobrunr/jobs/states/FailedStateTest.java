package org.jobrunr.jobs.states;

import org.jobrunr.scheduling.exceptions.JobClassNotFoundException;
import org.jobrunr.scheduling.exceptions.JobMethodNotFoundException;
import org.jobrunr.scheduling.exceptions.JobNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.jobrunr.jobs.JobDetailsTestBuilder.jobDetails;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

class FailedStateTest {

    @Test
    void getExceptionWithoutMessage() {
        final FailedState failedState = new FailedState("JobRunr message", new CustomException());

        assertThat(failedState.getException())
                .isInstanceOf(CustomException.class)
                .hasMessage(null);
    }

    @Test
    void getExceptionWithMessage() {
        final FailedState failedState = new FailedState("JobRunr message", new CustomException("custom exception message"));

        assertThat(failedState.getException())
                .isInstanceOf(CustomException.class)
                .hasMessage("custom exception message");
    }

    @Test
    void getExceptionWithNestedException() {
        final FailedState failedState = new FailedState("JobRunr message", new CustomException(new CustomException()));

        assertThat(failedState.getException())
                .isInstanceOf(CustomException.class)
                .hasCauseInstanceOf(CustomException.class);
    }

    @Test
    void getExceptionWithMessageAndNestedException() {
        final FailedState failedState = new FailedState("JobRunr message", new CustomException("custom exception message", new CustomException()));

        assertThat(failedState.getException())
                .isInstanceOf(CustomException.class)
                .hasMessage("custom exception message")
                .hasCauseInstanceOf(CustomException.class);
    }

    @Test
    void getExceptionWithMessageAndNestedExceptionWithMessage() {
        final FailedState failedState = new FailedState("JobRunr message", new CustomException("custom exception message", new CustomException("other exception")));

        assertThat(failedState.getException())
                .isInstanceOf(CustomException.class)
                .hasMessage("custom exception message")
                .hasCauseInstanceOf(CustomException.class)
                .hasRootCauseMessage("other exception");
    }

    @Test
    void getExceptionThatCannotReconstructIsStillAvailableUntilItIsStoredInTheStorageProvider() {
        final FailedState failedState = new FailedState("JobRunr message", new CustomExceptionThatCannotReconstruct(UUID.randomUUID()));

        assertThat(failedState.getException())
                .isInstanceOf(CustomExceptionThatCannotReconstruct.class);
    }

    @Test
    void getExceptionThatCannotReconstructIfLoadedFromStorageProvider() {
        final FailedState failedState = new FailedState("JobRunr message", new CustomExceptionThatCannotReconstruct(UUID.randomUUID()));

        setInternalState(failedState, "exception", null);

        assertThatThrownBy(failedState::getException)
                .isInstanceOf(IllegalStateException.class)
                .hasRootCauseInstanceOf(ReflectiveOperationException.class);
    }

    @Test
    void getExceptionForJobClassNotFoundException() {
        final FailedState failedState = new FailedState("JobRunr message", new JobClassNotFoundException(jobDetails().build()));

        setInternalState(failedState, "exception", null);

        assertThat(failedState.getException())
                .isInstanceOf(JobClassNotFoundException.class);
    }

    @Test
    void getExceptionForJobMethodNotFoundException() {
        final FailedState failedState = new FailedState("JobRunr message", new JobMethodNotFoundException(jobDetails().build()));

        setInternalState(failedState, "exception", null);

        assertThat(failedState.getException())
                .isInstanceOf(JobMethodNotFoundException.class);
    }

    @Test
    void getExceptionForJobNotFoundException() {
        final FailedState failedState = new FailedState("JobRunr message", new JobNotFoundException("some message"));

        setInternalState(failedState, "exception", null);

        assertThat(failedState.getException())
                .isInstanceOf(JobNotFoundException.class);
    }

    public static class CustomException extends Exception {

        public CustomException() {
        }

        public CustomException(String message) {
            super(message);
        }

        public CustomException(String message, Throwable cause) {
            super(message, cause);
        }

        public CustomException(Throwable cause) {
            super(cause);
        }
    }

    public static class CustomExceptionThatCannotReconstruct extends Exception {

        public CustomExceptionThatCannotReconstruct(UUID id) {
            super(id.toString());
        }
    }
}