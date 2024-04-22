package com.scratchgame.utils;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class WeightedRandomPickerUtil {
    private final NavigableMap<Double, String> map = new TreeMap<>();
    private final Random random = new Random();
    private double totalSum = 0;

    public void clear() {
        map.clear();
        totalSum = 0;
    }

    public void add(String value, double weight) {
        if (weight <= 0) return;
        totalSum += weight;
        map.put(totalSum, value);
    }

    public String pick() {
        double value = random.nextDouble() * totalSum;
        return map.higherEntry(value).getValue();
    }
}
