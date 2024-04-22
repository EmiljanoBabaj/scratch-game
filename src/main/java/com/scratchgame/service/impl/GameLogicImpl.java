package com.scratchgame.service.impl;

import com.scratchgame.dto.config.GameConfig;
import com.scratchgame.dto.config.Symbol;
import com.scratchgame.dto.config.WinCombination;
import com.scratchgame.dto.response.GameResponse;
import com.scratchgame.service.GameLogic;
import com.scratchgame.utils.GroupWinConditionsUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogicImpl implements GameLogic {

    public static final String STANDARD = "standard";
    private final GameConfig config;

    public GameLogicImpl( GameConfig config) {
        this.config = config;
    }

    public GameResponse getResult(String[][] matrix, BigDecimal bettingAmount) {
        Map<String, Map<String, WinCombination>> groupedWinConditionsByWhen =
                GroupWinConditionsUtil.groupWinConditionsByWhen(config.winCombinations());

        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();

        groupedWinConditionsByWhen.forEach((whenType, winCombinations) -> {
            switch (whenType) {
                case "same_symbols":
                    calculateSameSymbolWins(winCombinations, appliedWinningCombinations, matrix);
                    break;
                case "linear_symbols":
                    calculateLinearSymbolWins(winCombinations, appliedWinningCombinations, matrix);
                    break;
                default:
                    throw new IllegalStateException("Unexpected win condition type: " + whenType);
            }
        });

        BigDecimal reward = BigDecimal.ZERO;
        if (appliedWinningCombinations.isEmpty()) {
            return new GameResponse(matrix, reward, appliedWinningCombinations, null);
        } else {
            reward = calculateReward(bettingAmount, appliedWinningCombinations);
            String bonusSymbol = getBonusSymbol(matrix);
            if (bonusSymbol == null || bonusSymbol.equals("MISS")){
                bonusSymbol = null;
            } else {
                reward = applyBonus(reward, bonusSymbol);
            }
            return new GameResponse(matrix, reward, appliedWinningCombinations, bonusSymbol);
        }
    }

    private void calculateSameSymbolWins(Map<String, WinCombination> winCombinations,
                                         Map<String, List<String>> appliedWinningCombinations, String[][] matrix) {
        Map<String, Integer> symbolCounts = getCountOfSymbols(matrix);

        symbolCounts.forEach((symbol, count) -> {
            Map<String, BigDecimal> bestRewardPerGroup = new HashMap<>();
            Map<String, String> bestKeyByGroup = new HashMap<>();

            winCombinations.forEach((key, winCombination) -> {
                if (count >= winCombination.count()) {
                    updateBestGroupWin(key, winCombination, bestRewardPerGroup, bestKeyByGroup);
                }
            });

            bestKeyByGroup.values().forEach(winKey ->
                    appliedWinningCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).add(winKey));
        });
    }

    private Map<String, Integer> getCountOfSymbols(String[][] matrix) {
        Map<String, Integer> count = new HashMap<>();
        for (String[] row : matrix) {
            for (String symbol : row) {
                if (config.symbols().get(symbol) != null && STANDARD.equals(config.symbols().get(symbol).type())) {
                    count.merge(symbol, 1, Integer::sum);
                }
            }
        }
        return count;
    }

    private void calculateLinearSymbolWins(Map<String, WinCombination> winCombinations,
                                           Map<String, List<String>> appliedWinningCombinations, String[][] matrix) {
        Map<String, Map<String, String>> bestKeyPerGroup = new HashMap<>();
        Map<String, Map<String, BigDecimal>> bestRewardPerGroup = new HashMap<>();

        for (Map.Entry<String, WinCombination> entry : winCombinations.entrySet()) {
            String key = entry.getKey();
            WinCombination winCombination = entry.getValue();

            for (List<String> coveredArea : winCombination.coveredAreas()) {
                if (isUniformSymbol(coveredArea, matrix)) {
                    String symbol = getSymbolAtPosition(coveredArea.get(0), matrix);
                    updateBestGroupWin(key, winCombination, bestRewardPerGroup.computeIfAbsent(symbol, k -> new HashMap<>()),
                            bestKeyPerGroup.computeIfAbsent(symbol, k -> new HashMap<>()));
                }
            }
        }

        bestKeyPerGroup.forEach((symbol, groupKeys) -> {
            appliedWinningCombinations.computeIfAbsent(symbol, k -> new ArrayList<>()).addAll(groupKeys.values());
        });
    }

    private boolean isUniformSymbol(List<String> coveredArea, String[][] matrix) {
        if (coveredArea.isEmpty() || !config.symbols().get(getSymbolAtPosition(coveredArea.get(0), matrix)).type().equals(
                STANDARD)) {
            return false;
        }

        String firstSymbol = getSymbolAtPosition(coveredArea.get(0), matrix);
        return coveredArea.stream()
                .map(position -> getSymbolAtPosition(position, matrix))
                .allMatch(symbol -> symbol.equals(firstSymbol));
    }

    private String getSymbolAtPosition(String position, String[][] matrix) {
        String[] parts = position.split(":");
        return matrix[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])];
    }

    private void updateBestGroupWin(String key, WinCombination winCombination, Map<String, BigDecimal> bestRewardPerGroup,
                                    Map<String, String> bestKeyByGroup) {
        String group = winCombination.group();
        BigDecimal rewardMultiplier = winCombination.rewardMultiplier();

        if (!bestRewardPerGroup.containsKey(group) || rewardMultiplier.compareTo(bestRewardPerGroup.get(group)) > 0) {
            bestRewardPerGroup.put(group, rewardMultiplier);
            bestKeyByGroup.put(group, key);
        }
    }

    private BigDecimal calculateReward(BigDecimal bettingAmount, Map<String, List<String>> appliedWinningCombinations) {
        BigDecimal reward = BigDecimal.ZERO;

        for (Map.Entry<String, List<String>> winSymbol : appliedWinningCombinations.entrySet()
        ) {
            BigDecimal symbolRewardMultiplier = config.symbols().get(winSymbol.getKey()).rewardMultiplier();
            BigDecimal symbolReward = bettingAmount.multiply(symbolRewardMultiplier);
            for (String winCombinations : winSymbol.getValue()
            ) {
                BigDecimal winCombinationRewardMultiplier =
                        config.winCombinations().get(winCombinations).rewardMultiplier();
                symbolReward = symbolReward.multiply(winCombinationRewardMultiplier);
            }
            reward = reward.add(symbolReward);
        }
        return reward;
    }

    private String getBonusSymbol(String[][] matrix) {
        for (String[] row : matrix) {
            for (String symbol : row) {
                if (!STANDARD.equals(config.symbols().get(symbol).type())) {
                    return symbol;
                }
            }
        }
        return null;
    }

    private BigDecimal applyBonus(BigDecimal reward, String bonusSymbolString) {
        Symbol bonusSymbol = config.symbols().get(bonusSymbolString);
        return switch (bonusSymbol.impact()) {
            case "multiply_reward" -> reward.multiply(bonusSymbol.rewardMultiplier());
            case "extra_bonus" -> reward.add(bonusSymbol.extra());
            default -> throw new IllegalStateException("Unexpected value: " + bonusSymbol.impact());
        };
    }

}
