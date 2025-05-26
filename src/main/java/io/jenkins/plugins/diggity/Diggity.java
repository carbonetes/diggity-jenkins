package io.jenkins.plugins.diggity;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import io.jenkins.plugins.diggity.install.Clone;
import io.jenkins.plugins.diggity.install.DiggityExist;
import io.jenkins.plugins.diggity.install.Go;
import io.jenkins.plugins.diggity.model.DiggityConfig;
import io.jenkins.plugins.diggity.model.JenkinsConfig;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import lombok.Getter;
import lombok.Setter;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/**
 * @author Sairen Christian Buerano
 * @author Carbonetes
 */
@Getter
@Setter
public class Diggity extends Builder implements SimpleBuildStep {

    private DiggityConfig diggityConfig;
    private String scanDest;
    private String scanName;
    private String skipFail;
    private String token;
    private Map<String, String> content;

    @DataBoundConstructor
    public Diggity(
        String scanDest,
        String scanName,
        String skipFail,
        String token,
        Map<String, String> content,
        DiggityConfig diggityConfig
    ) {
        this.scanDest = scanDest;
        this.scanName = scanName;
        this.skipFail = skipFail;
        this.token = token;
        this.content = content;
        this.diggityConfig = new DiggityConfig(
            scanDest, 
            scanName, 
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
        
        DiggityExist diggityExist = new DiggityExist();
        if (Boolean.FALSE.equals(diggityExist.checkIfExists(jenkinsConfig.getWorkspace()))) {
            Clone.repo(jenkinsConfig);
            Go.install(jenkinsConfig);
        } 
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.HelloWorldBuilder_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_tooShort());
            if (!useFrench && value.matches(".*[éáàç].*")) {
                return FormValidation.warning(Messages.HelloWorldBuilder_DescriptorImpl_warnings_reallyFrench());
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Diggity BOM Analyzer";
        }

    }
}
