package com.ef.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class UserLog {

    @Id
    private long id;
    private String date;
    private String ip;
    private String request;
    private String status;
    private String userAgent;

}
