package com.evaluation.dto;

import com.evaluation.model.LogLevel;
import com.evaluation.model.LogPackage;
import com.evaluation.model.LogStack;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogRequestDto {
    private LogStack stack;
    private LogLevel level;
    private LogPackage packageName;
    private String message;
}
