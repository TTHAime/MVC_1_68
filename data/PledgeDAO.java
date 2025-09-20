package data;

import models.Pledge;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Data Access Object for Pledge model
 */
public class PledgeDAO {
    private static final String CSV_FILE = "pledges.csv";
    private static final String[] HEADERS = { "pledgeId", "userId", "projectId", "pledgeTime",
            "amount", "rewardTierId", "status", "rejectionReason" };

    public List<Pledge> loadPledges() throws IOException {
        List<Pledge> pledges = new ArrayList<>();
        List<String[]> records = CSVUtil.readCSV(CSV_FILE);

        for (int i = (records.size() > 0 && isHeader(records.get(0)) ? 1 : 0); i < records.size(); i++) {
            String[] record = records.get(i);
            if (record.length >= 8) {
                Pledge pledge = new Pledge();
                pledge.setPledgeId(record[0]);
                pledge.setUserId(record[1]);
                pledge.setProjectId(record[2]);
                pledge.setPledgeTime(LocalDateTime.parse(record[3], Pledge.DATETIME_FORMAT));
                pledge.setAmount(Double.parseDouble(record[4]));
                pledge.setRewardTierId(record[5].isEmpty() ? null : record[5]);
                pledge.setStatus(Pledge.PledgeStatus.valueOf(record[6]));
                pledge.setRejectionReason(record[7].isEmpty() ? null : record[7]);
                pledges.add(pledge);
            }
        }

        return pledges;
    }

    public void savePledges(List<Pledge> pledges) throws IOException {
        List<String[]> records = new ArrayList<>();
        records.add(HEADERS);

        for (Pledge pledge : pledges) {
            String[] record = {
                    pledge.getPledgeId(),
                    pledge.getUserId(),
                    pledge.getProjectId(),
                    pledge.getPledgeTime().format(Pledge.DATETIME_FORMAT),
                    String.valueOf(pledge.getAmount()),
                    pledge.getRewardTierId() != null ? pledge.getRewardTierId() : "",
                    pledge.getStatus().toString(),
                    pledge.getRejectionReason() != null ? pledge.getRejectionReason() : ""
            };
            records.add(record);
        }

        CSVUtil.writeCSV(CSV_FILE, records);
    }

    public void addPledge(Pledge pledge) throws IOException {
        List<Pledge> pledges = loadPledges();
        pledges.add(pledge);
        savePledges(pledges);
    }

    public List<Pledge> findByProject(String projectId) throws IOException {
        List<Pledge> pledges = loadPledges();
        return pledges.stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Pledge> findByUser(String userId) throws IOException {
        List<Pledge> pledges = loadPledges();
        return pledges.stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Pledge> findSuccessfulPledges() throws IOException {
        List<Pledge> pledges = loadPledges();
        return pledges.stream()
                .filter(Pledge::isSuccessful)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Pledge> findRejectedPledges() throws IOException {
        List<Pledge> pledges = loadPledges();
        return pledges.stream()
                .filter(Pledge::isRejected)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private boolean isHeader(String[] record) {
        return record.length >= 8 && record[0].equals("pledgeId");
    }
}