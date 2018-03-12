package jig.domain.model.list;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

public enum RepositoryModelConcern implements Converter {
    クラス名(condition ->
            condition.getType().name().value()),
    クラス和名(condition ->
            condition.getType().japaneseName().value()),
    メソッド名(condition ->
            condition.getMethod().name()),
    メソッド戻り値の型(condition ->
            condition.getMethod().returnType().getSimpleName()),
    メソッド引数型(condition ->
            Arrays.stream(condition.getMethod().parameters())
                    .map(Class::getSimpleName)
                    .collect(joining(",")));

    private final Function<ConverterCondition, String> function;

    RepositoryModelConcern(Function<ConverterCondition, String> function) {
        this.function = function;
    }

    public String convert(ConverterCondition converterCondition) {
        return function.apply(converterCondition);
    }
}
