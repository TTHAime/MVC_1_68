package views;

import controllers.MainController;
import controllers.StatisticsController;
import controllers.StatisticsController.SystemStatistics;
import controllers.StatisticsController.ProjectPerformance;
import controllers.StatisticsController.UserActivity;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Statistics View - Shows comprehensive statistics for the crowdfunding system
 */
public class StatisticsView extends JFrame {
    private MainController mainController;
    private StatisticsController statisticsController;

    // Components for system overview
    private JLabel totalProjectsLabel;
    private JLabel totalUsersLabel;
    private JLabel totalPledgesLabel;
    private JLabel successfulPledgesLabel;
    private JLabel rejectedPledgesLabel;
    private JLabel totalRaisedLabel;
    private JLabel averagePledgeLabel;
    private JLabel successRateLabel;

    // Tables for detailed statistics
    private JTable projectPerformanceTable;
    private DefaultTableModel projectTableModel;
    private JTable userActivityTable;
    private DefaultTableModel userTableModel;

    // Controls
    private JButton refreshButton;
    private JButton backButton;

    public StatisticsView(MainController mainController) {
        this.mainController = mainController;
        this.statisticsController = mainController.getStatisticsController();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("Crowdfunding System - Statistics");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // System statistics labels
        totalProjectsLabel = new JLabel("0");
        totalUsersLabel = new JLabel("0");
        totalPledgesLabel = new JLabel("0");
        successfulPledgesLabel = new JLabel("0");
        rejectedPledgesLabel = new JLabel("0");
        totalRaisedLabel = new JLabel("$0.00");
        averagePledgeLabel = new JLabel("$0.00");
        successRateLabel = new JLabel("0%");

        // Project performance table
        String[] projectColumns = { "Project Name", "Goal", "Raised", "Progress %", "Pledges", "Success", "Rejected",
                "Backers" };
        projectTableModel = new DefaultTableModel(projectColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        projectPerformanceTable = new JTable(projectTableModel);

        // User activity table
        String[] userColumns = { "Username", "Total Pledges", "Successful", "Rejected", "Total Pledged",
                "Projects Supported" };
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userActivityTable = new JTable(userTableModel);

        // Control buttons
        refreshButton = new JButton("Refresh");
        backButton = new JButton("Back to Projects");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with title and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("System Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(refreshButton);
        controlPanel.add(backButton);

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(controlPanel, BorderLayout.EAST);

        // System overview panel
        JPanel overviewPanel = createOverviewPanel();

        // Tables panel
        JPanel tablesPanel = createTablesPanel();

        // Main content with split pane
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, overviewPanel, tablesPanel);
        mainSplit.setDividerLocation(200);

        add(topPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("System Overview"));

        // Create grid for statistics
        JPanel statsGrid = new JPanel(new GridLayout(2, 4, 10, 10));
        statsGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Row 1
        statsGrid.add(createStatCard("Total Projects", totalProjectsLabel));
        statsGrid.add(createStatCard("Total Users", totalUsersLabel));
        statsGrid.add(createStatCard("Total Pledges", totalPledgesLabel));
        statsGrid.add(createStatCard("Success Rate", successRateLabel));

        // Row 2
        statsGrid.add(createStatCard("Successful Pledges", successfulPledgesLabel));
        statsGrid.add(createStatCard("Rejected Pledges", rejectedPledgesLabel));
        statsGrid.add(createStatCard("Total Raised", totalRaisedLabel));
        statsGrid.add(createStatCard("Average Pledge", averagePledgeLabel));

        panel.add(statsGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLoweredBevelBorder());
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create tabbed pane for different statistics
        JTabbedPane tabbedPane = new JTabbedPane();

        // Project performance tab
        JScrollPane projectScroll = new JScrollPane(projectPerformanceTable);
        tabbedPane.addTab("Project Performance", projectScroll);

        // User activity tab
        JScrollPane userScroll = new JScrollPane(userActivityTable);
        tabbedPane.addTab("User Activity", userScroll);

        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> refreshStatistics());
        backButton.addActionListener(e -> mainController.showProjectListView());
    }

    public void refreshStatistics() {
        // Update system overview
        updateSystemOverview();

        // Update detailed tables
        updateProjectPerformanceTable();
        updateUserActivityTable();
    }

    private void updateSystemOverview() {
        SystemStatistics stats = statisticsController.getSystemStatistics();

        totalProjectsLabel.setText(String.valueOf(stats.getTotalProjects()));
        totalUsersLabel.setText(String.valueOf(stats.getTotalUsers()));
        totalPledgesLabel.setText(String.valueOf(stats.getTotalPledges()));
        successfulPledgesLabel.setText(String.valueOf(stats.getSuccessfulPledges()));
        rejectedPledgesLabel.setText(String.valueOf(stats.getRejectedPledges()));
        totalRaisedLabel.setText(String.format("$%.2f", stats.getTotalAmountRaised()));
        averagePledgeLabel.setText(String.format("$%.2f", stats.getAveragePledgeAmount()));
        successRateLabel.setText(String.format("%.1f%%", stats.getSuccessRate()));

        // Color code success rate
        if (stats.getSuccessRate() >= 80) {
            successRateLabel.setForeground(Color.GREEN);
        } else if (stats.getSuccessRate() >= 60) {
            successRateLabel.setForeground(Color.ORANGE);
        } else {
            successRateLabel.setForeground(Color.RED);
        }
    }

    private void updateProjectPerformanceTable() {
        projectTableModel.setRowCount(0);

        List<ProjectPerformance> performance = statisticsController.getProjectPerformanceStats();
        for (ProjectPerformance perf : performance) {
            Object[] row = {
                    perf.getProject().getName(),
                    String.format("$%.2f", perf.getProject().getGoalAmount()),
                    String.format("$%.2f", perf.getTotalRaised()),
                    String.format("%.1f%%", perf.getFundingPercentage()),
                    perf.getTotalPledges(),
                    perf.getSuccessfulPledges(),
                    perf.getRejectedPledges(),
                    perf.getUniqueBackers()
            };
            projectTableModel.addRow(row);
        }
    }

    private void updateUserActivityTable() {
        userTableModel.setRowCount(0);

        List<UserActivity> activity = statisticsController.getUserActivityStats();
        for (UserActivity userAct : activity) {
            Object[] row = {
                    userAct.getUser().getUsername(),
                    userAct.getTotalPledges(),
                    userAct.getSuccessfulPledges(),
                    userAct.getRejectedPledges(),
                    String.format("$%.2f", userAct.getTotalPledged()),
                    userAct.getProjectsSupported()
            };
            userTableModel.addRow(row);
        }
    }
}