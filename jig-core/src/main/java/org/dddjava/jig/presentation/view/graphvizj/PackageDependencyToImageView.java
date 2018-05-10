package org.dddjava.jig.presentation.view.graphvizj;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.dddjava.jig.domain.model.DocumentType;
import org.dddjava.jig.domain.model.identifier.namespace.PackageIdentifierFormatter;
import org.dddjava.jig.domain.model.japanese.JapaneseName;
import org.dddjava.jig.domain.model.japanese.JapaneseNameRepository;
import org.dddjava.jig.domain.model.relation.dependency.PackageDependencies;
import org.dddjava.jig.presentation.view.local.AbstractLocalView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringJoiner;

import static java.util.stream.Collectors.joining;

public class PackageDependencyToImageView extends AbstractLocalView {

    private final PackageDependencies packageDependencies;
    final PackageIdentifierFormatter formatter;
    final JapaneseNameRepository repository;

    public PackageDependencyToImageView(PackageDependencies packageDependencies, PackageIdentifierFormatter formatter, JapaneseNameRepository repository) {
        super(DocumentType.PackageDependency.fileName());
        this.packageDependencies = packageDependencies;
        this.formatter = formatter;
        this.repository = repository;
    }

    @Override
    protected void write(OutputStream outputStream) {
        try {
            PackageIdentifierFormatter doubleQuote = value -> "\"" + value + "\"";

            String dependenciesText = packageDependencies.list().stream()
                    .map(item -> String.format("%s -> %s;",
                            item.from().format(doubleQuote),
                            item.to().format(doubleQuote)))
                    .collect(joining("\n"));

            String labelsText = packageDependencies.allPackages().stream()
                    .map(packageIdentifier -> {
                        String labelText = packageIdentifier.format(formatter);
                        if (repository.exists(packageIdentifier)) {
                            JapaneseName japaneseName = repository.get(packageIdentifier);
                            labelText = japaneseName.summarySentence() + "\\n" + labelText;
                        }

                        return String.format("%s [label=%s];",
                                packageIdentifier.format(doubleQuote),
                                doubleQuote.format(labelText));
                    })
                    .collect(joining("\n"));

            Graphviz.fromString(
                    new StringJoiner("\n", "digraph {", "}")
                            .add("node [shape=box,style=filled,color=lightgoldenrod];")
                            .add(dependenciesText)
                            .add(labelsText)
                            .toString())
                    .render(Format.PNG)
                    .toOutputStream(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
