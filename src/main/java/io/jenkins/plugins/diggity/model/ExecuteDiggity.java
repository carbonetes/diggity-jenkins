package io.jenkins.plugins.diggity.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteDiggity {
    private int ret;
    private String buildStatus;
    private String assesstmentSummary;

    public ExecuteDiggity(int ret, String buildStatus, String assesstmentSummary) {
        this.ret = ret;
        this.buildStatus = buildStatus;
        this.assesstmentSummary = assesstmentSummary;
    }
}
