package ru.plumsoftware.costcontrol.data;

public class Category {
    private int id;
    private String categoryName;
    private int categoryPromoResId;
    private int categoryColor;
    private long categoryAddTime;
    private FinanceOperation financeOperation;

    public Category(int id, String categoryName, int categoryPromoResId, int categoryColor, long categoryAddTime) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryPromoResId = categoryPromoResId;
        this.categoryColor = categoryColor;
        this.categoryAddTime = categoryAddTime;
    }

    public Category(int id, String categoryName, int categoryPromoResId, int categoryColor, long categoryAddTime, FinanceOperation financeOperation) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryPromoResId = categoryPromoResId;
        this.categoryColor = categoryColor;
        this.categoryAddTime = categoryAddTime;
        this.financeOperation = financeOperation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryPromoResId() {
        return categoryPromoResId;
    }

    public void setCategoryPromoResId(int categoryPromoResId) {
        this.categoryPromoResId = categoryPromoResId;
    }

    public int getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(int categoryColor) {
        this.categoryColor = categoryColor;
    }

    public long getCategoryAddTime() {
        return categoryAddTime;
    }

    public void setCategoryAddTime(long categoryAddTime) {
        this.categoryAddTime = categoryAddTime;
    }

    public FinanceOperation getFinanceOperation() {
        return financeOperation;
    }

    public void setFinanceOperation(FinanceOperation financeOperation) {
        this.financeOperation = financeOperation;
    }
}
