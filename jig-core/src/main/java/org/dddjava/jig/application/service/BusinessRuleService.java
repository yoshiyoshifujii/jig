package org.dddjava.jig.application.service;

import org.dddjava.jig.annotation.Progress;
import org.dddjava.jig.domain.model.businessrules.BusinessRuleCondition;
import org.dddjava.jig.domain.model.businessrules.BusinessRules;
import org.dddjava.jig.domain.model.categories.CategoryAngles;
import org.dddjava.jig.domain.model.categories.CategoryTypes;
import org.dddjava.jig.domain.model.collections.CollectionAngles;
import org.dddjava.jig.domain.model.declaration.type.TypeIdentifiers;
import org.dddjava.jig.domain.model.declaration.type.Types;
import org.dddjava.jig.domain.model.implementation.ProjectData;
import org.dddjava.jig.domain.model.smells.MethodSmellAngles;
import org.dddjava.jig.domain.model.values.ValueAngles;
import org.dddjava.jig.domain.model.values.ValueKind;
import org.springframework.stereotype.Service;

/**
 * ビジネスルールの分析サービス
 */
@Progress("安定")
@Service
public class BusinessRuleService {

    BusinessRuleCondition businessRuleCondition;

    public BusinessRuleService(BusinessRuleCondition businessRuleCondition) {
        this.businessRuleCondition = businessRuleCondition;
    }

    /**
     * ビジネスルール一覧を取得する
     */
    public BusinessRules businessRules(Types types) {
        return new BusinessRules(types, businessRuleCondition);
    }

    /**
     * メソッドの不吉なにおい一覧を取得する
     */
    public MethodSmellAngles methodSmells(ProjectData projectData) {
        return new MethodSmellAngles(
                projectData.methods(),
                projectData.methodUsingFields(),
                projectData.fieldDeclarations(),
                projectData.methodRelations(),
                businessRules(projectData.types()));
    }

    /**
     * 区分一覧を取得する
     */
    public CategoryAngles categories(ProjectData projectData) {
        CategoryTypes categoryTypes = projectData.categories();

        return new CategoryAngles(categoryTypes,
                projectData.typeDependencies(),
                projectData.fieldDeclarations(),
                projectData.staticFieldDeclarations());
    }

    /**
     * 値一覧を取得する
     */
    public ValueAngles values(ValueKind valueKind, ProjectData projectData) {
        return new ValueAngles(valueKind, projectData.valueTypes(), projectData.typeDependencies());
    }

    /**
     * コレクションを分析する
     */
    public CollectionAngles collections(ProjectData projectData) {
        TypeIdentifiers collectionTypeIdentifiers = projectData.valueTypes().extract(ValueKind.COLLECTION);
        return new CollectionAngles(collectionTypeIdentifiers,
                projectData.fieldDeclarations(),
                projectData.methods(),
                projectData.typeDependencies());
    }
}