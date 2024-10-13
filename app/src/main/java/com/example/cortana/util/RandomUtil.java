package com.example.cortana.util;

import java.util.UUID;

public class RandomUtil {
    public static String getRandomName()
    {
        return UUID.randomUUID().toString();
    }
}
