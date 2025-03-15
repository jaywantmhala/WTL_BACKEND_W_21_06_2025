package com.workshop.Config;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.workshop.Repo.OnewayTripRepo;

@Component
public class DeleteAllDataJob implements Job {

    @Autowired
    private OnewayTripRepo repository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Delete all data from the onewayTrip table
        repository.deleteAll();
        System.out.println("Deleted all data from onewayTrip table on end date.");
    }
}