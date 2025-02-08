package ru.plumsoftware.costcontrol.data;

public class FinanceOperation {
    private int id;
    private int value;
    private long time;
    private int categoryId;
    private String currency;
    private int financeMode;
    private long addTime;

    public FinanceOperation(int id, int value, long time, int categoryId, String currency, int financeMode, long addTime) {
        this.id = id;
        this.value = value;
        this.time = time;
        this.categoryId = categoryId;
        this.currency = currency;
        this.financeMode = financeMode;
        this.addTime = addTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getFinanceMode() {
        return financeMode;
    }

    public void setFinanceMode(int financeMode) {
        this.financeMode = financeMode;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }
}
