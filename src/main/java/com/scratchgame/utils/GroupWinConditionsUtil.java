package com.scratchgame.utils;


import com.scratchgame.dto.config.WinCombination;

import java.util.HashMap;
import java.util.Map;

public class GroupWinConditionsUtil {
    private GroupWinConditionsUtil() {}

    public static Map<String, Map<String, WinCombination>> groupWinConditionsByWhen(Map<String, WinCombination> winConditions) {
        Map<String, Map<String, WinCombination>> groupedConditions = new HashMap<>();
        for (Map.Entry<String, WinCombination> entry : winConditions.entrySet()) {
            String uniqueKey = entry.getKey();
            WinCombination winComb = entry.getValue();
            groupedConditions.computeIfAbsent(winComb.when(), k -> new HashMap<>()).put(uniqueKey, winComb);
        }
        return groupedConditions;
    }
}
