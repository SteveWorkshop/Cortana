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

    //todo：自定义降级返回
    public static final String DEFAULT_FALLBACK_ANSWER="对不起主人，猫猫遇到了一点问题，要不再挼我一下吧";

    private Integer type;
    private Integer origin;
    private String textContent;
}
