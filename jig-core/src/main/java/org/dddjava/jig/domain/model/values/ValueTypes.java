package org.dddjava.jig.domain.model.values;

import org.dddjava.jig.domain.model.businessrules.BusinessRule;
import org.dddjava.jig.domain.model.businessrules.BusinessRules;
import org.dddjava.jig.domain.model.businessrules.ValueKind;

import java.util.ArrayList;
import java.util.List;

/**
 * 値の型一覧
 */
public class ValueTypes {
    private List<ValueType> list;

    public ValueTypes(BusinessRules businessRules, ValueKind valueKind) {
        list = new ArrayList<>();
        for (BusinessRule businessRule : businessRules.listValue(valueKind)) {
            list.add(new ValueType(businessRule.typeIdentifier()));
        }
    }

    public List<ValueType> list() {
        return list;
    }
}
