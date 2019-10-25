package io.xstefank;

import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class ApplicationRunner {

    private static final Logger LOG = Logger.getLogger(ApplicationRunner.class);
    
    @Inject
    BuildProcessor buildProcessor;

    public void observeStart(@Observes StartupEvent event) throws IOException {
        LOG.error("Startup event call");

        new Thread(() -> {
            try {
                buildProcessor.process();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }).start();
    }
}

