package org.example;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Ability {

    private final String name;

    private final Integer requiredLevel;
}
