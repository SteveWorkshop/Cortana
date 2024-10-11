package com.example.cortana.entity;

import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BaseEntity implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Boolean isDeleted;
    private Long createTime=System.currentTimeMillis();
    private Long updateTime=System.currentTimeMillis();
}
