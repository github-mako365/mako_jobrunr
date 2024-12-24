package org.jobrunr.server.strategy;

import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.server.JobSteward;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicWorkDistributionStrategyTest {


    @Mock
    private BackgroundJobServer backgroundJobServer;
    @Mock
    private JobSteward jobSteward;
    private BasicWorkDistributionStrategy workDistributionStrategy;

    @BeforeEach
    void setUpWorkDistributionStrategy() {
        when(backgroundJobServer.getJobSteward()).thenReturn(jobSteward);
        workDistributionStrategy = new BasicWorkDistributionStrategy(backgroundJobServer, 100);
    }

    @Test
    void canOnboardIfWorkQueueSizeIsEmpty() {
        when(jobSteward.getOccupiedWorkerCount()).thenReturn(0);

        assertThat(workDistributionStrategy.canOnboardNewWork()).isTrue();
    }

    @Test
    void canNotOnboardIfWorkQueueIsFull() {
        when(jobSteward.getOccupiedWorkerCount()).thenReturn(100);

        assertThat(workDistributionStrategy.canOnboardNewWork()).isFalse();
    }

    @Test
    void canOnboardIfMoreThan30PercentFreeInWorkQueue() {
        when(jobSteward.getOccupiedWorkerCount()).thenReturn(69);

        assertThat(workDistributionStrategy.canOnboardNewWork()).isTrue();
    }

    @Test
    void canNotOnboardIfLessThan30PercentFreeInWorkQueue() {
        when(jobSteward.getOccupiedWorkerCount()).thenReturn(71);

        assertThat(workDistributionStrategy.canOnboardNewWork()).isFalse();
    }

}