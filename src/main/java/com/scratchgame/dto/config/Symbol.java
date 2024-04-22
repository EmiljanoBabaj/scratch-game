package com.scratchgame.dto.config;

import java.math.BigDecimal;

public record Symbol(BigDecimal rewardMultiplier, String type, String impact, BigDecimal extra) {
}
