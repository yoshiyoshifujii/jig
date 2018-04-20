package jig.domain.model.identifier.field;

import jig.domain.model.identifier.type.TypeIdentifier;

public class FieldIdentifier {

    private final TypeIdentifier declaringType;
    String name;
    TypeIdentifier typeIdentifier;

    public FieldIdentifier(TypeIdentifier declaringType, String name, TypeIdentifier typeIdentifier) {
        this.declaringType = declaringType;
        this.name = name;
        this.typeIdentifier = typeIdentifier;
    }

    public TypeIdentifier typeIdentifier() {
        return typeIdentifier;
    }

    public String nameText() {
        return name;
    }

    public String signatureText() {
        return String.format("%s %s", typeIdentifier.asSimpleText(), name);
    }

    public TypeIdentifier declaringType() {
        return declaringType;
    }
}
