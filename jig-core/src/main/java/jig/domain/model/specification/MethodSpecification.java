package jig.domain.model.specification;

import jig.domain.model.identifier.Identifier;
import jig.domain.model.identifier.Identifiers;
import jig.domain.model.identifier.MethodIdentifier;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodSpecification {

    // TODO 名前の混乱をなんとかする
    public final String methodName;
    public final MethodIdentifier identifier;
    public final String descriptor;

    public MethodSpecification(Identifier classIdentifier, String name, String descriptor) {
        this.methodName = name;
        this.descriptor = descriptor;

        this.identifier = new MethodIdentifier(classIdentifier, name, toArgumentSignatureString(descriptor));
    }

    private static Identifiers toArgumentSignatureString(String descriptor) {
        Type[] argumentTypes = Type.getArgumentTypes(descriptor);
        return Arrays.stream(argumentTypes).map(Type::getClassName).map(Identifier::new).collect(Identifiers.collector());
    }

    public final List<Identifier> usingFieldTypeIdentifiers = new ArrayList<>();
    public final List<MethodIdentifier> usingMethodIdentifiers = new ArrayList<>();

    public MethodIdentifier methodIdentifierWith(Identifier typeIdentifier) {
        return new MethodIdentifier(typeIdentifier, this.methodName, argumentTypeIdentifiers());
    }

    public Identifiers argumentTypeIdentifiers() {
        return Arrays.stream(Type.getArgumentTypes(this.descriptor))
                        .map(Type::getClassName)
                        .map(Identifier::new)
                        .collect(Identifiers.collector());
    }

    public Identifier getReturnTypeName() {
        return new Identifier(Type.getReturnType(descriptor).getClassName());
    }

    public void addFieldInstruction(String owner, String name, String descriptor) {
        // 使っているフィールドの型がわかればOK
        Type type = Type.getType(descriptor);
        usingFieldTypeIdentifiers.add(new Identifier(type.getClassName()));
    }

    public void addMethodInstruction(String owner, String name, String descriptor) {
        // 使ってるメソッドがわかりたい
        Identifier ownerTypeIdentifier = new Identifier(owner);
        MethodIdentifier methodIdentifier = new MethodIdentifier(ownerTypeIdentifier, name, toArgumentSignatureString(descriptor));
        usingMethodIdentifiers.add(methodIdentifier);
    }
}
