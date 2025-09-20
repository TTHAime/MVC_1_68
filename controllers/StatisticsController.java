package controllers;

import models.*;
import data.*;
import java.io.IOException;
import java.util.*;

/**
 * Statistics Controller - handles statistical analysis and reporting
 */
public class StatisticsController {
    private MainController mainController;
    private PledgeDAO pledgeDAO;
    private ProjectDAO projectDAO;
    private UserDAO userDAO;

    public StatisticsController(MainController mainController) {
        this.mainController = mainController;
        this.pledgeDAO = new PledgeDAO();
        this.projectDAO = new ProjectDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Get overall system statistics
     */
    public SystemStatistics getSystemStatistics() {
        try {
            List<Pledge> allPledges = pledgeDAO.loadPledges();
            List<Project> allProjects = projectDAO.loadProjects();
            List<User> allUsers = userDAO.loadUsers();

            return new SystemStatistics(allPledges, allProjects, allUsers);
        } catch (IOException e) {
            mainController.showError("Error loading system statistics: " + e.getMessage());
            return new SystemStatistics(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

    /**
     * Get project performance statistics
     */
    public List<ProjectPerformance> getProjectPerformanceStats() {
        try {
            List<Project> projects = projectDAO.loadProjects();
            List<ProjectPerformance> performance = new ArrayList<>();

            for (Project project : projects) {
                List<Pledge> projectPledges = pledgeDAO.findByProject(project.getProjectId());
                performance.add(new ProjectPerformance(project, projectPledges));
            }

            // Sort by funding percentage descending
            performance.sort((p1, p2) -> Double.compare(p2.getFundingPercentage(), p1.getFundingPercentage()));

            return performance;
        } catch (IOException e) {
            mainController.showError("Error loading project performance: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get user activity statistics
     */
    public List<UserActivity> getUserActivityStats() {
        try {
            List<User> users = userDAO.loadUsers();
            List<UserActivity> activity = new ArrayList<>();

            for (User user : users) {
                List<Pledge> userPledges = pledgeDAO.findByUser(user.getUserId());
                activity.add(new UserActivity(user, userPledges));
            }

            // Sort by total pledged amount descending
            activity.sort((u1, u2) -> Double.compare(u2.getTotalPledged(), u1.getTotalPledged()));

            return activity;
        } catch (IOException e) {
            mainController.showError("Error loading user activity: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * System-wide statistics
     */
    public static class SystemStatistics {
        private int totalProjects;
        private int totalUsers;
        private int totalPledges;
        private int successfulPledges;
        private int rejectedPledges;
        private double totalAmountRaised;
        private double averagePledgeAmount;
        private int activeProjects;
        private int completedProjects;
        private int successfulProjects; // Projects that reached their goal
        private int failedProjects; // Projects that failed

        public SystemStatistics(List<Pledge> pledges, List<Project> projects, List<User> users) {
            this.totalProjects = projects.size();
            this.totalUsers = users.size();
            this.totalPledges = pledges.size();
            this.successfulPledges = (int) pledges.stream().filter(Pledge::isSuccessful).count();
            this.rejectedPledges = (int) pledges.stream().filter(Pledge::isRejected).count();
            this.totalAmountRaised = pledges.stream()
                    .filter(Pledge::isSuccessful)
                    .mapToDouble(Pledge::getAmount)
                    .sum();
            this.averagePledgeAmount = successfulPledges > 0 ? totalAmountRaised / successfulPledges : 0;
            this.activeProjects = (int) projects.stream().filter(Project::isActive).count();
            this.completedProjects = totalProjects - activeProjects;

            // Calculate successful and failed projects
            this.successfulProjects = (int) projects.stream().filter(Project::isFundingGoalReached).count();
            this.failedProjects = (int) projects.stream()
                    .filter(p -> !p.isActive() && !p.isFundingGoalReached()).count();
        }

        // Getters
        public int getTotalProjects() {
            return totalProjects;
        }

        public int getTotalUsers() {
            return totalUsers;
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

        public double getTotalAmountRaised() {
            return totalAmountRaised;
        }

        public double getAveragePledgeAmount() {
            return averagePledgeAmount;
        }

        public int getActiveProjects() {
            return activeProjects;
        }

        public int getCompletedProjects() {
            return completedProjects;
        }

        public double getSuccessRate() {
            return totalPledges > 0 ? (double) successfulPledges / totalPledges * 100 : 0;
        }

        public int getSuccessfulProjects() {
            return successfulProjects;
        }

        public int getFailedProjects() {
            return failedProjects;
        }

        public double getProjectSuccessRate() {
            return totalProjects > 0 ? (double) successfulProjects / totalProjects * 100 : 0;
        }
    }

    /**
     * Individual project performance
     */
    public static class ProjectPerformance {
        private Project project;
        private int totalPledges;
        private int successfulPledges;
        private int rejectedPledges;
        private double totalRaised;
        private int uniqueBackers;

        public ProjectPerformance(Project project, List<Pledge> pledges) {
            this.project = project;
            this.totalPledges = pledges.size();
            this.successfulPledges = (int) pledges.stream().filter(Pledge::isSuccessful).count();
            this.rejectedPledges = (int) pledges.stream().filter(Pledge::isRejected).count();
            this.totalRaised = pledges.stream()
                    .filter(Pledge::isSuccessful)
                    .mapToDouble(Pledge::getAmount)
                    .sum();
            this.uniqueBackers = (int) pledges.stream()
                    .filter(Pledge::isSuccessful)
                    .map(Pledge::getUserId)
                    .distinct()
                    .count();
        }

        public double getFundingPercentage() {
            return project.getGoalAmount() > 0 ? (totalRaised / project.getGoalAmount()) * 100 : 0;
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

        public double getTotalRaised() {
            return totalRaised;
        }

        public int getUniqueBackers() {
            return uniqueBackers;
        }
    }

    /**
     * Individual user activity
     */
    public static class UserActivity {
        private User user;
        private int totalPledges;
        private int successfulPledges;
        private int rejectedPledges;
        private double totalPledged;
        private int projectsSupported;

        public UserActivity(User user, List<Pledge> pledges) {
            this.user = user;
            this.totalPledges = pledges.size();
            this.successfulPledges = (int) pledges.stream().filter(Pledge::isSuccessful).count();
            this.rejectedPledges = (int) pledges.stream().filter(Pledge::isRejected).count();
            this.totalPledged = pledges.stream()
                    .filter(Pledge::isSuccessful)
                    .mapToDouble(Pledge::getAmount)
                    .sum();
            this.projectsSupported = (int) pledges.stream()
                    .filter(Pledge::isSuccessful)
                    .map(Pledge::getProjectId)
                    .distinct()
                    .count();
        }

        // Getters
        public User getUser() {
            return user;
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

        public double getTotalPledged() {
            return totalPledged;
        }

        public int getProjectsSupported() {
            return projectsSupported;
        }
    }
}