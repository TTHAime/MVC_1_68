package views;

import controllers.MainController;
import controllers.ProjectController;
import models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Project List View - Shows all projects with search, filter, and sort
 * functionality
 */
public class ProjectListView extends JFrame {
    private MainController mainController;
    private ProjectController projectController;

    // Components
    private JTextField searchField;
    private JComboBox<Category> categoryComboBox;
    private JComboBox<String> sortComboBox;
    private JTable projectTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton statisticsButton;
    private JButton logoutButton;

    // Data
    private List<Project> currentProjects;
    private List<Category> categories;

    public ProjectListView(MainController mainController) {
        this.mainController = mainController;
        this.projectController = mainController.getProjectController();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCategories();
    }

    private void initializeComponents() {
        setTitle("Crowdfunding System - Project List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        // Search and filter components
        searchField = new JTextField(20);
        categoryComboBox = new JComboBox<>();
        sortComboBox = new JComboBox<>(new String[] {
                "Name", "Newest", "Deadline", "Funding", "Progress"
        });

        // Table setup
        String[] columnNames = { "Project ID", "Name", "Category", "Goal", "Current", "Progress %", "Days Left",
                "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        projectTable = new JTable(tableModel);
        projectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons
        refreshButton = new JButton("Refresh");
        statisticsButton = new JButton("Statistics");
        logoutButton = new JButton("Logout");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with search, filter, and controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryComboBox);
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortComboBox);
        topPanel.add(refreshButton);

        // User info and action panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.add(new JLabel("Welcome, "
                + (mainController.getCurrentUser() != null ? mainController.getCurrentUser().getUsername() : "User")));
        userPanel.add(statisticsButton);
        userPanel.add(logoutButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // Center panel with project table
        JScrollPane scrollPane = new JScrollPane(projectTable);
        scrollPane.setPreferredSize(new Dimension(950, 400));

        // Bottom panel with instructions
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Double-click a project to view details"));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Search field - trigger search on enter
        searchField.addActionListener(e -> filterAndSortProjects());

        // Category combo box - trigger filter when selection changes
        categoryComboBox.addActionListener(e -> filterAndSortProjects());

        // Sort combo box - trigger sort when selection changes
        sortComboBox.addActionListener(e -> filterAndSortProjects());

        // Refresh button
        refreshButton.addActionListener(e -> refreshProjects());

        // Statistics button
        statisticsButton.addActionListener(e -> mainController.showStatisticsView());

        // Logout button
        logoutButton.addActionListener(e -> mainController.logout());

        // Double-click on table row to view project details
        projectTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = projectTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String projectId = (String) tableModel.getValueAt(selectedRow, 0);
                        mainController.showProjectDetailView(projectId);
                    }
                }
            }
        });
    }

    private void loadCategories() {
        categories = projectController.getAllCategories();
        categoryComboBox.removeAllItems();
        categoryComboBox.addItem(new Category("", "All Categories", ""));
        for (Category category : categories) {
            categoryComboBox.addItem(category);
        }
    }

    public void refreshProjects() {
        currentProjects = projectController.getAllProjects();
        filterAndSortProjects();
    }

    private void filterAndSortProjects() {
        if (currentProjects == null) {
            refreshProjects();
            return;
        }

        // Apply search filter
        String searchTerm = searchField.getText().trim();
        List<Project> filteredProjects = projectController.searchProjects(searchTerm);

        // Apply category filter
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
        if (selectedCategory != null && !selectedCategory.getCategoryId().isEmpty()) {
            filteredProjects = projectController.getProjectsByCategory(selectedCategory.getCategoryId());
            if (!searchTerm.isEmpty()) {
                // Apply search to category-filtered results
                filteredProjects = filteredProjects.stream()
                        .filter(p -> p.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                p.getDescription().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);
            }
        }

        // Apply sorting
        String sortBy = (String) sortComboBox.getSelectedItem();
        if (sortBy != null) {
            filteredProjects = projectController.sortProjects(filteredProjects, sortBy);
        }

        // Update table
        updateTable(filteredProjects);
    }

    private void updateTable(List<Project> projects) {
        tableModel.setRowCount(0);

        for (Project project : projects) {
            Category category = projectController.getCategory(project.getCategoryId());
            String categoryName = category != null ? category.getName() : "Unknown";

            Object[] row = {
                    project.getProjectId(),
                    project.getName(),
                    categoryName,
                    String.format("$%.2f", project.getGoalAmount()),
                    String.format("$%.2f", project.getCurrentAmount()),
                    String.format("%.1f%%", project.getFundingProgress()),
                    project.isActive() ? String.valueOf(project.getDaysRemaining()) : "Ended",
                    project.getStatusDescription() // Display detailed status
            };
            tableModel.addRow(row);
        }
    }
}