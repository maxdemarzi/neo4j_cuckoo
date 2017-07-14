package com.maxdemarzi;

import com.github.mgunlogson.cuckoofilter4j.CuckooFilter;
import com.google.common.hash.Funnels;

import java.util.HashMap;

public class CuckooFilters {
    private static HashMap<String, CuckooFilter<byte[]>> filters = new HashMap<>();


    public static boolean get(String name, long from, long to) {
        if (filters.containsKey(name)) {
            return filters.get(name).mightContain(Util.longsToBytes(from, to));
        } else {
            return false;
        }
    }

    public static void set(String name, long from, long to) {
        if (filters.containsKey(name)) {
            filters.get(name).put(Util.longsToBytes(from, to));
        } else {
            CuckooFilter<byte[]> ck = new CuckooFilter.Builder<>(Funnels.byteArrayFunnel(), 200_000_000).build();
            ck.put(Util.longsToBytes(from, to));
            filters.put(name, ck);
        }
    }

    public static void unset(String name, long from, long to) {
        if (filters.containsKey(name)) {
            filters.get(name).delete(Util.longsToBytes(from, to));
        }
    }

    public static boolean get(String name, String key) {
        if (filters.containsKey(name)) {
            return filters.get(name).mightContain(key.getBytes());
        } else {
            return false;
        }
    }

    public static void set(String name, String key) {
        if (filters.containsKey(name)) {
            filters.get(name).put(key.getBytes());
        } else {
            CuckooFilter<byte[]> ck = new CuckooFilter.Builder<>(Funnels.byteArrayFunnel(), 200_000_000).build();
            ck.put(key.getBytes());
            filters.put(name, ck);
        }
    }

    public static void unset(String name, String key) {
        if (filters.containsKey(name)) {
            filters.get(name).delete(key.getBytes());
        }
    }
}
