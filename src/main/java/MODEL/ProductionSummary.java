package MODEL;

import java.util.HashMap;
import java.util.Map;

public class ProductionSummary {
    private String productName;
    private int totalQuantity;
    private Map<String, Integer> quantityByPeriod;

    public ProductionSummary(String productName) {
        this.productName = productName;
        this.totalQuantity = 0;
        this.quantityByPeriod = new HashMap<>();
    }

    public void addQuantity(String period, int quantity) {
        quantityByPeriod.put(period, quantityByPeriod.getOrDefault(period, 0) + quantity);
        totalQuantity += quantity;
    }

    // Getters
    public String getProductName() { return productName; }
    public int getTotalQuantity() { return totalQuantity; }
    public Map<String, Integer> getQuantityByPeriod() { return quantityByPeriod; }

    @Override
    public String toString() {
        return String.format("Product: %s, Total: %d, Breakdown: %s",
                productName, totalQuantity, quantityByPeriod);
    }
}
