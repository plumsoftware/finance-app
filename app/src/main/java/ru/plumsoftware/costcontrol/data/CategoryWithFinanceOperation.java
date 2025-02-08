package ru.plumsoftware.costcontrol.data;

public class CategoryWithFinanceOperation {
    private Category category;
    private int value;
    private String currency;
    private FinanceOperation financeOperation;

    public CategoryWithFinanceOperation(Category category, int value, String currency) {
        this.category = category;
        this.value = value;
        this.currency = currency;
    }

    public CategoryWithFinanceOperation(Category category, int value, String currency, FinanceOperation financeOperation) {
        this.category = category;
        this.value = value;
        this.currency = currency;
        this.financeOperation = financeOperation;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public FinanceOperation getFinanceOperation() {
        return financeOperation;
    }

    public void setFinanceOperation(FinanceOperation financeOperation) {
        this.financeOperation = financeOperation;
    }
}
