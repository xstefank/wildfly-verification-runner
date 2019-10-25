package io.xstefank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.xstefank.model.Stream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@ApplicationScoped
public class BuildProcessor {

    private static final Logger log = Logger.getLogger(BuildProcessor.class);
    
    @ConfigProperty(name = "config", defaultValue = "src/main/resources/test-streams.json")
    String config;
    
    @ConfigProperty(name = "stream", defaultValue = "wildfly")
    String streamName;
    
    public void process() throws IOException, InterruptedException {
        Map<String, Stream> streams = readStreams();

        Path cloneDirectory = Files.createTempDirectory("wildfly-verification-runner");

        Stream stream = streams.get(streamName);

        initialize(cloneDirectory, stream);

        stream.codebases.forEach(codebase -> {
            // TODO clone codebase
            try {
                cloneRepository(codebase.repository_url, cloneDirectory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // TODO build and install codebase with tests
        });

//        Files.delete(cloneDirectory);
    }

    private Map<String, Stream> readStreams() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(Paths.get(config).toFile(), new TypeReference<Map<String, Stream>>() {});
    }

    private void initialize(Path cloneDirectory, Stream stream) throws IOException, InterruptedException {
        // TODO should we also do EAP 6.4.x? then jbossas/jboss-eap
        switch (stream.name) {
            case "wildfly":
                cloneRepository(Constants.WILDFLY_URL, cloneDirectory);
                break;
            case "7.2.x":
                cloneRepository(Constants.JBOSS_EAP7, cloneDirectory);
                break;
        }
    }

    private void cloneRepository(String repositoryURL, Path targetDirectory) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("git", "clone",
            repositoryURL, targetDirectory.resolve(getRepositoryName(repositoryURL)).toAbsolutePath().toString())
            .inheritIO().start();

        int result = process.waitFor();
        if (result != 0) {
            throw new IllegalStateException("Cannot clone repository: " + repositoryURL);
        }
    }

    private String getRepositoryName(String repositoryURL) {
        return repositoryURL.substring(repositoryURL.lastIndexOf("/") + 1);
    }
}
