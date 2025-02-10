package com.nimbleways.springboilerplate.common.infra.mappers;

import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.common.utils.ParsableChecker;
import org.eclipse.collections.api.set.ImmutableSet;

import static org.checkerframework.checker.nullness.util.NullnessUtil.castNonNull;

public abstract class AbstractDefaultEnumMapper<TEnum extends Enum<TEnum>>
    implements ValueObjectMapper<TEnum, String>, ParsableChecker {
    private final ImmutableSet<String> values;
    private final Class<TEnum> enumType;

    protected AbstractDefaultEnumMapper(Class<TEnum> enumType) {
        this.enumType = enumType;
        // getEnumConstants() is not null because "enumType" is an enum
        TEnum[] enumConstants = castNonNull(enumType.getEnumConstants());
        this.values = Immutable.collectSet(enumConstants, Enum::name);
    }

    @Override
    public String fromValueObject(TEnum enumValue) {
        return enumValue.name();
    }

    @Override
    public TEnum toValueObject(String value) {
        return Enum.valueOf(enumType, value);
    }

    @Override
    public boolean canParse(Object value) {
        return value instanceof String stringValue && values.contains(stringValue);
    }
}
