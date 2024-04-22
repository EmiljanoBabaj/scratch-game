package com.scratchgame.dto.config;

import java.math.BigDecimal;
import java.util.List;

public record WinCombination(BigDecimal rewardMultiplier, String when, Integer count, String group,
                             List<List<String>> coveredAreas) { }
