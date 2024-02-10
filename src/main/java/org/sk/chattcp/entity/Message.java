package org.sk.chattcp.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Message {
    private long id;
    private User sender;
    private String content;
    private LocalDateTime date;
}
