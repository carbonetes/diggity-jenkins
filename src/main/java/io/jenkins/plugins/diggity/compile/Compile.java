package io.jenkins.plugins.diggity.compile;

import java.io.IOException;


import hudson.AbortException;
import hudson.model.TaskListener;
import io.jenkins.plugins.diggity.arguments.SetArguments;
import io.jenkins.plugins.diggity.execute.Execute;
import io.jenkins.plugins.diggity.model.BuildConfig;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

public class Compile {
    
    public void compileArgs(JenkinsConfig jenkinsConfig, DiggityConfig diggityConfig) throws InterruptedException, IOException {

        TaskListener listener = jenkinsConfig.getListener();
        listener.getLogger().println("Compilling Commands...");

        SetArguments setArgs = new SetArguments();
        Execute execute = new Execute();

        if (diggityConfig.getScanName() != null && !diggityConfig.getScanName().equals("")) {
            // Compile arguments based on the user-inputs
            String[] cmdArgs = setArgs.scanTypeArgs(diggityConfig, jenkinsConfig);

            // Execute compiled arguments for Bill of Materials (BOM) Scanning and return build status fo Jenkins Build.
            BuildConfig diggityExecute = execute.binary(cmdArgs, jenkinsConfig, diggityConfig);

            // Set Build Status as content of JSON File
            String buildStatus = diggityExecute.getBuildStatus();
            buildFailFilter(buildStatus, jenkinsConfig.getListener());
        } else {
            jenkinsConfig.getListener().getLogger().println("Please input your scan name");
            throw new AbortException("Scan name cannot be null or empty");
        }
    }

    public void buildFailFilter(String buildStatus, TaskListener listener) throws AbortException {
        if ("failed".equals(buildStatus)) {
            throw new AbortException("Build Failed");
        }
    }
}
