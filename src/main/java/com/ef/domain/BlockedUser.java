package com.ef.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class BlockedUser {

    @Id
    private long id;
    private int requests;
    private String ip;
    private String comment;
}
