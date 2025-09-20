package models;

/**
 * RewardTier model representing reward levels for projects
 */
public class RewardTier {
    private String tierId;
    private String projectId;
    private String name;
    private double minimumAmount; // Minimum pledge amount for this tier
    private int totalQuantity; // Total available quantity
    private int remainingQuantity; // Remaining quantity/quota
    private String description;

    public RewardTier() {
    }

    public RewardTier(String tierId, String projectId, String name, double minimumAmount,
            int totalQuantity, String description) {
        this.tierId = tierId;
        this.projectId = projectId;
        this.name = name;
        this.minimumAmount = minimumAmount;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = totalQuantity;
        this.description = description;
    }

    // Business rule validation
    public boolean isValidMinimumAmount(double amount) {
        return amount > 0;
    }

    public boolean isAvailable() {
        return remainingQuantity > 0;
    }

    public boolean canPledge(double amount) {
        return amount >= minimumAmount && isAvailable();
    }

    // Reduce quantity when someone pledges for this tier
    public boolean reducQuantity() {
        if (remainingQuantity > 0) {
            remainingQuantity--;
            return true;
        }
        return false;
    }

    // Get quantity sold
    public int getQuantitySold() {
        return totalQuantity - remainingQuantity;
    }

    // Getters and Setters
    public String getTierId() {
        return tierId;
    }

    public void setTierId(String tierId) {
        this.tierId = tierId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " - $" + minimumAmount + " (" + remainingQuantity + " left)";
    }
}