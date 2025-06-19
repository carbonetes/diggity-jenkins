package io.jenkins.plugins.diggity.install;

import java.io.IOException;

import hudson.FilePath;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

public class InstallBinary {

    public static void installDiggity(JenkinsConfig jenkinsConfig, DiggityConfig diggityConfig)
            throws InterruptedException, IOException {
        // Get the workspace root
        FilePath workspace = jenkinsConfig.getWorkspace();
        String installDir = workspace.getRemote();

        // Install Diggity using the install script
        String[] cmd = {
            "bash",
            "-c",
            "curl -sSfL https://raw.githubusercontent.com/carbonetes/diggity/main/install.sh | sh -s -- -d " + installDir
        };

        int exitCode = jenkinsConfig.getLauncher().launch()
            .cmds(cmd)
            // .stdout(jenkinsConfig.getListener())
            // .stderr(jenkinsConfig.getListener().getLogger())
            .pwd(workspace)
            .join();

        if (exitCode != 0) {
            throw new IOException("Failed to install Diggity binary. Exit code: " + exitCode);
        }

        FilePath diggityBinary = workspace.child("diggity");
        try {
            diggityBinary.chmod(0755);
        } catch (IOException | InterruptedException e) {
            jenkinsConfig.getListener().getLogger().println("Warning: Could not set executable permission on Diggity binary.");
        }

        jenkinsConfig.getListener().getLogger().println("Diggity binary installed successfully to: " + diggityBinary.getRemote());

        setPath(jenkinsConfig, diggityConfig, diggityBinary.getRemote());
    }

    public static void setPath(JenkinsConfig jenkinsConfig, DiggityConfig diggityConfig, String diggityExecutablePath)
            throws IOException, InterruptedException {

        jenkinsConfig.getListener().getLogger().println("Diggity binary path: " + diggityExecutablePath);
    }
}