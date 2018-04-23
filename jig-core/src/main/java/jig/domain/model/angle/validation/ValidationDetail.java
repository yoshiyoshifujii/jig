package jig.domain.model.angle.validation;

import jig.domain.model.declaration.annotation.AnnotationDescription;
import jig.domain.model.declaration.annotation.ValidationAnnotationDeclaration;
import jig.domain.model.identifier.type.TypeIdentifier;
import jig.domain.model.identifier.type.TypeIdentifierFormatter;
import jig.domain.model.japanese.JapaneseName;

public class ValidationDetail {

    private final ValidationAnnotationDeclaration annotationDeclaration;
    private final JapaneseName japaneseName;
    private final TypeIdentifierFormatter typeIdentifierFormatter;

    public ValidationDetail(ValidationAnnotationDeclaration annotationDeclaration, JapaneseName japaneseName, TypeIdentifierFormatter typeIdentifierFormatter) {
        this.annotationDeclaration = annotationDeclaration;
        this.japaneseName = japaneseName;
        this.typeIdentifierFormatter = typeIdentifierFormatter;
    }

    public String declaringTypeName() {
        return annotationDeclaration.declaringType().format(typeIdentifierFormatter);
    }

    public JapaneseName japaneseName() {
        return japaneseName;
    }

    public String annotateSimpleName() {
        return annotationDeclaration.annotateSimpleName();
    }

    public TypeIdentifier annotationType() {
        return annotationDeclaration.annotationType();
    }

    public AnnotationDescription description() {
        return annotationDeclaration.annotationDescription();
    }
}