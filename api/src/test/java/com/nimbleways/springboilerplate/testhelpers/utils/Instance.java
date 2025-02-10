package com.nimbleways.springboilerplate.testhelpers.utils;

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Instance {
    public static final String SCOPE_NAME = "perCall";
    private static final ClearableSimpleThreadScope SCOPE = new ClearableSimpleThreadScope();
    private static final MutableMap<Class<?>, AnnotationConfigApplicationContext> CONTEXT_MAP = new ConcurrentHashMap<>();

    public static <T> T create(Class<T> clazz) {
        AnnotationConfigApplicationContext ctx = CONTEXT_MAP.computeIfAbsent(clazz, Instance::createContext);
        return getNewBean(ctx, clazz);
    }

    private static <T> T getNewBean(AnnotationConfigApplicationContext ctx, Class<T> clazz) {
        T bean = ctx.getBean(clazz);
        SCOPE.clear();
        return bean;
    }

    private static AnnotationConfigApplicationContext createContext(final Class<?> clazz) {
        Import importAnnotation = clazz.getAnnotation(Import.class);
        if (importAnnotation == null) {
            throw new IllegalArgumentException("@Import annotation is missing on class: " + clazz.getName());
        }
        Class<?>[] dependencyClasses = importAnnotation.value();
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.getBeanFactory().registerScope(SCOPE_NAME, SCOPE);
        ctx.registerBean(clazz.getName(), clazz, bd -> bd.setScope(SCOPE_NAME));
        for (Class<?> dependencyClass : dependencyClasses) {
            ctx.registerBean(dependencyClass.getName(), dependencyClass, bd -> bd.setScope(SCOPE_NAME));
        }
        ctx.refresh();
        return ctx;
    }

    // fork of org.springframework.context.support.SimpleThreadScope
    private final static class ClearableSimpleThreadScope implements Scope {
        private final ThreadLocal<Map<String, Object>> threadScope = NamedThreadLocal.withInitial("ClearableSimpleThreadScope", HashMap::new);

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            Map<String, Object> scope = this.threadScope.get();
            Object scopedObject = scope.get(name);
            if (scopedObject == null) {
                scopedObject = objectFactory.getObject();
                scope.put(name, scopedObject);
            }
            return scopedObject;
        }

        @Override
        @Nullable
        public Object remove(String name) {
            Map<String, Object> scope = this.threadScope.get();
            return scope.remove(name);
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
        }

        @Override
        @Nullable
        public Object resolveContextualObject(String key) {
            return null;
        }

        @Override
        public String getConversationId() {
            return Thread.currentThread().getName();
        }

        public void clear() {
            threadScope.get().clear();
        }
    }
}
