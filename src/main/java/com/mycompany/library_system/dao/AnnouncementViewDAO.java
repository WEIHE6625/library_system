package com.mycompany.library_system.dao;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.AnnouncementView;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.IdGenerator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import me.tongfei.progressbar.ProgressBar;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
/**
 *
 * @author ZhenHao
 */
public class AnnouncementViewDAO {

    public static MapInterface<String, AnnouncementView> readFile(String filePath) {
        MapInterface<String, AnnouncementView> viewMap = new TreeMap<>();
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return viewMap;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            // Skip Header
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try (ProgressBar pb = new ProgressBar("Loading engagement data...", totalLines)) {
                while ((data = reader.readNext()) != null) {
                    pb.step();

                    if (data.length >= 3) {
                        try {
                            String announcementId = data[0].trim();
                            String userId = data[1].trim();
                            String timeStr = data[2].trim();
                            LocalDateTime timestamp = LocalDateTime.parse(timeStr);

                            AnnouncementView view = new AnnouncementView(announcementId, userId, timestamp);

                            String key = IdGenerator.getKeyId(announcementId, userId + "_" + timeStr);
                            viewMap.put(key, view);

                        } catch (Exception e) {
                            System.out.println("Skipping malformed view row: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Critical error reading view logs: " + e.getMessage());
        }

        return viewMap;
    }


    public static void recordView(String filePath, String announcementId, String userId) {
        File file = new File(filePath);
        boolean needsHeader = !file.exists() || file.length() == 0;

        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            if (needsHeader) {
                String[] header = {"AnnouncementID", "UserID", "ViewTimestamp"};
                writer.writeNext(header, false);
            }

            String[] data = {
                    announcementId,
                    userId,
                    LocalDateTime.now().toString()
            };
            writer.writeNext(data, false);
        } catch (IOException e) {
            System.err.println("Error recording view to " + filePath + ": " + e.getMessage());
        }
    }


    public static void writeFile(String filePath, MapInterface<String, AnnouncementView> viewMap) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            String[] header = {"AnnouncementID", "UserID", "ViewTimestamp"};
            writer.writeNext(header, false);

            for (MapInterface.Entry<String, AnnouncementView> entry : viewMap) {
                AnnouncementView v = entry.getValue();
                String[] data = {
                        v.getAnnouncementId(),
                        v.getUserId(),
                        v.getViewTimestamp().toString()
                };
                writer.writeNext(data, false);
            }
        } catch (IOException e) {
            System.err.println("Error performing bulk write to " + filePath + ": " + e.getMessage());
        }
    }
}