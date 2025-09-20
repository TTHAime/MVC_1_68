# Crowdfunding System - MVC Architecture

## Features

- User authentication (simple login system)
- Project listing with search, filter, and sort functionality
- Project details view with pledge functionality
- Statistics dashboard
- Business rule validation for pledges
- CSV-based data persistence

## MVC Architecture

### Models (`models/` package)

- **User.java**: User account information
- **Category.java**: Project categories
- **Project.java**: Crowdfunding projects with validation rules
- **RewardTier.java**: Reward levels for projects
- **Pledge.java**: User pledges with status tracking

### Views (`views/` package)

- **LoginView.java**: User authentication interface
- **ProjectListView.java**: Project listing with search/filter/sort
- **ProjectDetailView.java**: Detailed project view with pledge functionality
- **StatisticsView.java**: System statistics and analytics

### Controllers (`controllers/` package)

- **MainController.java**: Main application controller and navigation
- **ProjectController.java**: Project-related business logic
- **PledgeController.java**: Pledge processing and validation
- **StatisticsController.java**: Statistics calculation and reporting

### Data Access Layer (`data/` package)

- **CSVUtil.java**: CSV file reading/writing utilities
- **UserDAO.java**: User data access
- **CategoryDAO.java**: Category data access
- **ProjectDAO.java**: Project data access
- **RewardTierDAO.java**: Reward tier data access
- **PledgeDAO.java**: Pledge data access

## Sample Data

The system includes comprehensive sample data:

- **9 projects** across 5 categories
- **12 users** (including admin account)
- **27 reward tiers** (2-3 per project)
- **46 pledges** (40 successful + 6 rejected) demonstrating business rules

## Business Rules Implemented

1. **Project ID**: 8-digit number, first digit cannot be 0
2. **Deadline validation**: Must be in the future
3. **Pledge validation**:
   - Amount must be positive
   - Must meet minimum amount for selected reward tier
   - Tier must have available quantity
4. **Automatic updates**:
   - Project funding amount updated on successful pledge
   - Reward tier quantity reduced when pledged
5. **Rejection tracking**: Failed pledges are tracked with reasons


## Demo Accounts

- **admin** / **admin** - Administrator account
- **user1** / **password** - Regular user
- **user2** / **password** - Regular user

## Main Routes/Actions

1. **Login** → Authentication and session management
2. **Project List** → Browse, search, filter, and sort projects
3. **Project Details** → View project info and make pledges
4. **Statistics** → System analytics and performance metrics
5. **Pledge Processing** → Validate and process user pledges

## Key Views

1. **Project List View**: Shows all projects with filtering and sorting options
2. **Project Detail View**: Displays complete project information, progress bar, reward tiers, and pledge interface
3. **Statistics View**: Comprehensive system statistics including success rates, funding totals, and user activity

## File Structure

```
MVC_1_68/
├── Main.java                     # Application entry point
├── models/                       # Data models
├── views/                        # Swing UI components
├── controllers/                  # Business logic controllers
├── data/                         # Data access layer
├── *.csv                        # Sample data files
└── README.md                    # This file
```

## Technical Notes

- Uses Java Swing for GUI
- CSV files for simple data persistence
- MVC pattern for clean separation of concerns
- Business rule validation in controllers
- Simple authentication system
- No external dependencies required
