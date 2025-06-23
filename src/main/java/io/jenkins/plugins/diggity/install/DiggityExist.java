package io.jenkins.plugins.diggity.install;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import hudson.FilePath;

public class DiggityExist {

    public Boolean checkIfExists(FilePath workspace) {
        CheckVersion checkVersion = new CheckVersion();
        String workspacePath = workspace.getRemote();
        String version = checkVersion.getVersion();
        String fileName = "diggity-" + version + "-Exist.txt";
        String fileContent = "Diggity" + version + " installed on this workspace";

        Boolean fileExists = checkFileExists(workspacePath, fileName);
        if (Boolean.FALSE.equals(fileExists)) {
            try {
                String filePath = workspacePath + File.separator + fileName;
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

    private Boolean checkFileExists(String workspacePath, String fileName) {
        File file = new File(workspacePath, fileName);
        return file.exists() && !file.isDirectory();
    }
}