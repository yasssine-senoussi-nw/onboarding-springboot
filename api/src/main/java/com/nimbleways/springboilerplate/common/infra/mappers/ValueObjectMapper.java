package com.nimbleways.springboilerplate.common.infra.mappers;

public interface ValueObjectMapper<TValueObject, TPrimitive> {
    TPrimitive fromValueObject(TValueObject valueObject);

    TValueObject toValueObject(TPrimitive primitiveValue);
}
