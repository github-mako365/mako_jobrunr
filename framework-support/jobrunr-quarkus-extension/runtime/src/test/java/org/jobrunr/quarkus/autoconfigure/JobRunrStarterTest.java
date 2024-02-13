package org.jobrunr.quarkus.autoconfigure;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.inject.Instance;
import org.jobrunr.dashboard.JobRunrDashboardWebServer;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.storage.StorageProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobRunrStarterTest {

    JobRunrBuildTimeConfiguration jobRunrBuildTimeConfiguration;

    JobRunrBuildTimeConfiguration.BackgroundJobServerConfiguration backgroundJobServerConfiguration;

    JobRunrBuildTimeConfiguration.DashboardConfiguration dashboardConfiguration;

    @Mock
    Instance<BackgroundJobServer> backgroundJobServerInstance;

    @Mock
    BackgroundJobServer backgroundJobServer;

    @Mock
    Instance<JobRunrDashboardWebServer> dashboardWebServerInstance;

    @Mock
    JobRunrDashboardWebServer dashboardWebServer;

    @Mock
    Instance<StorageProvider> storageProviderInstance;

    @Mock
    StorageProvider storageProvider;

    JobRunrStarter jobRunrStarter;

    @BeforeEach
    void setUpJobRunrMetricsStarter() {
        jobRunrBuildTimeConfiguration = new JobRunrBuildTimeConfiguration();
        backgroundJobServerConfiguration = new JobRunrBuildTimeConfiguration.BackgroundJobServerConfiguration();
        dashboardConfiguration = new JobRunrBuildTimeConfiguration.DashboardConfiguration();
        jobRunrBuildTimeConfiguration.backgroundJobServer = backgroundJobServerConfiguration;
        jobRunrBuildTimeConfiguration.dashboard = dashboardConfiguration;

        lenient().when(backgroundJobServerInstance.get()).thenReturn(backgroundJobServer);
        lenient().when(dashboardWebServerInstance.get()).thenReturn(dashboardWebServer);
        lenient().when(storageProviderInstance.get()).thenReturn(storageProvider);

        jobRunrStarter = new JobRunrStarter(jobRunrBuildTimeConfiguration, backgroundJobServerInstance, dashboardWebServerInstance, storageProviderInstance);
    }

    @Test
    void jobRunrStarterDoesNotStartBackgroundJobServerIfNotConfigured() {
        backgroundJobServerConfiguration.enabled = false;

        jobRunrStarter.startup(new StartupEvent());

        verify(backgroundJobServerInstance, never()).get();
    }

    @Test
    void jobRunrStarterStartsBackgroundJobServerIfConfigured() {
        backgroundJobServerConfiguration.enabled = true;

        jobRunrStarter.startup(new StartupEvent());

        verify(backgroundJobServer).start();
    }

    @Test
    void jobRunrStarterDoesNotStartDashboardIfNotConfigured() {
        dashboardConfiguration.enabled = false;

        jobRunrStarter.startup(new StartupEvent());

        verify(dashboardWebServerInstance, never()).get();
    }

    @Test
    void jobRunrStarterStartsDashboardIfConfigured() {
        dashboardConfiguration.enabled = true;

        jobRunrStarter.startup(new StartupEvent());

        verify(dashboardWebServer).start();
    }

    @Test
    void jobRunrStarterDoesNotStopBackgroundJobServerIfNotConfigured() {
        backgroundJobServerConfiguration.enabled = false;

        jobRunrStarter.shutdown(new ShutdownEvent());

        verify(backgroundJobServerInstance, never()).get();
    }

    @Test
    void jobRunrStarterStopsBackgroundJobServerIfConfigured() {
        backgroundJobServerConfiguration.enabled = true;

        jobRunrStarter.shutdown(new ShutdownEvent());

        verify(backgroundJobServer).stop();
    }

    @Test
    void jobRunrStarterDoesNotStopsDashboardIfNotConfigured() {
        dashboardConfiguration.enabled = false;

        jobRunrStarter.shutdown(new ShutdownEvent());

        verify(dashboardWebServerInstance, never()).get();
    }

    @Test
    void jobRunrStarterStopsDashboardIfConfigured() {
        dashboardConfiguration.enabled = true;

        jobRunrStarter.shutdown(new ShutdownEvent());

        verify(dashboardWebServer).stop();
    }

    @Test
    void jobRunrStarterStopsStorageProvider() {
        jobRunrStarter.shutdown(new ShutdownEvent());

        verify(storageProvider).close();
    }

}