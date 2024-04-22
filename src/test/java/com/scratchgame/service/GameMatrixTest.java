package com.scratchgame.service;

import com.scratchgame.dto.config.BonusSymbolProbability;
import com.scratchgame.dto.config.GameConfig;
import com.scratchgame.dto.config.Probability;
import com.scratchgame.dto.config.StandardSymbolProbability;
import com.scratchgame.service.impl.GameMatrixImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameMatrixTest {
    private GameMatrix gameMatrix;
    private GameConfig config;
    private Random mockRandom;

    @BeforeEach
    void setUp() {
        config = mock(GameConfig.class);
        mockRandom = mock(Random.class);
        gameMatrix = new GameMatrixImpl(config, mockRandom);

        when(config.rows()).thenReturn(3);
        when(config.columns()).thenReturn(3);
        setupMockProbabilities();

        when(mockRandom.nextInt(anyInt())).thenReturn(1);
    }

    private void setupMockProbabilities() {
        Probability probabilities = mock(Probability.class);
        when(config.probabilities()).thenReturn(probabilities);

        List<StandardSymbolProbability> standardProbabilities = new ArrayList<>();
        for (int i = 0; i < config.rows(); i++) {
            for (int j = 0; j < config.columns(); j++) {
                StandardSymbolProbability standardProbability = mock(StandardSymbolProbability.class);
                when(standardProbability.row()).thenReturn(i);
                when(standardProbability.column()).thenReturn(j);
                Map<String, Integer> standardSymbols = new HashMap<>();
                standardSymbols.put("A", 1);
                when(standardProbability.symbols()).thenReturn(standardSymbols);
                standardProbabilities.add(standardProbability);
            }
        }
        when(probabilities.standardSymbols()).thenReturn(standardProbabilities);

        BonusSymbolProbability bonusProbability = mock(BonusSymbolProbability.class);
        Map<String, Integer> bonusSymbols = new HashMap<>();
        bonusSymbols.put("X", 1);
        when(bonusProbability.symbols()).thenReturn(bonusSymbols);
        when(probabilities.bonusSymbols()).thenReturn(bonusProbability);
    }


    @Test
    void testGenerateMatrixCompleteFilling() {
        String[][] matrix = gameMatrix.generateMatrix();
        for (String[] row : matrix) {
            for (String cell : row) {
                assertNotNull(cell);
                assertNotEquals("", cell);
            }
        }
    }

    @Test
    void testBonusSymbolPlacement() {
        when(mockRandom.nextInt(3)).thenReturn(1);
        String[][] matrix = gameMatrix.generateMatrix();
        assertEquals("X", matrix[1][1]);
    }
}



