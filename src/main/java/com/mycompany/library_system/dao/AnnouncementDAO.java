package com.mycompany.library_system.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.*;
import com.mycompany.library_system.utility.DataProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import me.tongfei.progressbar.ProgressBar;
/**
 *
 * @author ZhenHao
 */
public class AnnouncementDAO {
// Read Announcement
    public static MapInterface<String, Announcement> readFile(String filePath){
        MapInterface<String, Announcement> announcementTree = new TreeMap<>();

        try(CSVReader reader = new CSVReader(new FileReader(filePath))){
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try(ProgressBar pb = new ProgressBar("Loading announcements...", totalLines)){
                while((data = reader.readNext()) != null){
                    pb.step();

                    if(data.length>=9){
                        try{
                            String type = data[0].trim();
                            String id =  data[1].trim();
                            String title = data[2].trim();
                            String content = data[3].trim();
                            String adminId = data[4].trim();
                            LocalDate createdDate = LocalDate.parse(data[5].trim());
                            String editAdminId = data[6].trim();

                            String rawEditDate = data[7].trim();
                            LocalDate editDate = null;
                            if (!rawEditDate.equals("-")) {
                                editDate = LocalDate.parse(rawEditDate);
                            }

                            Announcement announcement = null;

                            if (type.equalsIgnoreCase("BROADCAST")) {
                                announcement = new AnnouncementBroadcast(id, title, content, adminId, createdDate, editAdminId, editDate);
                            }
                            else if (type.equalsIgnoreCase("EDUCATION")) {
                                String eduLevel = data[8].trim();
                                announcement = new AnnouncementEduLevel(id, title, content, adminId, createdDate, editAdminId, editDate, eduLevel);
                            }
                            else if(type.equalsIgnoreCase("FINE")){
                                String extraData = data[8].trim();
                                announcement = new AnnouncementFines(id, title, content, adminId, createdDate, editAdminId, editDate, extraData);
                            }
                            else {
                                announcement = new Announcement(id, title, content, adminId, createdDate, editAdminId, editDate);
                            }

                            if(announcement!=null){
                                announcementTree.put(id, announcement);
                            }
                        } catch (Exception e) {
                            System.out.println("Skipping invalid row: " +  e.getMessage());
                        }
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }catch(CsvValidationException e){
            e.printStackTrace();
        }


        return announcementTree;
    }

    //Write Announcement
    public static void writeFile(String filePath, MapInterface<String, Announcement> announcementTree) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

            String[] header = { "Type", "ID", "Title", "Content", "AdminID", "CreatedDate", "EditAdminID", "EditDate", "ExtraData" };
            writer.writeNext(header, false);

            for (MapInterface.Entry<String, Announcement> entry : announcementTree) {
                Announcement a = entry.getValue();

                String typeStr;
                String extra = "N/A";

                // Identify the specific type of Announcement
                if (a instanceof AnnouncementEduLevel) {
                    typeStr = "EDUCATION";
                    extra = ((AnnouncementEduLevel) a).getEduLevel();
                } else if (a instanceof AnnouncementBroadcast) {
                    typeStr = "BROADCAST";
                } else if (a instanceof AnnouncementFines) {
                    typeStr = "FINE";
                    extra = ((AnnouncementFines) a).getTargetUserId(); // Save the Student ID here
                } else {
                    typeStr = "GENERAL";
                }

                String adminId = (a.getAdminId() == null || a.getAdminId().isEmpty()) ? "SYSTEM" : a.getAdminId();
                String createdDate = (a.getCreatedDate() == null) ? LocalDate.now().toString() : a.getCreatedDate().toString();

                String editAdminId = (a.getEditAdminId() == null || a.getEditAdminId().isEmpty()) ? "-" : a.getEditAdminId();
                String editDate = (a.getEditDate() == null) ? "-" : a.getEditDate().toString();

                String[] data = {
                        typeStr,
                        a.getId(),
                        a.getTitle(),
                        a.getContent(),
                        adminId,
                        createdDate,
                        editAdminId,
                        editDate,
                        extra
                };

                writer.writeNext(data, false);
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    public static MapInterface<String, String> getUniqueEduLevels(String filePath) {
        MapInterface<String, String> uniqueLevels = new TreeMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.readNext();
            String[] data;
            while ((data = reader.readNext()) != null) {
                String level = data[5].trim();
                uniqueLevels.put(level, level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uniqueLevels;
    }

}
