package com.evaluation.dto;

import lombok.Data;

@Data
public class VehicleDto {
    private String id;
    private String depotId;
    private int impact;
    private int timeRequired; // equivalent to mechanic hours needed
}
