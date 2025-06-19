package io.jenkins.plugins.diggity.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import hudson.FilePath;

public class DiggityExist {

    public Boolean checkIfExists(FilePath workspace) {
        String workspacePath = workspace.getRemote();
        String version = "v1.17.1";
        String filename = "diggity-" + version + "-exist.txt";
        String fileContent = "Diggity" + version + " installed on this workspace";
        Boolean fileExists = checkFileExists(workspacePath, filename);
        if (Boolean.FALSE.equals(fileExists)) {
            try {
                String filePath = workspacePath + File.separator + filename;
                try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
                    writer.write(fileContent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }

    }

    private Boolean checkFileExists(String workspacePath, String filename) {
        File file = new File(workspacePath, filename);
        return file.exists() && !file.isDirectory();
    }
    
}
