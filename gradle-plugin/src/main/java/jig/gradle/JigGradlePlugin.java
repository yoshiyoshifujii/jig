package jig.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

public class JigGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ExtensionContainer extensions = project.getExtensions();
        extensions.create("jigListConfig", JigListExtension.class);
        extensions.create("jigPackageDiagramConfig", JigPackageDiagramExtension.class);
        TaskContainer tasks = project.getTasks();
        tasks.create("jigList", JigListTask.class);
        tasks.create("jigPackageDiagram", JigPackageDiagramTask.class);
    }

}
