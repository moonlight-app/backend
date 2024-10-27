package ru.moonlightapp.backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@UtilityClass
public final class CharSequenceGenerator {

    private static final char[] ALPHANUMERIC_DICTIONARY = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] ALPHABETIC_DICTIONARY = Arrays.copyOfRange(ALPHANUMERIC_DICTIONARY, 10, ALPHANUMERIC_DICTIONARY.length);
    private static final char[] DIGITS_DICTIONARY = Arrays.copyOfRange(ALPHANUMERIC_DICTIONARY, 0, 10);

    public String generateRandomAlphanumericString(int length, boolean randomCase) {
        return generateRandomString(ALPHANUMERIC_DICTIONARY, length, randomCase);
    }

    public String generateRandomAlphabeticCode(int length, boolean randomCase) {
        return generateRandomString(ALPHABETIC_DICTIONARY, length, randomCase);
    }

    public String generateRandomDigitsCode(int length) {
        return generateRandomString(DIGITS_DICTIONARY, length, false);
    }

    public String generateRandomString(char[] dict, int length, boolean randomCase) {
        Random random = new Random();

        Stream<Character> charStream = IntStream.generate(() -> random.nextInt(dict.length))
                .limit(length)
                .mapToObj(i -> dict[i]);

        if (randomCase)
            charStream = charStream.map(ch -> random.nextBoolean() ? Character.toUpperCase(ch) : ch);

        return charStream
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public String generateProofKey() {
        UUID randomUuid = UUID.randomUUID();
        byte[] asBytes = randomUuid.toString().getBytes(StandardCharsets.UTF_8);
        return DigestUtils.md5DigestAsHex(asBytes);
    }

}