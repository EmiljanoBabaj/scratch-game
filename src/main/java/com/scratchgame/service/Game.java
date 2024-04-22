package com.scratchgame.service;

import com.scratchgame.dto.response.GameResponse;

import java.math.BigDecimal;

public interface Game {
    GameResponse playGame(BigDecimal bettingAmount);
}
