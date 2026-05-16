package com.evaluation.controller;

import com.evaluation.dto.ScheduleResponseDto;
import com.evaluation.model.LogLevel;
import com.evaluation.model.LogPackage;
import com.evaluation.model.LogStack;
import com.evaluation.service.LoggingService;
import com.evaluation.service.SchedulingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final SchedulingService schedulingService;
    private final LoggingService loggingService;

    public ScheduleController(SchedulingService schedulingService, LoggingService loggingService) {
        this.schedulingService = schedulingService;
        this.loggingService = loggingService;
    }

    @GetMapping("/{depotId}")
    public ResponseEntity<ScheduleResponseDto> getSchedule(@PathVariable String depotId) {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.controller, "GET /schedule/" + depotId + " called");
        ScheduleResponseDto response = schedulingService.scheduleTasksForDepot(depotId);
        return ResponseEntity.ok(response);
    }
}
