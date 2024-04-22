package com.scratchgame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.scratchgame.dto.config.GameConfig;
import com.scratchgame.dto.response.GameResponse;
import com.scratchgame.service.Game;
import com.scratchgame.service.impl.GameImpl;

import java.math.BigDecimal;
import java.nio.file.Paths;

public class Main {



    public static void main(String[] args) throws Exception {
        BigDecimal bettingAmount = null;
        String configPath = null;

        for (int i = 0; i < args.length; i++) {
            if ("--betting-amount".equals(args[i]) && i + 1 < args.length) {
                try {
                    bettingAmount = new BigDecimal(args[i + 1]);
                    i++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid betting amount provided. Please enter a valid decimal number.");
                    System.exit(1);
                }
            } else if ("--config".equals(args[i]) && i + 1 < args.length) {
                configPath = args[i + 1];
                i++;
            }
        }
        if (bettingAmount == null) {
            System.out.println("Error: A betting amount is required. Use --betting-amount <amount>");
            System.exit(1);
        }
        if (configPath == null) {
            System.out.println("Error: A configuration file path is required. Use --config <path>");
            System.exit(1);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        GameConfig config = mapper.readValue(Paths.get(configPath).toFile(), GameConfig.class);

        Game game = new GameImpl(config);
        GameResponse result = game.playGame(bettingAmount);

        System.out.println(mapper.writeValueAsString(result));
    }
}

