package controllers;

import models.User;
import data.UserDAO;
import views.*;
import javax.swing.*;
import java.io.IOException;

/**
 * Main Controller - coordinates the application flow
 * Handles authentication and navigation between views
 */
public class MainController {
    private User currentUser;
    private UserDAO userDAO;
    private ProjectController projectController;
    private PledgeController pledgeController;
    private StatisticsController statisticsController;

    // Views
    private LoginView loginView;
    private ProjectListView projectListView;
    private ProjectDetailView projectDetailView;
    private StatisticsView statisticsView;

    public MainController() {
        this.userDAO = new UserDAO();
        this.projectController = new ProjectController(this);
        this.pledgeController = new PledgeController(this);
        this.statisticsController = new StatisticsController(this);
    }

    /**
     * Start the application
     */
    public void start() {
        showLoginView();
    }

    /**
     * Show login screen
     */
    public void showLoginView() {
        if (loginView == null) {
            loginView = new LoginView(this);
        }
        hideAllViews();
        loginView.setVisible(true);
    }

    /**
     * Show project list view
     */
    public void showProjectListView() {
        if (projectListView == null) {
            projectListView = new ProjectListView(this);
        }
        hideAllViews();
        projectListView.setVisible(true);
        projectListView.refreshProjects();
    }

    /**
     * Show project detail view
     */
    public void showProjectDetailView(String projectId) {
        if (projectDetailView == null) {
            projectDetailView = new ProjectDetailView(this);
        }
        hideAllViews();
        projectDetailView.setVisible(true);
        projectDetailView.loadProject(projectId);
    }

    /**
     * Show statistics view
     */
    public void showStatisticsView() {
        if (statisticsView == null) {
            statisticsView = new StatisticsView(this);
        }
        hideAllViews();
        statisticsView.setVisible(true);
        statisticsView.refreshStatistics();
    }

    /**
     * Attempt to login user
     */
    public boolean login(String username, String password) {
        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                this.currentUser = user;
                return true;
            }
        } catch (IOException e) {
            showError("Error accessing user data: " + e.getMessage());
        }
        return false;
    }

    /**
     * Logout current user
     */
    public void logout() {
        this.currentUser = null;
        showLoginView();
    }

    /**
     * Get current logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get project controller
     */
    public ProjectController getProjectController() {
        return projectController;
    }

    /**
     * Get pledge controller
     */
    public PledgeController getPledgeController() {
        return pledgeController;
    }

    /**
     * Get statistics controller
     */
    public StatisticsController getStatisticsController() {
        return statisticsController;
    }

    /**
     * Show error message
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show success message
     */
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hide all views
     */
    private void hideAllViews() {
        if (loginView != null)
            loginView.setVisible(false);
        if (projectListView != null)
            projectListView.setVisible(false);
        if (projectDetailView != null)
            projectDetailView.setVisible(false);
        if (statisticsView != null)
            statisticsView.setVisible(false);
    }
}