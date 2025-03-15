package com.workshop.Service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.Config.DeleteAllDataJob;
import com.workshop.Config.SaveAllDataJob;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class QuartzSchedulingService {

    @Autowired
    private Scheduler scheduler;

    public void scheduleSaveJob(LocalDate startDate, String filePath) throws SchedulerException {
        JobDetail jobDetail = buildSaveJobDetail(filePath);
        Trigger trigger = buildJobTrigger(jobDetail, startDate, "saveAllTrigger", "onewayTripTriggers");
        scheduler.scheduleJob(jobDetail, trigger);
    }

public void scheduleDeleteJob(LocalDate endDate) throws SchedulerException {
    JobDetail jobDetail = buildDeleteJobDetail();
    Trigger trigger = buildJobTrigger(jobDetail, endDate, "deleteAllTrigger", "onewayTripTriggers");
    scheduler.scheduleJob(jobDetail, trigger);
}

private JobDetail buildDeleteJobDetail() {
    return JobBuilder.newJob(DeleteAllDataJob.class)
            .withIdentity("deleteAllJob", "onewayTripJobs")
            .storeDurably()
            .build();
}

    private JobDetail buildSaveJobDetail(String filePath) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("filePath", filePath); 

        return JobBuilder.newJob(SaveAllDataJob.class)
                .withIdentity("saveAllJob", "onewayTripJobs")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    // private JobDetail buildDeleteJobDetail() {
    //     return JobBuilder.newJob(DeleteAllDataJob.class)
    //             .withIdentity("deleteAllJob", "onewayTripJobs")
    //             .storeDurably()
    //             .build();
    // }

    private Trigger buildJobTrigger(JobDetail jobDetail, LocalDate date, String triggerId, String triggerGroup) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(triggerId, triggerGroup)
                .startAt(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
    }
}