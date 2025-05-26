package io.jenkins.plugins.diggity.save;

import hudson.model.Job;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import jenkins.model.Jenkins;

public class FileFormat extends RunListener<Run<?, ?>> {
    private static String fileName;

    @Override
    public void onStarted(Run<?, ?> run, TaskListener listener) {
        String jobName = run.getParent().getFullName();
        int buildNumber = run.getNumber();

        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins != null) {
            // Get the queue
            Queue.Item[] items = jenkins.getQueue().getItems();
            if (items.length > 0) {
                // Get the first item in the queue
                Queue.Item queueItem = items[0];

                // Get the task object associated with the queue item
                Queue.Task task = queueItem.task;
                if (task instanceof Job<?, ?>) {
                    Job<?, ?> queueJob = (Job<?, ?>) task;

                    // Get the curent build number
                    int currentBuildNumber = (int) queueItem.getId();

                    // Get the job name
                    jobName = queueJob.getFullName();

                    // Update the build number
                    buildNumber = currentBuildNumber;
                }
            }

            // Construct the file name using jobName and buildNumber
            String localFileName = "diggity_result_" + jobName + "_" + buildNumber + ".txt";

            setFileName(localFileName);
        }
    }

    private static void setFileName(String fileName) {
        FileFormat.fileName = fileName;
    }
    
    public static String getFileName() {
        return fileName;
    }
}
