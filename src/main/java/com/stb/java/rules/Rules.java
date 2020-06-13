package com.stb.java.rules;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class Rules {

    public static Supplier<String> $s$dateNow = () -> LocalDateTime.now().toString();

}
