package org.example.domain;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class AccessRecord {
    private Date accessDate;
    private String ip;
    private String request;
    private int status;
    private String userAgent;
}
