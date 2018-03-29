package jig.infrastructure;

import jig.domain.model.datasource.SqlSources;
import jig.domain.model.project.ProjectLocation;
import jig.domain.model.specification.SpecificationSource;
import jig.domain.model.specification.SpecificationSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JigPaths {

    private static final Logger LOGGER = LoggerFactory.getLogger(JigPaths.class);

    Path classesDirectory;
    Path resourcesDirectory;
    Path sourcesDirectory;

    public JigPaths(@Value("${directory.classes:build/classes/java/main}") String classesDirectory,
                    @Value("${directory.resources:build/resources/main}") String resourcesDirectory,
                    @Value("${directory.sources:src/main/java}") String sourcesDirectory) {
        LOGGER.info("classes suffix  : {}", classesDirectory);
        LOGGER.info("resources suffix: {}", resourcesDirectory);
        LOGGER.info("sources suffix  : {}", sourcesDirectory);

        this.classesDirectory = Paths.get(classesDirectory);
        this.resourcesDirectory = Paths.get(resourcesDirectory);
        this.sourcesDirectory = Paths.get(sourcesDirectory);
    }

    public SpecificationSources getSpecificationSources(ProjectLocation location) {
        ArrayList<SpecificationSource> sources = new ArrayList<>();
        try {
            for (Path path : extractClassPath(location)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (isClassFile(file)) {
                            SpecificationSource specificationSource = new SpecificationSource(file);
                            sources.add(specificationSource);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        LOGGER.info("*.class: {}件", sources.size());
        return new SpecificationSources(sources);
    }

    private Path[] extractClassPath(ProjectLocation location) {
        try (Stream<Path> walk = Files.walk(location.toPath())) {
            return walk
                    .filter(Files::isDirectory)
                    .filter(path -> path.endsWith(classesDirectory) || path.endsWith(resourcesDirectory))
                    .peek(path -> LOGGER.info("classes: {}", path))
                    .toArray(Path[]::new);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isClassFile(Path path) {
        return path.toString().endsWith(".class");
    }

    public List<Path> sourcePaths(ProjectLocation location) {
        List<Path> paths = pathsOf(location, this::isJavaFile);
        LOGGER.info("*.java: {}件", paths.size());
        return paths;
    }

    public List<Path> packageInfoPaths(ProjectLocation location) {
        List<Path> paths = pathsOf(location, this::isPackageInfoFile);
        LOGGER.info("package-info.java: {}件", paths.size());
        return paths;
    }

    private List<Path> pathsOf(ProjectLocation location, Predicate<Path> condition) {
        try {
            List<Path> paths = new ArrayList<>();
            for (Path path : extractSourcePath(location)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (condition.test(file)) paths.add(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            return paths;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Path[] extractSourcePath(ProjectLocation location) {
        try (Stream<Path> walk = Files.walk(location.toPath())) {
            return walk.filter(Files::isDirectory)
                    .filter(path -> path.endsWith(sourcesDirectory))
                    .peek(path -> LOGGER.info("sources: {}", path))
                    .toArray(Path[]::new);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isJavaFile(Path path) {
        return path.toString().endsWith(".java");
    }

    private boolean isPackageInfoFile(Path path) {
        return path.toString().endsWith("package-info.java");
    }

    public SqlSources getSqlSources(ProjectLocation projectLocation) {
        try {
            Path[] array = extractClassPath(projectLocation);

            URL[] urls = new URL[array.length];
            List<String> classNames = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                Path path = array[i];
                urls[i] = path.toUri().toURL();

                try (Stream<Path> walk = Files.walk(path)) {
                    List<String> collect = walk.filter(p -> p.toFile().isFile())
                            .map(path::relativize)
                            .filter(this::isMapperClassFile)
                            .map(this::toClassName)
                            .collect(Collectors.toList());
                    classNames.addAll(collect);
                }
            }

            LOGGER.info("*Mapper.class: {}件", classNames.size());
            return new SqlSources(urls, classNames);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isMapperClassFile(Path path) {
        return path.toString().endsWith("Mapper.class");
    }

    private String toClassName(Path path) {
        String pathStr = path.toString();
        return pathStr.substring(0, pathStr.length() - 6).replace(File.separatorChar, '.');
    }
}
