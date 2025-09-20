package data;

import models.Category;
import java.io.IOException;
import java.util.*;

/**
 * Data Access Object for Category model
 */
public class CategoryDAO {
    private static final String CSV_FILE = "categories.csv";
    private static final String[] HEADERS = { "categoryId", "name", "description" };

    public List<Category> loadCategories() throws IOException {
        List<Category> categories = new ArrayList<>();
        List<String[]> records = CSVUtil.readCSV(CSV_FILE);

        for (int i = (records.size() > 0 && isHeader(records.get(0)) ? 1 : 0); i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 3) {
                Category category = new Category();
                category.setCategoryId(record[0]);
                category.setName(record[1]);
                category.setDescription(record[2]);
                categories.add(category);
            }
        }

        return categories;
    }

    public void saveCategories(List<Category> categories) throws IOException {
        List<String[]> records = new ArrayList<>();
        records.add(HEADERS);

        for (Category category : categories) {
            String[] record = {
                    category.getCategoryId(),
                    category.getName(),
                    category.getDescription()
            };
            records.add(record);
        }

        CSVUtil.writeCSV(CSV_FILE, records);
    }

    public Category findById(String categoryId) throws IOException {
        List<Category> categories = loadCategories();
        return categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElse(null);
    }

    private boolean isHeader(String[] record) {
        return record.length >= 3 && record[0].equals("categoryId");
    }
}