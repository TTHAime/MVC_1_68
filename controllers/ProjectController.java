package controllers;

import models.*;
import data.*;
import java.io.IOException;
import java.util.*;

/**
 * Project Controller - handles project-related business logic
 */
public class ProjectController {
    private MainController mainController;
    private ProjectDAO projectDAO;
    private CategoryDAO categoryDAO;
    private RewardTierDAO rewardTierDAO;
    private PledgeDAO pledgeDAO;

    public ProjectController(MainController mainController) {
        this.mainController = mainController;
        this.projectDAO = new ProjectDAO();
        this.categoryDAO = new CategoryDAO();
        this.rewardTierDAO = new RewardTierDAO();
        this.pledgeDAO = new PledgeDAO();
    }

    /**
     * Get all projects
     */
    public List<Project> getAllProjects() {
        try {
            return projectDAO.loadProjects();
        } catch (IOException e) {
            mainController.showError("Error loading projects: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get project by ID
     */
    public Project getProject(String projectId) {
        try {
            return projectDAO.findById(projectId);
        } catch (IOException e) {
            mainController.showError("Error loading project: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get projects by category
     */
    public List<Project> getProjectsByCategory(String categoryId) {
        try {
            if (categoryId == null || categoryId.isEmpty()) {
                return getAllProjects();
            }
            return projectDAO.findByCategory(categoryId);
        } catch (IOException e) {
            mainController.showError("Error loading projects by category: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Search projects by name
     */
    public List<Project> searchProjects(String searchTerm) {
        List<Project> allProjects = getAllProjects();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return allProjects;
        }

        String term = searchTerm.toLowerCase();
        return allProjects.stream()
                .filter(p -> p.getName().toLowerCase().contains(term) ||
                        p.getDescription().toLowerCase().contains(term))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Sort projects by different criteria
     */
    public List<Project> sortProjects(List<Project> projects, String sortBy) {
        List<Project> sorted = new ArrayList<>(projects);

        switch (sortBy.toLowerCase()) {
            case "newest":
                // Since we don't have creation date, sort by project ID (newer IDs = newer
                // projects)
                sorted.sort((p1, p2) -> p2.getProjectId().compareTo(p1.getProjectId()));
                break;
            case "deadline":
                sorted.sort(Comparator.comparing(Project::getDeadline));
                break;
            case "funding":
                sorted.sort((p1, p2) -> Double.compare(p2.getCurrentAmount(), p1.getCurrentAmount()));
                break;
            case "progress":
                sorted.sort((p1, p2) -> Double.compare(p2.getFundingProgress(), p1.getFundingProgress()));
                break;
            default:
                // Default sort by name
                sorted.sort(Comparator.comparing(Project::getName));
        }

        return sorted;
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        try {
            return categoryDAO.loadCategories();
        } catch (IOException e) {
            mainController.showError("Error loading categories: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get category by ID
     */
    public Category getCategory(String categoryId) {
        try {
            return categoryDAO.findById(categoryId);
        } catch (IOException e) {
            mainController.showError("Error loading category: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get reward tiers for a project
     */
    public List<RewardTier> getRewardTiers(String projectId) {
        try {
            return rewardTierDAO.findByProject(projectId);
        } catch (IOException e) {
            mainController.showError("Error loading reward tiers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get project statistics
     */
    public ProjectStatistics getProjectStatistics(String projectId) {
        try {
            Project project = projectDAO.findById(projectId);
            if (project == null)
                return null;

            List<Pledge> pledges = pledgeDAO.findByProject(projectId);
            List<RewardTier> tiers = rewardTierDAO.findByProject(projectId);

            return new ProjectStatistics(project, pledges, tiers);
        } catch (IOException e) {
            mainController.showError("Error loading project statistics: " + e.getMessage());
            return null;
        }
    }

    /**
     * Inner class for project statistics
     */
    public static class ProjectStatistics {
        private Project project;
        private int totalPledges;
        private int successfulPledges;
        private int rejectedPledges;
        private double totalAmount;
        private int totalBackers;

        public ProjectStatistics(Project project, List<Pledge> pledges, List<RewardTier> tiers) {
            this.project = project;
            this.totalPledges = pledges.size();
            this.successfulPledges = (int) pledges.stream().filter(Pledge::isSuccessful).count();
            this.rejectedPledges = (int) pledges.stream().filter(Pledge::isRejected).count();
            this.totalAmount = pledges.stream().filter(Pledge::isSuccessful).mapToDouble(Pledge::getAmount).sum();
            this.totalBackers = (int) pledges.stream().filter(Pledge::isSuccessful).map(Pledge::getUserId).distinct()
                    .count();
        }

        // Getters
        public Project getProject() {
            return project;
        }

        public int getTotalPledges() {
            return totalPledges;
        }

        public int getSuccessfulPledges() {
            return successfulPledges;
        }

        public int getRejectedPledges() {
            return rejectedPledges;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public int getTotalBackers() {
            return totalBackers;
        }
    }
}