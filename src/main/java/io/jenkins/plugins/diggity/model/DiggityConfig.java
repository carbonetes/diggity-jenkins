package io.jenkins.plugins.diggity.model;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiggityConfig {
    private String scanDest;
    private String scanName;
    private String scanType;
    private String skipFail;
    private String token;
    private Map<String, String> content;

    public DiggityConfig(
            String scanDest,
            String scanName,
            String scanType,
            String skipFail,
            String token
    ) {
        this.scanDest = scanDest;
        this.scanName = scanName;
        this.scanType = scanType;
        this.skipFail = skipFail;
        this.token = token;
    }
}
