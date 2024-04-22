package com.scratchgame.service;

import com.scratchgame.dto.config.GameConfig;
import com.scratchgame.dto.config.Symbol;
import com.scratchgame.dto.config.WinCombination;
import com.scratchgame.dto.response.GameResponse;
import com.scratchgame.service.impl.GameLogicImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameLogicTest {
    private GameLogic gameLogic;
    private GameConfig config;

    @BeforeEach
    void setup() {
        config = mock(GameConfig.class);
        gameLogic = new GameLogicImpl(config);
    }

    @Test
    void testNoWinningCombinationsReturnsZeroReward() {
        String[][] matrix = {{"A", "B", "C"}, {"C", "B", "A"}, {"A", "C", "B"}};
        when(config.winCombinations()).thenReturn(Collections.emptyMap());

        GameResponse response = gameLogic.getResult(matrix, BigDecimal.valueOf(100));
        assertEquals(BigDecimal.ZERO, response.reward());
        assertTrue(response.appliedWinningCombinations().isEmpty());
        assertNull(response.appliedBonusSymbol());
    }

    @Test
    void testSameSymbolsWin() {
        Map<String, Symbol> symbolMap = generateStandardSymbols();
        String[] symbols = symbolMap.keySet().toArray(new String[0]);
        String[][] matrix = {{symbols[0], symbols[0], symbols[0]},
                {symbols[1], symbols[2], symbols[3]},
                {symbols[1], symbols[2], symbols[3]}};
        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("same_symbol_3_times", new WinCombination(BigDecimal.valueOf(2), "same_symbols", 3,
                "same_symbols", null));
        when(config.winCombinations()).thenReturn(winCombinations);
        when(config.symbols()).thenReturn(symbolMap);

        GameResponse response = gameLogic.getResult(matrix, BigDecimal.valueOf(100));

        BigDecimal expectedReward = new BigDecimal("400");
        assertEquals(0, expectedReward.compareTo(response.reward()));
        assertEquals("same_symbol_3_times", response.appliedWinningCombinations().get(symbols[0]).get(0));
        assertEquals(1, response.appliedWinningCombinations().size());
        assertEquals(1, response.appliedWinningCombinations().get(symbols[0]).size());
        assertNull(response.appliedBonusSymbol());
    }

    @Test
    void testLinearSymbolsWin() {
        Map<String, Symbol> symbolMap = generateStandardSymbols();
        String[] symbols = symbolMap.keySet().toArray(new String[0]);
        String[][] matrix = {{symbols[0], symbols[0], symbols[0]},
                {symbols[1], symbols[2], symbols[3]},
                {symbols[1], symbols[2], symbols[3]}};

        Map<String, WinCombination> winCombinations = new HashMap<>();
        List<List<String>> coveredAreas = List.of(
                Arrays.asList("0:0", "0:1", "0:2"));
        winCombinations.put("same_symbols_horizontally", new WinCombination(BigDecimal.valueOf(2), "linear_symbols",
                null,
                "horizontally_linear_symbols", coveredAreas));
        when(config.winCombinations()).thenReturn(winCombinations);
        when(config.symbols()).thenReturn(symbolMap);

        GameResponse response = gameLogic.getResult(matrix, BigDecimal.valueOf(100));

        BigDecimal expectedReward = new BigDecimal("400");
        assertEquals(0, expectedReward.compareTo(response.reward()));
        assertEquals("same_symbols_horizontally", response.appliedWinningCombinations().get(symbols[0]).get(0));
        assertEquals(1, response.appliedWinningCombinations().size());
        assertEquals(1, response.appliedWinningCombinations().get(symbols[0]).size());
        assertNull(response.appliedBonusSymbol());
    }

    @Test
    void testOnePerGroupWin() {
        Map<String, Symbol> symbolMap = generateStandardSymbols();
        String[] symbols = symbolMap.keySet().toArray(new String[0]);
        String[][] matrix = {{symbols[0], symbols[0], symbols[0]},
                {symbols[0], symbols[2], symbols[3]},
                {symbols[1], symbols[2], symbols[3]}};
        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("same_symbol_3_times", new WinCombination(BigDecimal.valueOf(2), "same_symbols", 3,
                "same_symbols", null));
        winCombinations.put("same_symbol_4_times", new WinCombination(BigDecimal.valueOf(3), "same_symbols", 4,
                "same_symbols", null));
        when(config.winCombinations()).thenReturn(winCombinations);
        when(config.symbols()).thenReturn(symbolMap);

        GameResponse response = gameLogic.getResult(matrix, BigDecimal.valueOf(100));

        BigDecimal expectedReward = new BigDecimal("600");
        assertEquals(0, expectedReward.compareTo(response.reward()));
        assertEquals("same_symbol_4_times", response.appliedWinningCombinations().get(symbols[0]).get(0));
        assertEquals(1, response.appliedWinningCombinations().size());
        assertEquals(1, response.appliedWinningCombinations().get(symbols[0]).size());
        assertNull(response.appliedBonusSymbol());
    }

    @Test
    void testWinWithBonus() {
        Map<String, Symbol> symbolMap = generateStandardSymbols();
        String[] symbols = symbolMap.keySet().toArray(new String[0]);
        Symbol bonusSymbol = new Symbol(null, "bonus", "extra_bonus", BigDecimal.valueOf(1000));
        String bonusSymbolString = "+1000";
        symbolMap.put(bonusSymbolString, bonusSymbol);
        String[][] matrix = {{symbols[0], symbols[0], symbols[0]},
                {symbols[1], bonusSymbolString, symbols[3]},
                {symbols[1], symbols[2], symbols[3]}};

        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("same_symbol_3_times", new WinCombination(BigDecimal.valueOf(2), "same_symbols", 3,
                "same_symbols", null));
        when(config.winCombinations()).thenReturn(winCombinations);
        when(config.symbols()).thenReturn(symbolMap);

        GameResponse response = gameLogic.getResult(matrix, BigDecimal.valueOf(100));

        BigDecimal expectedReward = new BigDecimal("1400");
        assertEquals(0, expectedReward.compareTo(response.reward()));
        assertEquals("same_symbol_3_times", response.appliedWinningCombinations().get(symbols[0]).get(0));
        assertEquals(1, response.appliedWinningCombinations().size());
        assertEquals(1, response.appliedWinningCombinations().get(symbols[0]).size());
        assertEquals(bonusSymbolString, response.appliedBonusSymbol());
    }

    private Map<String, Symbol> generateStandardSymbols() {
        Map<String, Symbol> symbolMap = new HashMap<>();
        Symbol symbol1 = new Symbol(BigDecimal.valueOf(2), "standard", null, null);
        symbolMap.put("A", symbol1);
        Symbol symbol2 = new Symbol(BigDecimal.valueOf(3), "standard", null, null);
        symbolMap.put("B", symbol2);
        Symbol symbol3 = new Symbol(BigDecimal.valueOf(4), "standard", null, null);
        symbolMap.put("C", symbol3);
        Symbol symbol4 = new Symbol(BigDecimal.valueOf(5), "standard", null, null);
        symbolMap.put("D", symbol4);
        return symbolMap;
    }
}