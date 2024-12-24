package org.jobrunr.server.configuration;

import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.server.concurrent.ConcurrentJobModificationResolver;
import org.jobrunr.server.concurrent.DefaultConcurrentJobModificationResolver;
import org.jobrunr.server.concurrent.UnresolvableConcurrentJobModificationException;

/**
 * Default implementation of {@link ConcurrentJobModificationPolicy}.
 * <p>
 * If Jobs are deleted, the {@link DefaultConcurrentJobModificationPolicy} will resolve the concurrent job modification
 * by stopping the processing of the job. For other concurrent modifications, the {@link DefaultConcurrentJobModificationPolicy} will
 * throw {@link UnresolvableConcurrentJobModificationException} as these may point to programming errors (JobRunr was conceived with the idea that once a
 * job is being processed, it should not be modified anymore).
 */
public class DefaultConcurrentJobModificationPolicy implements ConcurrentJobModificationPolicy {

    @Override
    public ConcurrentJobModificationResolver toConcurrentJobModificationResolver(BackgroundJobServer backgroundJobServer) {
        return new DefaultConcurrentJobModificationResolver(backgroundJobServer);
    }

}
