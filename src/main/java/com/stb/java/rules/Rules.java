package com.stb.java.rules;

import com.stb.java.functions.TriFunction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Rules {

    public static Supplier<String> $s$dateNow = () -> LocalDateTime.now().toString();
    public static Function<String, Object> $f$date = (input) -> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(input);
        return LocalDate.now().format(formatter);
    };

    public static BiFunction<String,String,String> $bf$twoParams = (param1, param2) -> param1+"_"+param2;

    public static TriFunction<String,String,String,String> $tf$threeParams =
            (param1,param2,param3) -> param1+"+"+param2+"+"+param3;
}
