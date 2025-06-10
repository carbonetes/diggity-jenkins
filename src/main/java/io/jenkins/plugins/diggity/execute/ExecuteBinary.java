package io.jenkins.plugins.diggity.execute;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import hudson.AbortException;

import io.jenkins.cli.shaded.org.apache.commons.io.output.ByteArrayOutputStream;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.ExecuteDiggity;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

public class ExecuteBinary {
    
    public ExecuteDiggity executeDiggity(String[] cmd, JenkinsConfig jenkinsConfig, DiggityConfig diggityConfig) throws InterruptedException, IOException {
        
        String buildStatus = null;
        String assesstmentSummary = null;

        if (cmd == null || cmd.length == 0 || containsNull(cmd)) {
            throw new IllegalArgumentException("Command cannot be null or empty");
        }

        // Log comman fo debugging purporses
        jenkinsConfig.getListener().getLogger().println("Analyzing...");

        ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
        int ret = jenkinsConfig.getLauncher().launch()
                .cmds(cmd)
                .stdout(stdoutStream)
                .stderr(stderrStream)
                .pwd(jenkinsConfig.getWorkspace().child("diggityTmpDir").child("diggity"))
                .join();
        String stdout = new String(stdoutStream.toByteArray(), StandardCharsets.UTF_8);
        String stderr = new String(stderrStream.toByteArray(), StandardCharsets.UTF_8);
        
        jenkinsConfig.getListener().getLogger().print(stdout);
        jenkinsConfig.getListener().getLogger().print(stderr);
        
        if(stdout.toLowerCase().contains("Token is required.") && (diggityConfig.getToken() == null) || diggityConfig.getToken().isEmpty()) {
            throw new AbortException(stdout);
        }
        // Extract the line containing 'failed'
        if (ret != 0 || Boolean.FALSE.equals(diggityConfig.getSkipFail()) &&
            (stdout.toLowerCase().contains("failed") || stderr.toLowerCase().contains("failed") ||
            stdout.toLowerCase().contains("error") || stderr.toLowerCase().contains("error") ||
            stdout.toLowerCase().contains("404") || stderr.toLowerCase().contains("404") || 
            stdout.toLowerCase().contains("status code") || stderr.toLowerCase().contains("status code"))) {
                
                buildStatus = "failed";

                String[] lines = stdout.split("\\r?\\n");
                for (String line : lines) {
                    if (line.toLowerCase().contains("failed") || 
                        line.toLowerCase().contains("error") ||
                        line.toLowerCase().contains("404") || 
                        line.toLowerCase().contains("status code")) {
                            assesstmentSummary = line;
                            break;
                    }
                }

                if (assesstmentSummary == null) {
                    lines = stderr.split("\\r?\\n");
                    for (String line : lines) {
                        if (line.toLowerCase().contains("failed") || 
                            line.toLowerCase().contains("error") ||
                            line.toLowerCase().contains("404") || 
                            line.toLowerCase().contains("status code")) {

                                assesstmentSummary = line;
                                break;
                        }
                    }
                } 

            } else {
                buildStatus = "passed";
            }

        return new ExecuteDiggity(ret, buildStatus, assesstmentSummary, diggityConfig.getSkipFail(), jenkinsConfig.getListener());

    }

    private boolean containsNull(String[] cmd) {
        for (String c : cmd) {
            if (c == null) return true;
        }
        return false;
    }
}
