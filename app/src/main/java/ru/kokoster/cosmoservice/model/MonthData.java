package ru.kokoster.cosmoservice.model;

import java.math.BigDecimal;

/**
 * Created by kokoster on 14.05.16.
 */
public class MonthData {
    public String date;
    public BigDecimal value;

    public MonthData(String date, BigDecimal value) {
        this.date = date;
        this.value = value;
        this.value = this.value.setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
