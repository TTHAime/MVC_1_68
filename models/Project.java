package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Project model representing crowdfunding projects
 * Project ID: 8-digit number with first digit not being 0
 */
public class Project {
    private String projectId;
    private String name;
    private double goalAmount; // Target funding amount (> 0)
    private LocalDate deadline; // Project deadline (must be in future)
    private double currentAmount; // Current funding raised (starts at 0)
    private String categoryId;
    private String description;
    private String creatorId; // User who created the project

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Project() {
        this.currentAmount = 0.0;
    }

    public Project(String projectId, String name, double goalAmount, LocalDate deadline,
            String categoryId, String description, String creatorId) {
        this.projectId = projectId;
        this.name = name;
        this.goalAmount = goalAmount;
        this.deadline = deadline;
        this.currentAmount = 0.0;
        this.categoryId = categoryId;
        this.description = description;
        this.creatorId = creatorId;
    }

    // Business rule validation
    public boolean isValidProjectId(String projectId) {
        return projectId != null && projectId.length() == 8 &&
                projectId.matches("\\d{8}") && !projectId.startsWith("0");
    }

    public boolean isValidGoalAmount(double amount) {
        return amount > 0;
    }

    public boolean isValidDeadline(LocalDate deadline) {
        return deadline != null && deadline.isAfter(LocalDate.now());
    }

    // Calculate funding progress percentage
    public double getFundingProgress() {
        if (goalAmount <= 0)
            return 0;
        return Math.min((currentAmount / goalAmount) * 100, 100);
    }

    // Check if project is still active
    public boolean isActive() {
        return LocalDate.now().isBefore(deadline) || LocalDate.now().equals(deadline);
    }

    // Check if project has reached its funding goal
    public boolean isFundingGoalReached() {
        return currentAmount >= goalAmount;
    }

    // Get project status string
    public String getProjectStatus() {
        if (isFundingGoalReached()) {
            return "SUCCESS"; // Goal reached
        } else if (isActive()) {
            return "ACTIVE"; // Currently fundraising
        } else {
            return "FAILED"; // Deadline passed without reaching goal
        }
    }

    // Get detailed status description
    public String getStatusDescription() {
        if (isFundingGoalReached()) {
            if (isActive()) {
                return "Goal Reached (Still Accepting Pledges)";
            } else {
                return "Project Successful";
            }
        } else if (isActive()) {
            return "Active Fundraising";
        } else {
            return "Project Failed";
        }
    }

    // Days remaining until deadline
    public long getDaysRemaining() {
        if (!isActive())
            return 0;
        return LocalDate.now().until(deadline).getDays();
    }

    // Getters and Setters
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

    public double getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(double goalAmount) {
        this.goalAmount = goalAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String toString() {
        return name + " (Goal: $" + goalAmount + ", Current: $" + currentAmount + ")";
    }
}