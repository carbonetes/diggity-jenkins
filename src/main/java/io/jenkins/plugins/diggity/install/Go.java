package io.jenkins.plugins.diggity.install;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import hudson.AbortException;
import hudson.model.TaskListener;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

public class Go {

    public static void install(JenkinsConfig jenkinsConfig) throws AbortException {
        TaskListener listener = jenkinsConfig.getListener();
        String workspaceDir = jenkinsConfig.getWorkspace().getRemote();
        String goTmpDir = workspaceDir;
        goTmpDir = goTmpDir.replace(" ", "_");

        String goURL = "https://go.dev/dl/go1.22.4.linux-amd64.tar.gz"; // Uses 1.22.4 Go Version
        String downloadPath = goTmpDir + File.separator + "go.tar.gz";
        String extractDir = goTmpDir; // final go path = goTmpDir/go

        try {
            // 1. Ensure the target directory exists
            Path goTmpDirPath = Paths.get(extractDir);
            Files.createDirectories(goTmpDirPath);

            // 2. Download Go tar.gz
            listener.getLogger().println("Downloading Go...");
            try (InputStream in = new URL(goURL).openStream()) {
                Files.copy(in, Paths.get(downloadPath));
            }

            // 3. Extract the tar.gz
            listener.getLogger().println("Extracting Go...");
            ProcessBuilder extract = new ProcessBuilder("tar", "-C", goTmpDir, "-xzf", downloadPath);
            extract.redirectErrorStream(true);
            Process process = extract.start();
            process.waitFor();

            // 4. Remove the tar.gz to save space
            Files.deleteIfExists(Paths.get(downloadPath));
        } catch (Exception e) {
            listener.getLogger().println("Failed to install Go: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
            throw new AbortException("Build failed");
        }
    }
    
}
