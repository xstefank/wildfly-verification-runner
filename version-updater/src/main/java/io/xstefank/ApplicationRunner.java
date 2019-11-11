package io.xstefank;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ApplicationRunner {

    private static final Logger log = Logger.getLogger(ApplicationRunner.class);

    @Inject
    VersionUpdater versionUpdater;
    
    @ConfigProperty(name = "versions")
    String versionsProperty;
    
    @ConfigProperty(name = "pom", defaultValue = "pom.xml")
    String pomFile;

    public void observeStart(@Observes StartupEvent event) {
        log.error("XXXXXXXXXXXXXXXXX Versions updater");

        new Thread(() -> {
            try {
                versionUpdater.process(versionsProperty, pomFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }).start();
        
    }
}
