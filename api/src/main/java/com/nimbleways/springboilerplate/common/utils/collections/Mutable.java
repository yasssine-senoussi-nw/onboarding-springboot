package com.nimbleways.springboilerplate.common.utils.collections;

import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.BiMaps;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.SortedBags;
import org.eclipse.collections.api.factory.SortedMaps;
import org.eclipse.collections.api.factory.SortedSets;
import org.eclipse.collections.api.factory.bag.MutableBagFactory;
import org.eclipse.collections.api.factory.bag.sorted.MutableSortedBagFactory;
import org.eclipse.collections.api.factory.bimap.MutableBiMapFactory;
import org.eclipse.collections.api.factory.list.MutableListFactory;
import org.eclipse.collections.api.factory.map.MutableMapFactory;
import org.eclipse.collections.api.factory.map.sorted.MutableSortedMapFactory;
import org.eclipse.collections.api.factory.set.MutableSetFactory;
import org.eclipse.collections.api.factory.set.sorted.MutableSortedSetFactory;

@SuppressWarnings({"PMD.UseCollectionUtils", "PMD.DataClass", "unused"})
public final class Mutable {
    public static final MutableBagFactory bag = Bags.mutable;
    public static final MutableBiMapFactory biMap = BiMaps.mutable;
    public static final MutableListFactory list = Lists.mutable;
    public static final MutableMapFactory map = Maps.mutable;
    public static final MutableSetFactory set = Sets.mutable;
    public static final MutableSortedBagFactory sortedBag = SortedBags.mutable;
    public static final MutableSortedMapFactory sortedMap = SortedMaps.mutable;
    public static final MutableSortedSetFactory sortedSet = SortedSets.mutable;

    private Mutable() {
    }
}
