package io.jenkins.plugins.diggity.install;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import hudson.FilePath;
import hudson.model.TaskListener;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

public class Clone {
    public static void repo(JenkinsConfig jenkinsConfig) throws IOException, InterruptedException {
        TaskListener listener = jenkinsConfig.getListener();
        FilePath diggityTmpDir = jenkinsConfig.getWorkspace().child("diggityTmpDir");
        diggityTmpDir.mkdirs();
        FilePath destDir = diggityTmpDir.child("diggity");
        
        try {
            if (destDir.exists()) {
                listener.getLogger().println("Cleaning up existing Diggity directory...");
                destDir.deleteRecursive();   
            }

            listener.getLogger().println("Cloning Diggity repository..."); // v1.14.3-ci

            // Clone the repository without checkout
            Git.cloneRepository()
            .setURI("https://github.com/carbonetes/diggity.git")
            .setDirectory(new File(destDir.getRemote()))
            .setBranch("refs/tags/v1.14.3-ci")
            .call();

            listener.getLogger().println("Diggity repository cloned successfully to: " + destDir.getRemote());
        } catch (GitAPIException e) {
            listener.getLogger().println("Error during repository clone or checkout: " + e.getMessage());
            e.printStackTrace(listener.getLogger());
            throw new IOException("Build failed", e);
        }
    }
    
}
