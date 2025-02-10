package com.nimbleways.springboilerplate.common.utils.collections;

import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
class ImmutableUnitTests {

    @Test
    void collectList_shouldMapElementsToImmutableList() {
        // Arrange
        List<String> input = List.of("a", "b", "c");
        Function<String, Integer> mapper = String::length;

        // Act
        ImmutableList<Integer> result = Immutable.collectList(input, mapper);

        // Assert
        assertEquals(3, result.size());
        assertEquals(List.of(1, 1, 1), result.castToList());
    }

    @Test
    void collectSet_shouldMapElementsToImmutableSet() {
        // Arrange
        List<String> input = List.of("a", "bb", "c", "a");
        Function<String, Integer> mapper = String::length;

        // Act
        ImmutableSet<Integer> result = Immutable.collectSet(input, mapper);

        // Assert
        assertEquals(Set.of(1, 2), result.castToSet());
    }

    @Test
    void collectSet_array_shouldMapElementsToImmutableSet() {
        // Arrange
        String[] input = {"x", "yy", "z", "x"};
        Function<String, Integer> mapper = String::length;

        // Act
        ImmutableSet<Integer> result = Immutable.collectSet(input, mapper);

        // Assert
        assertEquals(Set.of(1, 2), result.castToSet());
    }

    @Test
    void collectList_emptyIterable_shouldReturnEmptyImmutableList() {
        // Arrange
        List<String> input = List.of();
        Function<String, Integer> mapper = String::length;

        // Act
        ImmutableList<Integer> result = Immutable.collectList(input, mapper);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    void collectSet_emptyIterable_shouldReturnEmptyImmutableSet() {
        // Arrange
        List<String> input = List.of();
        Function<String, Integer> mapper = String::length;

        // Act
        ImmutableSet<Integer> result = Immutable.collectSet(input, mapper);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    void collectSet_emptyArray_shouldReturnEmptyImmutableSet() {
        // Arrange
        String[] input = {};
        Function<String, Integer> mapper = String::length;

        // Act
        ImmutableSet<Integer> result = Immutable.collectSet(input, mapper);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    void collectList_nonCollectionInput_shouldHandleIterableCorrectly() {
        // Arrange
        Iterable<String> input = () -> List.of("alpha", "beta", "gamma").iterator();
        Function<String, Integer> mapper = String::length;

        // Act
        ImmutableList<Integer> result = Immutable.collectList(input, mapper);

        // Assert
        assertEquals(3, result.size());
        assertEquals(List.of(5, 4, 5), result.castToList());
    }

    @Test
    void collectList_memory_allocation_test() {
        List<@Nullable String> collection = Collections.nCopies(10_000, null);
        long baseline = getAdditionalMemoryWithWarmup(() -> new Object[collection.size()]);

        long collectListAdditionalMemory = getAdditionalMemoryWithWarmup(
                () -> Immutable.collectList(collection, s -> s));

        long expected = baseline * 2;
        assertEquals(expected, collectListAdditionalMemory, expected * 0.05);
    }

    @Test
    void collectSet_with_Iterable_memory_allocation_test() {
        float loadFactor = 0.75f;
        int powerOf2 = 128 * 128;
        int size = (int)(powerOf2 * loadFactor);

        List<Integer> collection = IntStream.rangeClosed(1, size)
                .boxed()
                .collect(Collectors.toList());
        long baseline = getAdditionalMemoryWithWarmup(() -> new Object[collection.size()]);

        long collectSetAdditionalMemory = getAdditionalMemoryWithWarmup(
                () -> Immutable.collectSet(collection, s -> s));

        double expected = (baseline/loadFactor) + baseline + (baseline/loadFactor);
        assertEquals(expected, collectSetAdditionalMemory, expected * 0.05);
    }

    @Test
    void collectSet_with_Array_memory_allocation_test() {
        float loadFactor = 0.75f; // https://stackoverflow.com/a/10901821
        int powerOf2 = 128 * 128;
        int size = (int)(powerOf2 * loadFactor);

        Object[] collection = IntStream.rangeClosed(1, size)
                .boxed()
                .toArray();
        long baseline = getAdditionalMemoryWithWarmup(() -> new Object[collection.length]);

        long collectSetAdditionalMemory = getAdditionalMemoryWithWarmup(
                () -> Immutable.collectSet(collection, s -> s));

        double expected = (baseline/loadFactor) + baseline + (baseline/loadFactor);
        assertEquals(expected, collectSetAdditionalMemory, expected * 0.05);
    }

    private static long getAdditionalMemoryWithWarmup(Supplier<Object> method) {
        // Warming up
        method.get();
        com.sun.management.ThreadMXBean mxBean =
                (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long currentThreadId = Thread.currentThread().getId();
        long before = mxBean.getThreadAllocatedBytes(currentThreadId);
        method.get();
        return mxBean.getThreadAllocatedBytes(currentThreadId) - before;
    }
}
