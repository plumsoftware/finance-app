package ru.plumsoftware.costcontrol.data;

public class CurrencyWithValue {
    private String currencySign;
    private long sum;

    public CurrencyWithValue(String currencySign, long sum) {
        this.currencySign = currencySign;
        this.sum = sum;
    }

    public String getCurrencySign() {
        return currencySign;
    }

    public void setCurrencySign(String currencySign) {
        this.currencySign = currencySign;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }
}
