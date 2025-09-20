package data;

import models.RewardTier;
import java.io.IOException;
import java.util.*;

/**
 * Data Access Object for RewardTier model
 */
public class RewardTierDAO {
    private static final String CSV_FILE = "reward_tiers.csv";
    private static final String[] HEADERS = { "tierId", "projectId", "name", "minimumAmount",
            "totalQuantity", "remainingQuantity", "description" };

    public List<RewardTier> loadRewardTiers() throws IOException {
        List<RewardTier> tiers = new ArrayList<>();
        List<String[]> records = CSVUtil.readCSV(CSV_FILE);

        for (int i = (records.size() > 0 && isHeader(records.get(0)) ? 1 : 0); i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 7) {
                RewardTier tier = new RewardTier();
                tier.setTierId(record[0]);
                tier.setProjectId(record[1]);
                tier.setName(record[2]);
                tier.setMinimumAmount(Double.parseDouble(record[3]));
                tier.setTotalQuantity(Integer.parseInt(record[4]));
                tier.setRemainingQuantity(Integer.parseInt(record[5]));
                tier.setDescription(record[6]);
                tiers.add(tier);
            }
        }

        return tiers;
    }

    public void saveRewardTiers(List<RewardTier> tiers) throws IOException {
        List<String[]> records = new ArrayList<>();
        records.add(HEADERS);

        for (RewardTier tier : tiers) {
            String[] record = {
                    tier.getTierId(),
                    tier.getProjectId(),
                    tier.getName(),
                    String.valueOf(tier.getMinimumAmount()),
                    String.valueOf(tier.getTotalQuantity()),
                    String.valueOf(tier.getRemainingQuantity()),
                    tier.getDescription()
            };
            records.add(record);
        }

        CSVUtil.writeCSV(CSV_FILE, records);
    }

    public List<RewardTier> findByProject(String projectId) throws IOException {
        List<RewardTier> tiers = loadRewardTiers();
        return tiers.stream()
                .filter(t -> t.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public RewardTier findById(String tierId) throws IOException {
        List<RewardTier> tiers = loadRewardTiers();
        return tiers.stream()
                .filter(t -> t.getTierId().equals(tierId))
                .findFirst()
                .orElse(null);
    }

    public void updateRewardTier(RewardTier tier) throws IOException {
        List<RewardTier> tiers = loadRewardTiers();
        for (int i = 0; i < tiers.size(); i++) {
            if (tiers.get(i).getTierId().equals(tier.getTierId())) {
                tiers.set(i, tier);
                break;
            }
        }
        saveRewardTiers(tiers);
    }

    private boolean isHeader(String[] record) {
        return record.length >= 7 && record[0].equals("tierId");
    }
}