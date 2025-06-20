package io.jenkins.plugins.diggity.model;

import hudson.AbortException;
import hudson.model.TaskListener;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteDiggity {
    private int ret;
    private String buildStatus;
    private String assesstmentSummary;
    private Boolean skipFail;
    private TaskListener listener;

    public ExecuteDiggity(int ret, String buildStatus, String assesstmentSummary, Boolean skipFail, TaskListener listener) throws AbortException {
        this.ret = ret;
        this.buildStatus = buildStatus;
        this.assesstmentSummary = assesstmentSummary;

        if (buildStatus == "failed" && skipFail) {
            listener.getLogger().println("Diggity failed but skipping failure due to configuration.");
        }
        if (buildStatus == "failed" && !skipFail) {
            throw new AbortException("Diggity execution failed");
        }
    }
}
