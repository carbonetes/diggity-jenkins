package io.jenkins.plugins.diggity.scan;

import java.nio.file.Paths;
import java.util.ArrayList;

import hudson.AbortException;
import hudson.model.TaskListener;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.JenkinsConfig;
import io.jenkins.plugins.diggity.save.FileFormat;

public class SetArgs {
    private static final String DIR = "--dir";
    private static final String TAR = "--tar";
    private static final String FILE = "--file";
    private static final String CIMODE = "--ci";
    private static final String TOKEN = "--token";
    private static final String PLUGIN = "--plugin";

    public String[] scanTypeArgs(DiggityConfig diggityConfig, JenkinsConfig jenkinsConfig) throws AbortException {
        if(diggityConfig.getScanType() == null || diggityConfig.getScanType().isEmpty()) {
            jenkinsConfig.getListener().getLogger().println("Scan type cannot be null or empty");
            throw new AbortException("Scan type cannot be null or empty");
        }
        ArrayList<String> cmdArgs = new ArrayList<>();

        // Get the Go binary path
        String workspaceDir = jenkinsConfig.getWorkspace().getRemote();
        String goTmpDir = Paths.get(workspaceDir, "go").toString();
        String goBinaryPath = Paths.get(goTmpDir, "bin", "go").toString();

        if (goBinaryPath == null || goBinaryPath.isEmpty()) {
            throw new IllegalArgumentException("Go binary path is not set.");
        }

        // Add go on command line
        cmdArgs.add(goBinaryPath);
        cmdArgs.add("run");
        cmdArgs.add(".");

        // Scan type-specific arguments
        switch (diggityConfig.getScanType()) {
            case "image":
                cmdArgs.add(diggityConfig.getScanName());
                break;
            case "dir":
                cmdArgs.add(DIR);
                cmdArgs.add(diggityConfig.getScanName());
                break;
            case "tar":
                cmdArgs.add(TAR);
                cmdArgs.add(diggityConfig.getScanName());
                break;
            case "file":
                cmdArgs.add(FILE);
                cmdArgs.add(diggityConfig.getScanName());
                break;
            default:
                jenkinsConfig.getListener().getLogger().println("Invalid scan type: " + diggityConfig.getScanType());
                throw new AbortException("Invalid scan type: " + diggityConfig.getScanType());
        }

        // Add standard flags
        cmdArgs.add(CIMODE);
        cmdArgs.add(TOKEN);
        cmdArgs.add(diggityConfig.getToken());
        cmdArgs.add(PLUGIN);
        cmdArgs.add("jenkins");

        // Output File
        cmdArgs.add(FILE);
        cmdArgs.add(FileFormat.getFileName());

        return cmdArgs.toArray(new String[0]);
    }
}