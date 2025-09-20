package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Pledge model representing user pledges to projects
 */
public class Pledge {
    private String pledgeId;
    private String userId; // User making the pledge
    private String projectId; // Project being supported
    private LocalDateTime pledgeTime; // When the pledge was made
    private double amount; // Amount pledged
    private String rewardTierId; // Selected reward tier (optional)
    private PledgeStatus status; // Success or rejected
    private String rejectionReason; // Reason if rejected

    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum PledgeStatus {
        SUCCESS, REJECTED
    }

    public Pledge() {
        this.pledgeTime = LocalDateTime.now();
        this.status = PledgeStatus.SUCCESS;
    }

    public Pledge(String pledgeId, String userId, String projectId, double amount, String rewardTierId) {
        this.pledgeId = pledgeId;
        this.userId = userId;
        this.projectId = projectId;
        this.pledgeTime = LocalDateTime.now();
        this.amount = amount;
        this.rewardTierId = rewardTierId;
        this.status = PledgeStatus.SUCCESS;
    }

    // Business rule validation
    public boolean isValidAmount(double amount) {
        return amount > 0;
    }

    public void reject(String reason) {
        this.status = PledgeStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public boolean isSuccessful() {
        return status == PledgeStatus.SUCCESS;
    }

    public boolean isRejected() {
        return status == PledgeStatus.REJECTED;
    }

    // Getters and Setters
    public String getPledgeId() {
        return pledgeId;
    }

    public void setPledgeId(String pledgeId) {
        this.pledgeId = pledgeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public LocalDateTime getPledgeTime() {
        return pledgeTime;
    }

    public void setPledgeTime(LocalDateTime pledgeTime) {
        this.pledgeTime = pledgeTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRewardTierId() {
        return rewardTierId;
    }

    public void setRewardTierId(String rewardTierId) {
        this.rewardTierId = rewardTierId;
    }

    public PledgeStatus getStatus() {
        return status;
    }

    public void setStatus(PledgeStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public String toString() {
        return "Pledge $" + amount + " to " + projectId + " (" + status + ")";
    }
}