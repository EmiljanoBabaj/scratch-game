package com.scratchgame.dto.config;

import java.util.List;

public record Probability(List<StandardSymbolProbability> standardSymbols, BonusSymbolProbability bonusSymbols) {
}
