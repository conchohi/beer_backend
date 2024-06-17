package com.zipbeer.beerbackend.dto.game;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BalanceTopic {
    private String choice0;
    private String choice1;

    @Override
    public String toString() {
        return this.choice0 + " " + this.choice1;
    }
}
