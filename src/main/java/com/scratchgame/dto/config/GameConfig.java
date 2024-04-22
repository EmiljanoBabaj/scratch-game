package com.scratchgame.dto.config;

import java.util.Map;

public record GameConfig(int columns, int rows, Map<String, Symbol> symbols, Probability probabilities,
                         Map<String, WinCombination> winCombinations) {
}
