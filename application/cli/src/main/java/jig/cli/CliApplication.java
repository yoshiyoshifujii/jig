package jig.cli;

import jig.analizer.dependency.ModelFormatter;
import jig.analizer.dependency.Models;
import jig.application.service.AnalyzeService;
import jig.application.service.DiagramService;
import jig.domain.model.Diagram;
import jig.domain.model.DiagramIdentifier;
import jig.domain.model.DiagramSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(scanBasePackages = "jig")
public class CliApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CliApplication.class, args);
    }

    @Autowired
    AnalyzeService analyzeService;
    @Autowired
    DiagramService diagramService;

    @Override
    public void run(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("usage: cli.jar <options> <jar files...>");
            System.out.println("jarファイルは1つ以上指定してください。");
            System.out.println("  -source  ソースコードの含まれているディレクトリを指定します。");
            System.out.println("           デフォルトは ./src です。");
            System.out.println("  -output  出力ファイル名を指定します。");
            System.out.println("           デフォルトは ./diagram.png です。");
            return;
        }

        List<Path> jarPaths = new ArrayList<>();
        Path sourceRoot = Paths.get("./src");
        Path output = Paths.get("./diagram.png");

        ParameterType parameterType = ParameterType.NONE;
        for (String arg : args) {
            if (arg.equals("-jar")) {
                parameterType = ParameterType.JAR;
                continue;
            }
            if (arg.equals("-source")) {
                parameterType = ParameterType.SOURCE;
                continue;
            }
            if (arg.equals("-output")) {
                parameterType = ParameterType.OUTPUT;
                continue;
            }

            switch (parameterType) {
                case NONE:
                case JAR:
                    Path jarPath = Paths.get(arg);
                    if (Files.notExists(jarPath) || Files.isDirectory(jarPath)) {
                        throw new IllegalArgumentException("存在するjarファイルを指定してください");
                    }
                    jarPaths.add(jarPath);
                    continue;
                case SOURCE:
                    sourceRoot = Paths.get(arg);
                    if (!Files.isDirectory(sourceRoot)) {
                        throw new IllegalArgumentException("-sourceはディレクトリを指定してください");
                    }
                    parameterType = ParameterType.NONE;
                    continue;
                case OUTPUT:
                    output = Paths.get(arg);
                    parameterType = ParameterType.NONE;
                    continue;
            }

            System.err.println("ignore:" + arg);
        }

        if (jarPaths.isEmpty()) {
            throw new IllegalArgumentException("jarファイルを一つ以上指定してください");
        }

        Models models = analyzeService.toModels(jarPaths);
        ModelFormatter modelFormatter = analyzeService.modelFormatter(sourceRoot);
        DiagramSource diagramSource = analyzeService.toDiagramSource(models, modelFormatter);
        DiagramIdentifier identifier = diagramService.request(diagramSource);
        diagramService.generate(identifier);
        Diagram diagram = diagramService.get(identifier);

        try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(output))) {
            outputStream.write(diagram.getBytes());
        }
    }
}

enum ParameterType {
    JAR,
    SOURCE,
    OUTPUT,
    NONE
}