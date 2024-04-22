package com.scratchgame.service.impl;

import com.scratchgame.dto.config.GameConfig;
import com.scratchgame.dto.config.StandardSymbolProbability;
import com.scratchgame.service.GameMatrix;
import com.scratchgame.utils.WeightedRandomPickerUtil;

import java.util.Arrays;
import java.util.Random;

public class GameMatrixImpl implements GameMatrix {
    private final GameConfig config;
    private final Random random;

    public GameMatrixImpl(GameConfig config, Random random) {
        this.config = config;
        this.random = random;
    }

    public String[][] generateMatrix() {
        String[][] matrix = new String[config.rows()][config.columns()];
        initializeMatrix(matrix);
        populateStandardSymbols(matrix);
        populateBonusSymbol(matrix);
        return matrix;
    }

    private void initializeMatrix(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            Arrays.fill(matrix[i], "");
        }
    }

    private void populateStandardSymbols(String[][] matrix) {
        WeightedRandomPickerUtil picker = new WeightedRandomPickerUtil();
        for (StandardSymbolProbability probability : config.probabilities().standardSymbols()) {
            picker.clear();
            probability.symbols().forEach(picker::add);
            matrix[probability.row()][probability.column()] = picker.pick();
        }
    }
    private void populateBonusSymbol(String[][] matrix) {
        WeightedRandomPickerUtil picker = new WeightedRandomPickerUtil();
        config.probabilities().bonusSymbols().symbols().forEach(picker::add);
        int randomRow = random.nextInt(matrix.length);
        int randomColumn = random.nextInt(matrix[0].length);
        matrix[randomRow][randomColumn] = picker.pick();
    }
}
