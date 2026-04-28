package com.resilientcampus.disaster.disaster;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

@Service
public class CSVStorageService {

    // This is where the file will be saved on your computer
    private final String FILE_PATH = "sos_alerts.csv";

    public void saveOne(SOSRequest r) {
        try {
            File file = new File(FILE_PATH);

            // Check if file exists to decide if we need a header
            boolean fileExists = file.exists();

            // 'true' means it will add new lines to the end (append) instead of deleting old ones
            PrintWriter writer = new PrintWriter(new FileWriter(file, true));

            // Write header only if it's a brand new file
            if (!fileExists || file.length() == 0) {
                writer.println("studentId,latitude,longitude,time");
            }

            // Write the actual SOS data
            writer.println(
                    r.getStudentId() + "," +
                            r.getLatitude() + "," +
                            r.getLongitude() + "," +
                            r.getTime()
            );

            writer.close();
            System.out.println("✅ SOS data saved to CSV file!");

        } catch (Exception e) {
            System.out.println("❌ Error saving to CSV:");
            e.printStackTrace();
        }
    }
}