package io.jenkins.plugins.diggity.arguments;

import java.nio.file.Paths;
import java.util.ArrayList;

import hudson.AbortException;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

public class SetArguments {
    
    // Binary: Carbonetes-CI Command Flags
    // ANALYZER
    private static final String ANALYZER = "--analyzer";
    private static final String INPUT = "--input";
    private static final String DIGGITY = "diggity";
    private static final String SCANTYPE = "--scan-type";
    private static final String SKIPFAIL = "--skip-fail";
    // API
    private static final String TOKEN = "--token";
    private static final String PLUGIN = "--plugin-type";
    private static final String ENVIRONMENT = "--environment-type";

    public String[] scanTypeArgs(DiggityConfig diggityConfig, JenkinsConfig jenkinsConfig) throws AbortException {

        ArrayList<String> cmdArgs = new ArrayList<>();
        
        String workspaceDir = jenkinsConfig.getWorkspace().getRemote(); // /home/sairen/.jenkins/workspace/<item-name>
        String binaryPath = Paths.get(workspaceDir, "carbonetes-ci").toString();
        String CarbonetesCI = binaryPath;

        String SCANTYPEVALUE = diggityConfig.getScanType() != null ? diggityConfig.getScanType() : "";
        String INPUTVALUE = diggityConfig.getScanName() != null ? diggityConfig.getScanName() : "";
        String TOKENINPUT = diggityConfig.getToken() != null ? diggityConfig.getToken() : "";

        // ANALYZER
        cmdArgs.add(CarbonetesCI);
        cmdArgs.add(ANALYZER);
        cmdArgs.add(DIGGITY);

        cmdArgs.add(INPUT);
        cmdArgs.add(INPUTVALUE);

        cmdArgs.add(SCANTYPE);
        cmdArgs.add(SCANTYPEVALUE);

        // API
        cmdArgs.add(TOKEN);
        cmdArgs.add(TOKENINPUT);

        cmdArgs.add(PLUGIN);
        cmdArgs.add("jenkins");

        cmdArgs.add(ENVIRONMENT);
        cmdArgs.add("test");

        if (diggityConfig.getSkipFail()) {
            cmdArgs.add(SKIPFAIL);
        } else {
            diggityConfig.setSkipFail(false);
        }

        return cmdArgs.toArray(new String[0]);
    }
}