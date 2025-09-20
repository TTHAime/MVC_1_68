package views;

import controllers.MainController;
import controllers.ProjectController;
import controllers.PledgeController;
import models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Project Detail View - Shows detailed project information and allows pledging
 */
public class ProjectDetailView extends JFrame {
    private MainController mainController;
    private ProjectController projectController;
    private PledgeController pledgeController;

    // Components
    private JLabel projectNameLabel;
    private JLabel categoryLabel;
    private JLabel creatorLabel;
    private JTextArea descriptionArea;
    private JLabel goalLabel;
    private JLabel currentLabel;
    private JProgressBar progressBar;
    private JLabel daysLeftLabel;
    private JLabel statusLabel;

    // Reward tiers
    private JList<RewardTier> rewardTierList;
    private DefaultListModel<RewardTier> rewardTierModel;

    // Pledge components
    private JTextField pledgeAmountField;
    private JButton pledgeButton;
    private JButton backButton;

    // Current project
    private Project currentProject;
    private List<RewardTier> rewardTiers;

    public ProjectDetailView(MainController mainController) {
        this.mainController = mainController;
        this.projectController = mainController.getProjectController();
        this.pledgeController = mainController.getPledgeController();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        setTitle("Project Details");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        // Project info components
        projectNameLabel = new JLabel();
        projectNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        categoryLabel = new JLabel();
        creatorLabel = new JLabel();
        descriptionArea = new JTextArea(5, 40);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);

        goalLabel = new JLabel();
        currentLabel = new JLabel();
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        daysLeftLabel = new JLabel();
        statusLabel = new JLabel();

        // Reward tier list
        rewardTierModel = new DefaultListModel<>();
        rewardTierList = new JList<>(rewardTierModel);
        rewardTierList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rewardTierList.setCellRenderer(new RewardTierCellRenderer());

        // Pledge components
        pledgeAmountField = new JTextField(10);
        pledgeButton = new JButton("Make Pledge");
        backButton = new JButton("Back to List");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with project basic info
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(projectNameLabel);

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.add(new JLabel("Category:"));
        infoPanel.add(categoryLabel);
        infoPanel.add(new JLabel("Creator:"));
        infoPanel.add(creatorLabel);

        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        // Center panel with description and funding info
        JPanel centerPanel = new JPanel(new BorderLayout());

        // Description
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        // Funding info
        JPanel fundingPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        fundingPanel.setBorder(BorderFactory.createTitledBorder("Funding Information"));
        fundingPanel.add(goalLabel);
        fundingPanel.add(currentLabel);
        fundingPanel.add(progressBar);
        fundingPanel.add(daysLeftLabel);
        fundingPanel.add(statusLabel);

        centerPanel.add(descPanel, BorderLayout.CENTER);
        centerPanel.add(fundingPanel, BorderLayout.EAST);

        // Reward tiers panel
        JPanel rewardPanel = new JPanel(new BorderLayout());
        rewardPanel.setBorder(BorderFactory.createTitledBorder("Reward Tiers"));
        rewardPanel.add(new JScrollPane(rewardTierList), BorderLayout.CENTER);

        // Pledge panel
        JPanel pledgePanel = new JPanel(new FlowLayout());
        pledgePanel.setBorder(BorderFactory.createTitledBorder("Make a Pledge"));
        pledgePanel.add(new JLabel("Amount: $"));
        pledgePanel.add(pledgeAmountField);
        pledgePanel.add(pledgeButton);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(pledgePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Main layout
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerPanel, rewardPanel);
        mainSplit.setDividerLocation(300);

        add(topPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        backButton.addActionListener(e -> mainController.showProjectListView());

        pledgeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makePledge();
            }
        });

        // Allow Enter key in amount field to trigger pledge
        pledgeAmountField.addActionListener(e -> makePledge());
    }

    public void loadProject(String projectId) {
        currentProject = projectController.getProject(projectId);
        if (currentProject == null) {
            mainController.showError("Project not found");
            return;
        }

        rewardTiers = projectController.getRewardTiers(projectId);
        updateProjectDisplay();
        updateRewardTiers();
    }

    private void updateProjectDisplay() {
        if (currentProject == null)
            return;

        projectNameLabel.setText(currentProject.getName());

        Category category = projectController.getCategory(currentProject.getCategoryId());
        categoryLabel.setText(category != null ? category.getName() : "Unknown");

        // For simplicity, showing creator ID - in real app would show creator name
        creatorLabel.setText(currentProject.getCreatorId());

        descriptionArea.setText(currentProject.getDescription());

        goalLabel.setText(String.format("Goal: $%.2f", currentProject.getGoalAmount()));
        currentLabel.setText(String.format("Raised: $%.2f", currentProject.getCurrentAmount()));

        int progress = (int) currentProject.getFundingProgress();
        progressBar.setValue(progress);
        progressBar.setString(progress + "%");

        // Display status based on project success
        String projectStatus = currentProject.getProjectStatus();
        String statusDescription = currentProject.getStatusDescription();

        if (projectStatus.equals("SUCCESS")) {
            // Project successful
            statusLabel.setText("Status: " + statusDescription);
            statusLabel.setForeground(new Color(0, 150, 0)); // Dark green

            if (currentProject.isActive()) {
                daysLeftLabel.setText(
                        String.format("Days remaining: %d (Goal reached!)", currentProject.getDaysRemaining()));
                pledgeButton.setEnabled(true); // Can still pledge
            } else {
                daysLeftLabel.setText("Project completed successfully");
                pledgeButton.setEnabled(false);
            }
        } else if (currentProject.isActive()) {
            // Project still fundraising
            daysLeftLabel.setText(String.format("Days remaining: %d", currentProject.getDaysRemaining()));
            statusLabel.setText("Status: " + statusDescription);
            statusLabel.setForeground(Color.BLUE);
            pledgeButton.setEnabled(true);
        } else {
            // Project deadline passed without reaching goal
            daysLeftLabel.setText("Project ended");
            statusLabel.setText("Status: " + statusDescription);
            statusLabel.setForeground(Color.RED);
            pledgeButton.setEnabled(false);
        }
    }

    private void updateRewardTiers() {
        rewardTierModel.clear();
        if (rewardTiers != null) {
            for (RewardTier tier : rewardTiers) {
                rewardTierModel.addElement(tier);
            }
        }
    }

    private void makePledge() {
        if (currentProject == null || !currentProject.isActive()) {
            mainController.showError("Cannot pledge to inactive project");
            return;
        }

        try {
            String amountText = pledgeAmountField.getText().trim();
            if (amountText.isEmpty()) {
                mainController.showError("Please enter pledge amount");
                return;
            }

            double amount = Double.parseDouble(amountText);

            // Get selected reward tier (optional)
            RewardTier selectedTier = rewardTierList.getSelectedValue();
            String tierId = selectedTier != null ? selectedTier.getTierId() : null;

            // Process pledge
            PledgeController.PledgeResult result = pledgeController.processPledge(
                    currentProject.getProjectId(), amount, tierId);

            if (result.isSuccess()) {
                mainController.showSuccess(result.getMessage());
                pledgeAmountField.setText("");

                // Refresh project data
                loadProject(currentProject.getProjectId());
            } else {
                mainController.showError(result.getMessage());
            }

        } catch (NumberFormatException e) {
            mainController.showError("Please enter a valid amount");
        }
    }

    /**
     * Custom cell renderer for reward tiers
     */
    private class RewardTierCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof RewardTier) {
                RewardTier tier = (RewardTier) value;
                String text = String.format("<html><b>%s</b> - $%.2f<br/>%s<br/><i>%d remaining</i></html>",
                        tier.getName(), tier.getMinimumAmount(), tier.getDescription(), tier.getRemainingQuantity());
                setText(text);

                if (!tier.isAvailable()) {
                    setForeground(Color.GRAY);
                }
            }

            return this;
        }
    }
}