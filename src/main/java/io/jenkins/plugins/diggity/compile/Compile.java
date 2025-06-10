package io.jenkins.plugins.diggity.compile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.diggity.execute.ExecuteBinary;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.ExecuteDiggity;
import io.jenkins.plugins.diggity.model.JenkinsConfig;
import io.jenkins.plugins.diggity.scan.SetArgs;
import net.sf.json.JSONObject;

public class Compile {
    
    public void compileArgs(JenkinsConfig jenkinsConfig, DiggityConfig diggityConfig) throws InterruptedException, IOException {

        TaskListener listener = jenkinsConfig.getListener();
        listener.getLogger().println("Compiling Diggity with provided arguments...");

        SetArgs setArgs = new SetArgs();
        ExecuteBinary executeBinary = new ExecuteBinary();

        if (diggityConfig.getScanName() != null && !diggityConfig.getScanName().equals("")) {
            // Compile arguments based on the user-inputs
            String[] cmdArgs = setArgs.scanTypeArgs(diggityConfig, jenkinsConfig);

            // Execute compiled arguments for Bill of Materials (BOM) Scanning and return build status fo Jenkins Build.
            ExecuteDiggity diggityExecute = executeBinary.executeDiggity(cmdArgs, jenkinsConfig, diggityConfig);

            // Set Build Status as content of JSON File
            String buildStatus = diggityExecute.getBuildStatus();
            setBuildStatusContent(buildStatus, diggityConfig, diggityExecute.getAssesstmentSummary());
        } else {
            jenkinsConfig.getListener().getLogger().println("Please input your scan name");
            throw new AbortException("Scan name cannot be null or empty");
        }
    }

    public void setBuildStatusContent(String buildStatus, DiggityConfig diggityConfig, String assessmentSummary) {
        String status = "passed";
        if (Boolean.FALSE.equals(diggityConfig.getSkipFail())) {
            status = buildStatus;
        }
        Map<String, String> keyValuePair = new HashMap<>();
        keyValuePair.put("buildStatus", status);
        keyValuePair.put("diggityAssessment", status);
        if (assessmentSummary != null) {
            keyValuePair.put("assessmentSummary", assessmentSummary);
        } else {
            keyValuePair.put("assessmentSummary", status);
        }
        keyValuePair.put("scanType", diggityConfig.getScanType());
        keyValuePair.put("scanName", diggityConfig.getScanName());

        diggityConfig.setContent(keyValuePair);;
    }

    public void generateJSON(JenkinsConfig jenkinsConfig, DiggityConfig diggityConfig) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();

        // Put keyvalue pairs inside the json content.
        for (Map.Entry<String, String> entry : diggityConfig.getContent().entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }

        // JSON File Saving
        FilePath diggityTmpDir = jenkinsConfig.getWorkspace().child("diggityTmpDir");
        try {
            diggityTmpDir.mkdirs();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Set the destination file path for diggity_file.json
        FilePath destFile = diggityTmpDir.child("diggity_file.json");
        try {
            destFile.write(json.toString(), "UTF-8");
        } catch (IOException e) {
            jenkinsConfig.getListener().getLogger().println("Failed to save JSON file: " + e.getMessage());
            throw e;
        } 
    }
}
