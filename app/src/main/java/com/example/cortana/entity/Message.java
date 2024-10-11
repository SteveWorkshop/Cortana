package com.example.cortana.entity;

import androidx.room.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
public class Message extends BaseEntity{
    public static final int MESSAGE_RECEIVED=0;
    public static final int MESSAGE_SENT=1;
    private Integer type;
    private Integer origin;
    private String textContent;
}
