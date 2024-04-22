package com.scratchgame.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.scratchgame.dto.config.GameConfig;
import com.scratchgame.dto.response.GameResponse;
import com.scratchgame.service.impl.GameImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameTest {
    private Game game;
    private GameConfig config;

    @BeforeEach
    void setup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        InputStream is = GameTest.class.getClassLoader().getResourceAsStream("config.json");
        assertNotNull(is, "Configuration file config.json not found in resources.");
        config = mapper.readValue(is, GameConfig.class);
        game = new GameImpl(config);
    }

    @Test
    void testPlayGameWithValidBet() {
        BigDecimal bettingAmount = new BigDecimal("100");
        GameResponse response = game.playGame(bettingAmount);

        assertNotNull(response);
        assertNotNull(response.matrix());
        assertNotNull(response.reward());
    }

    @Test
    void testPlayGameWithNegativeBettingAmount() {
        assertThrows(IllegalArgumentException.class, () -> game.playGame(new BigDecimal("-1")));
    }

    @Test
    void testPlayGameWithZeroBettingAmount() {
        assertThrows(IllegalArgumentException.class, () -> game.playGame(new BigDecimal("0")));
    }
    @Test
    void testPlayGameWithNoBettingAmount() {
        assertThrows(IllegalArgumentException.class, () -> game.playGame(null));
    }
}

