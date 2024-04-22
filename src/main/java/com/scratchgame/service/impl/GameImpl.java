package com.scratchgame.service.impl;

import com.scratchgame.dto.config.GameConfig;
import com.scratchgame.dto.response.GameResponse;
import com.scratchgame.service.Game;
import com.scratchgame.service.GameLogic;
import com.scratchgame.service.GameMatrix;

import java.math.BigDecimal;
import java.util.Random;

public class GameImpl implements Game {
    private final Random random;
    private final GameMatrix gameMatrix;
    private final GameLogic gameLogic;

    public GameImpl(GameConfig config) {
        this.random = new Random();
        this.gameMatrix = new GameMatrixImpl(config, random);
        this.gameLogic = new GameLogicImpl(config);
    }

    @Override
    public GameResponse playGame(BigDecimal bettingAmount) {
        if (bettingAmount == null) {
            throw new IllegalArgumentException("Betting amount cannot be null.");
        }
        if (bettingAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Betting amount must be positive and greater than zero.");
        }

        String[][] matrix = gameMatrix.generateMatrix();
        return gameLogic.getResult(matrix, bettingAmount);
    }
}

