package com.scratchgame.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public record GameResponse(String[][] matrix,
						   BigDecimal reward,
						   Map<String, List<String>> appliedWinningCombinations,
						   String appliedBonusSymbol) {
}