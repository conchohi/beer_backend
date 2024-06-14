package com.zipbeer.beerbackend.dto.game;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LiarTopic {
    private String subject;
    private String topic;

    @Override
    public String toString() {
        return this.subject + " " + this.topic;
    }
}
