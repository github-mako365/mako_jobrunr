package org.jobrunr.dashboard.server.sse;

import com.sun.net.httpserver.HttpExchange;
import org.jobrunr.dashboard.server.AbstractHttpExchangeHandler;

import java.io.IOException;
import java.util.Set;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

public abstract class ServerSentEventHandler extends AbstractHttpExchangeHandler {

    private final String contextPath;
    private final Set<SseExchange> sseExchanges;

    protected ServerSentEventHandler() {
        this("/sse");
    }

    protected ServerSentEventHandler(String contextPath) {
        this.contextPath = contextPath;
        this.sseExchanges = newKeySet();
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        sseExchanges.removeIf(SseExchange::isClosed);
        if (sseExchanges.size() > 40) {
            httpExchange.sendResponseHeaders(417, 0);
            return;
        }
        sseExchanges.add(createSseExchange(httpExchange));
    }

    protected abstract SseExchange createSseExchange(HttpExchange httpExchange) throws IOException;

    @Override
    public void close() {
        sseExchanges.forEach(SseExchange::close);
    }
}