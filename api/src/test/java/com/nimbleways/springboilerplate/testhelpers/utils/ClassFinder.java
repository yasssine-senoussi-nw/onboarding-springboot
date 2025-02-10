package com.nimbleways.springboilerplate.testhelpers.utils;

import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.collections.api.list.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

public final class ClassFinder {

    private ClassFinder() {
    }

    public static ImmutableList<? extends Class<?>> findAllNonAbstractExceptions(String packageName) {
        return Immutable.list.fromStream(
            getTypesAssignableTo(Throwable.class, packageName)
            .filter(c -> !Modifier.isPrivate(c.getModifiers()) && !Modifier.isAbstract(c.getModifiers()))
        );
    }

    @NotNull
    private static Stream<? extends Class<?>> getTypesAssignableTo(
        Class<?> targetType,
        String packageName
    ) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(
            new SimpleBeanDefinitionRegistry(), false);
        scanner.addIncludeFilter(new AssignableTypeFilter(targetType));
        return scanner
            .findCandidateComponents(packageName)
            .stream()
            .map(ClassFinder::getClassOrNull)
            .filter(Objects::nonNull);
    }

    @Nullable
    private static Class<?> getClassOrNull(BeanDefinition beanDefinition) {
        try {
            return Class.forName(beanDefinition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
