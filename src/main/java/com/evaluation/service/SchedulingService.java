package com.evaluation.service;

import com.evaluation.dto.DepotDto;
import com.evaluation.dto.ScheduleResponseDto;
import com.evaluation.dto.VehicleDto;
import com.evaluation.exception.ResourceNotFoundException;
import com.evaluation.model.LogLevel;
import com.evaluation.model.LogPackage;
import com.evaluation.model.LogStack;
import com.evaluation.util.KnapsackUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulingService {

    private final RestTemplate restTemplate;
    private final LoggingService loggingService;

    @Value("${external.api.base-url}")
    private String baseUrl;

    @Value("${external.api.bearer-token}")
    private String bearerToken;

    public SchedulingService(RestTemplate restTemplate, LoggingService loggingService) {
        this.restTemplate = restTemplate;
        this.loggingService = loggingService;
    }

    public ScheduleResponseDto scheduleTasksForDepot(String depotId) {
        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Scheduling tasks for depot: " + depotId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<DepotDto[]> depotsResponse = restTemplate.exchange(
                baseUrl + "/depots",
                HttpMethod.GET,
                requestEntity,
                DepotDto[].class
        );

        DepotDto[] depots = depotsResponse.getBody();
        if (depots == null) {
            loggingService.log(LogStack.backend, LogLevel.error, LogPackage.service, "Failed to fetch depots");
            throw new RuntimeException("Failed to fetch depots from external API");
        }

        DepotDto targetDepot = Arrays.stream(depots)
                .filter(d -> depotId.equals(d.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Depot with id " + depotId + " not found"));

        ResponseEntity<VehicleDto[]> vehiclesResponse = restTemplate.exchange(
                baseUrl + "/vehicles",
                HttpMethod.GET,
                requestEntity,
                VehicleDto[].class
        );

        VehicleDto[] vehicles = vehiclesResponse.getBody();
        if (vehicles == null) {
            loggingService.log(LogStack.backend, LogLevel.error, LogPackage.service, "Failed to fetch vehicles");
            throw new RuntimeException("Failed to fetch vehicles from external API");
        }

        List<VehicleDto> depotVehicles = Arrays.stream(vehicles)
                .filter(v -> depotId.equals(v.getDepotId()))
                .collect(Collectors.toList());

        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Found " + depotVehicles.size() + " vehicles for depot " + depotId);

        KnapsackUtil.KnapsackResult result = KnapsackUtil.solveKnapsack(depotVehicles, targetDepot.getMechanicHours());

        loggingService.log(LogStack.backend, LogLevel.info, LogPackage.service, "Scheduling completed for depot: " + depotId + " Max Impact: " + result.maxImpact);

        return ScheduleResponseDto.builder()
                .selectedTasks(result.selectedTasks)
                .maxImpact(result.maxImpact)
                .mechanicHoursUsed(result.mechanicHoursUsed)
                .build();
    }
}
