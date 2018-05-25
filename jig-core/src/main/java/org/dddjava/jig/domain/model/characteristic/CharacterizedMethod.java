package org.dddjava.jig.domain.model.characteristic;

import org.dddjava.jig.domain.model.declaration.method.MethodDeclaration;
import org.dddjava.jig.domain.model.implementation.bytecode.MethodImplementation;

public class CharacterizedMethod {

    private final MethodImplementation methodImplementation;

    public CharacterizedMethod(MethodImplementation methodImplementation) {
        this.methodImplementation = methodImplementation;
    }

    public MethodDeclaration methodDeclaration() {
        return methodImplementation.methodDeclaration;
    }

    public boolean hasDecision() {
        return methodImplementation.hasDecision();
    }

    public boolean has(MethodCharacteristic methodCharacteristic, TypeCharacteristics typeCharacteristics) {
        switch (methodCharacteristic) {
            case HAS_DECISION:
                return hasDecision();
            case SERVICE_METHOD:
                return typeCharacteristics.has(Characteristic.SERVICE).isSatisfy();
            case REPOSITORY_METHOD:
                return typeCharacteristics.has(Characteristic.REPOSITORY).isSatisfy();
            case MAPPER_METHOD:
                return typeCharacteristics.has(Characteristic.MAPPER).isSatisfy();
            case HANDLER:
                // TODO
        }

        throw new IllegalArgumentException(methodCharacteristic.name());
    }
}