package io.xstefank;

import io.xstefank.client.VersionClient;
import io.xstefank.model.VersionDefinition;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VersionUpdater {
    
    private Jsonb jsonb = JsonbBuilder.create();

    @ConfigProperty(name = "authorization")
    Optional<String> authorization;
    
    public void process(String versionProperty, String pomFileName) throws IOException, InterruptedException {
        String versionsContent = getVersionContent(versionProperty);
        List<VersionDefinition> versions = jsonb.fromJson(versionsContent, 
            new ArrayList<VersionDefinition>() {}.getClass().getGenericSuperclass());

        File pomXml = new File(pomFileName);
        Files.copy(pomXml.toPath(), new File(pomFileName + ".backup").toPath());
        
        for (VersionDefinition version : versions) {
            replaceVersion(version, pomXml);
        }

    }

    private String getVersionContent(String versionFileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(versionFileName)));
    }

    private void replaceVersion(VersionDefinition version, File pomXml) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        String property = version.getProperty();
        String s = String.format("sed -i 's#<%s>\\([^<][^<]*\\)</%s>", property, property)
            + String.format("#<%s>%s</%s>#' %s", property, getVersionString(version.getVersion()), property, pomXml.getAbsolutePath());
        processBuilder.command("bash", "-c", s);

        Process process = processBuilder.start();
        int i = process.waitFor();
    }

    private String getVersionString(String versionURL) {

        VersionClient versionClient = RestClientBuilder.newBuilder()
            .baseUri(URI.create(versionURL))
            .build(VersionClient.class);

        String authorizationHeader = authorization.orElse(null);

        String version = versionClient.getVersion(authorizationHeader);

        return version.replace("\n", "");
    }
}
