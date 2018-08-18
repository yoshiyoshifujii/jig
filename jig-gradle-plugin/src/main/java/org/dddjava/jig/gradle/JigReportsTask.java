package org.dddjava.jig.gradle;

import org.dddjava.jig.application.service.ImplementationService;
import org.dddjava.jig.domain.basic.ClassFindFailException;
import org.dddjava.jig.domain.basic.Warning;
import org.dddjava.jig.domain.model.implementation.ProjectData;
import org.dddjava.jig.infrastructure.LocalProject;
import org.dddjava.jig.infrastructure.configuration.Configuration;
import org.dddjava.jig.infrastructure.configuration.JigProperties;
import org.dddjava.jig.presentation.view.handler.JigDocumentHandlers;
import org.dddjava.jig.presentation.view.JigDocument;
import org.gradle.api.DefaultTask;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JigReportsTask extends DefaultTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(JigReportsTask.class);

    @TaskAction
    void outputReports() {
        ExtensionContainer extensions = getProject().getExtensions();
        JigConfig config = extensions.findByType(JigConfig.class);

        JigProperties jigProperties = config.asProperties();
        GradleProjects layout = new GradleProject(getProject()).allDependencyJavaProjects();
        JigConfigurationContext configurationContext = new JigConfigurationContext(config);
        Configuration configuration = new Configuration(layout, jigProperties, configurationContext);

        JigDocumentHandlers jigDocumentHandlers = configuration.documentHandlers();

        List<JigDocument> jigDocuments = config.documentTypes();

        LocalProject localProject = configuration.localProject();
        ImplementationService implementationService = configuration.implementationService();
        Path outputDirectory = Paths.get(config.getOutputDirectory() + "/" + getProject().getName());

        long startTime = System.currentTimeMillis();

        LOGGER.info("プロジェクト情報の取り込みをはじめます");
        try {
            ProjectData projectData = implementationService.readProjectData(localProject);

            for (JigDocument jigDocument : jigDocuments) {
                jigDocumentHandlers.handle(jigDocument, projectData, outputDirectory);
            }
        } catch(ClassFindFailException e) {
            LOGGER.info(Warning.クラス検出異常.with(configurationContext));
        }

        LOGGER.info("合計時間: {} ms", System.currentTimeMillis() - startTime);
    }
}
