package com.workshop.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.Service.ExceFileStorageService;
import com.workshop.Service.QuartzSchedulingService;

import java.time.LocalDate;

@RestController
@RequestMapping("/upload/excel")
public class UploadController {

    @Autowired
    private QuartzSchedulingService schedulingService;

    @Autowired
    private ExceFileStorageService fileStorageService;

    @PostMapping
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate
    ) throws Exception {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        String filePath = fileStorageService.saveFile(file);
        System.out.println("File saved to: " + filePath);

        schedulingService.scheduleSaveJob(start, filePath);

        schedulingService.scheduleDeleteJob(end);

        return "File uploaded and jobs scheduled successfully! File saved to: " + filePath;
    }
}