package io.jenkins.plugins.diggity;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.Permission;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import io.jenkins.plugins.diggity.compile.Compile;
import io.jenkins.plugins.diggity.install.CarbonetesCI;
import io.jenkins.plugins.diggity.install.Exist;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;


import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.verb.POST;

/**
 * @author Sairen Christian Buerano
 * @author Carbonetes
 */
@Getter
@Setter
public class Diggity extends Builder implements SimpleBuildStep {

    private String scanDest;
    private String scanType;
    private String scanName;
    private Boolean skipFail;
    private String token;
    private Map<String, String> content;
    private DiggityConfig diggityConfig;

    @DataBoundConstructor
    public Diggity(
        String scanDest,
        String scanType,
        String scanName,
        Boolean skipFail,
        String token,
        Map<String, String> content,
        DiggityConfig diggityConfig
    ) {
        this.scanDest = scanDest;
        this.scanType = scanType;
        this.scanName = scanName;
        this.skipFail = skipFail;
        this.token = token;
        this.content = content;
        this.diggityConfig = new DiggityConfig(
            scanDest, 
            scanName, 
            scanType,
            skipFail, 
            token
        );
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        
        // Initiate Jenkins and Input Config Model
        JenkinsConfig jenkinsConfig = new JenkinsConfig(run, workspace, env, launcher, listener);
        clone(jenkinsConfig);
        
            
    }

    public void clone(JenkinsConfig jenkinsConfig) throws IOException, InterruptedException {
        
        Exist diggityExist = new Exist();
        if (Boolean.FALSE.equals(diggityExist.checkIfExists(jenkinsConfig.getWorkspace()))) {
            CarbonetesCI.install(jenkinsConfig, diggityConfig);
        } 
        Compile compileArgs = new Compile();
        compileArgs.compileArgs(jenkinsConfig, diggityConfig);
    }

    @Symbol("diggity")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            super(Diggity.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Diggity BOM Analyzer";
        }

        @POST
        public ListBoxModel doFillScanTypeItems() throws AccessDeniedException {
            Jenkins jenkins = Jenkins.get();
            if (!jenkins.hasPermission(Permission.CONFIGURE)) {
                throw new AccessDeniedException("You do not have permission to configure this.");
            }
            return new ListBoxModel(
                new Option("-- Select --", ""),
                new Option("Image", "image"),
                new Option("File System", "filesystem"),
                new Option("Tar Ball", "tarball")
            );
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }
}
