package controllers;

import models.*;
import data.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Pledge Controller - handles pledge-related business logic and validation
 */
public class PledgeController {
    private MainController mainController;
    private PledgeDAO pledgeDAO;
    private ProjectDAO projectDAO;
    private RewardTierDAO rewardTierDAO;
    private static int pledgeCounter = 1;

    public PledgeController(MainController mainController) {
        this.mainController = mainController;
        this.pledgeDAO = new PledgeDAO();
        this.projectDAO = new ProjectDAO();
        this.rewardTierDAO = new RewardTierDAO();
        initializePledgeCounter();
    }

    /**
     * Initialize pledge counter based on existing pledges
     */
    private void initializePledgeCounter() {
        try {
            List<Pledge> pledges = pledgeDAO.loadPledges();
            pledgeCounter = pledges.size() + 1;
        } catch (IOException e) {
            pledgeCounter = 1;
        }
    }

    /**
     * Process a new pledge with business rule validation
     */
    public PledgeResult processPledge(String projectId, double amount, String rewardTierId) {
        User currentUser = mainController.getCurrentUser();
        if (currentUser == null) {
            return new PledgeResult(false, "User not logged in");
        }

        try {
            // Load project
            Project project = projectDAO.findById(projectId);
            if (project == null) {
                return new PledgeResult(false, "Project not found");
            }

            // Business Rule 1: Project deadline must be in the future
            if (!project.isActive()) {
                return new PledgeResult(false, "Project deadline has passed");
            }

            // Business Rule 2: Amount must be positive
            if (amount <= 0) {
                return new PledgeResult(false, "Pledge amount must be greater than 0");
            }

            // Business Rule 3: If reward tier is selected, amount must meet minimum
            RewardTier selectedTier = null;
            if (rewardTierId != null && !rewardTierId.isEmpty()) {
                selectedTier = rewardTierDAO.findById(rewardTierId);
                if (selectedTier == null) {
                    return new PledgeResult(false, "Selected reward tier not found");
                }

                if (!selectedTier.canPledge(amount)) {
                    if (amount < selectedTier.getMinimumAmount()) {
                        return new PledgeResult(false,
                                String.format("Minimum amount for '%s' is $%.2f",
                                        selectedTier.getName(), selectedTier.getMinimumAmount()));
                    } else if (!selectedTier.isAvailable()) {
                        return new PledgeResult(false,
                                String.format("Reward tier '%s' is no longer available",
                                        selectedTier.getName()));
                    }
                }
            }

            // Create and save pledge
            Pledge pledge = new Pledge();
            pledge.setPledgeId("P" + String.format("%06d", pledgeCounter++));
            pledge.setUserId(currentUser.getUserId());
            pledge.setProjectId(projectId);
            pledge.setPledgeTime(LocalDateTime.now());
            pledge.setAmount(amount);
            pledge.setRewardTierId(rewardTierId);
            pledge.setStatus(Pledge.PledgeStatus.SUCCESS);

            pledgeDAO.addPledge(pledge);

            // Update project current amount
            project.setCurrentAmount(project.getCurrentAmount() + amount);
            projectDAO.updateProject(project);

            // Update reward tier quantity if selected
            if (selectedTier != null) {
                selectedTier.reducQuantity();
                rewardTierDAO.updateRewardTier(selectedTier);
            }

            return new PledgeResult(true, "Pledge successful! Thank you for your support.");

        } catch (IOException e) {
            return new PledgeResult(false, "Error processing pledge: " + e.getMessage());
        }
    }

    /**
     * Get pledges for a user
     */
    public List<Pledge> getUserPledges(String userId) {
        try {
            return pledgeDAO.findByUser(userId);
        } catch (IOException e) {
            mainController.showError("Error loading user pledges: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get pledges for a project
     */
    public List<Pledge> getProjectPledges(String projectId) {
        try {
            return pledgeDAO.findByProject(projectId);
        } catch (IOException e) {
            mainController.showError("Error loading project pledges: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get successful pledges statistics
     */
    public PledgeStatistics getPledgeStatistics() {
        try {
            List<Pledge> allPledges = pledgeDAO.loadPledges();
            return new PledgeStatistics(allPledges);
        } catch (IOException e) {
            mainController.showError("Error loading pledge statistics: " + e.getMessage());
            return new PledgeStatistics(new ArrayList<>());
        }
    }

    /**
     * Result class for pledge processing
     */
    public static class PledgeResult {
        private boolean success;
        private String message;

        public PledgeResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Statistics class for pledge data
     */
    public static class PledgeStatistics {
        private int totalPledges;
        private int successfulPledges;
        private int rejectedPledges;
        private double totalAmountRaised;
        private double averagePledgeAmount;
        private int uniqueBackers;

        public PledgeStatistics(List<Pledge> pledges) {
            this.totalPledges = pledges.size();
            this.successfulPledges = (int) pledges.stream().filter(Pledge::isSuccessful).count();
            this.rejectedPledges = (int) pledges.stream().filter(Pledge::isRejected).count();
            this.totalAmountRaised = pledges.stream()
                    .filter(Pledge::isSuccessful)
                    .mapToDouble(Pledge::getAmount)
                    .sum();
            this.averagePledgeAmount = successfulPledges > 0 ? totalAmountRaised / successfulPledges : 0;
            this.uniqueBackers = (int) pledges.stream()
                    .filter(Pledge::isSuccessful)
                    .map(Pledge::getUserId)
                    .distinct()
                    .count();
        }

        // Getters
        public int getTotalPledges() {
            return totalPledges;
        }

        public int getSuccessfulPledges() {
            return successfulPledges;
        }

        public int getRejectedPledges() {
            return rejectedPledges;
        }

        public double getTotalAmountRaised() {
            return totalAmountRaised;
        }

        public double getAveragePledgeAmount() {
            return averagePledgeAmount;
        }

        public int getUniqueBackers() {
            return uniqueBackers;
        }
    }
}