package org.dddjava.jig.domain.model.declaration.annotation;

import org.dddjava.jig.domain.model.declaration.field.FieldDeclaration;
import org.dddjava.jig.domain.model.declaration.type.TypeIdentifier;

/**
 * フィールドにつけられたアノテーション
 */
public class FieldAnnotation {

    final Annotation annotation;
    final FieldDeclaration fieldDeclaration;

    public FieldAnnotation(Annotation annotation, FieldDeclaration fieldDeclaration) {
        this.annotation = annotation;
        this.fieldDeclaration = fieldDeclaration;
    }

    public FieldDeclaration fieldDeclaration() {
        return fieldDeclaration;
    }

    public TypeIdentifier annotationType() {
        return annotation.annotationType;
    }

    public AnnotationDescription description() {
        return annotation.description;
    }
}
