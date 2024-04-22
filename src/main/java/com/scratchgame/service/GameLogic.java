package com.scratchgame.service;

import com.scratchgame.dto.response.GameResponse;

import java.math.BigDecimal;

public interface GameLogic {
    GameResponse getResult(String[][] matrix, BigDecimal bettingAmount);
}
