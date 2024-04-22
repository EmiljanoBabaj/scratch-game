package com.scratchgame.dto.config;

import java.util.Map;

public record StandardSymbolProbability(Integer column, Integer row, Map<String, Integer> symbols) {
}
