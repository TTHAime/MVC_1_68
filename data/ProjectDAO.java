package data;

import models.Project;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Data Access Object for Project model
 */
public class ProjectDAO {
    private static final String CSV_FILE = "projects.csv";
    private static final String[] HEADERS = { "projectId", "name", "goalAmount", "deadline",
            "currentAmount", "categoryId", "description", "creatorId" };

    public List<Project> loadProjects() throws IOException {
        List<Project> projects = new ArrayList<>();
        List<String[]> records = CSVUtil.readCSV(CSV_FILE);

        for (int i = (records.size() > 0 && isHeader(records.get(0)) ? 1 : 0); i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 8) {
                Project project = new Project();
                project.setProjectId(record[0]);
                project.setName(record[1]);
                project.setGoalAmount(Double.parseDouble(record[2]));
                project.setDeadline(LocalDate.parse(record[3], Project.DATE_FORMAT));
                project.setCurrentAmount(Double.parseDouble(record[4]));
                project.setCategoryId(record[5]);
                project.setDescription(record[6]);
                project.setCreatorId(record[7]);
                projects.add(project);
            }
        }

        return projects;
    }

    public void saveProjects(List<Project> projects) throws IOException {
        List<String[]> records = new ArrayList<>();
        records.add(HEADERS);

        for (Project project : projects) {
            String[] record = {
                    project.getProjectId(),
                    project.getName(),
                    String.valueOf(project.getGoalAmount()),
                    project.getDeadline().format(Project.DATE_FORMAT),
                    String.valueOf(project.getCurrentAmount()),
                    project.getCategoryId(),
                    project.getDescription(),
                    project.getCreatorId()
            };
            records.add(record);
        }

        CSVUtil.writeCSV(CSV_FILE, records);
    }

    public Project findById(String projectId) throws IOException {
        List<Project> projects = loadProjects();
        return projects.stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .findFirst()
                .orElse(null);
    }

    public List<Project> findByCategory(String categoryId) throws IOException {
        List<Project> projects = loadProjects();
        return projects.stream()
                .filter(p -> p.getCategoryId().equals(categoryId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void updateProject(Project project) throws IOException {
        List<Project> projects = loadProjects();
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectId().equals(project.getProjectId())) {
                projects.set(i, project);
                break;
            }
        }
        saveProjects(projects);
    }

    private boolean isHeader(String[] record) {
        return record.length >= 8 && record[0].equals("projectId");
    }
}