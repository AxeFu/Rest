package com.example.server;

import com.example.server.builders.Hash;
import com.example.server.dto.Token;
import com.example.server.reflection.Reflection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class HashTest {

    @Test
    public void start() {
        Token token = Token.create(0L);
        System.out.println(Reflection.toJsonObject(token));

        ArrayList<String> hashs = new ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            String hash = Hash.getRandom();
            if (hashs.contains(hash)) assertEquals(0, 1);
            hashs.add(hash);
        }
        String textExample = "Hello World and Server Programming!";
        assertEquals(Hash.get(textExample), "78ba77185fc38431b8b1fabf57a895d6");

        StringBuilder stringBuilder = new StringBuilder();
        for (String hash : hashs) {
            stringBuilder.append(hash).append('\n');
        }
        System.out.println(stringBuilder);
    }

}
