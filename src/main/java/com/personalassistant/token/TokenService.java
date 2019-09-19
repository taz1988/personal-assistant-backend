package com.personalassistant.token;

import com.amazonaws.services.s3.AmazonS3;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class TokenService {

    private AmazonS3 amazonS3;

    public TokenService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public boolean validateToken(String token) {
        List<Token> tokens = loadTokens();
        writeToken(tokens);
        return tokens.stream().map(Token::getToken).anyMatch(storedToken -> storedToken.equals(token));
    }

    public String createToken() {
        List<Token> tokens = loadTokens();
        Token token = new Token(UUID.randomUUID().toString(), LocalDateTime.now());
        tokens.add(token);
        writeToken(tokens);
        return token.getToken();
    }

    private List<Token> loadTokens() {
        return Arrays
                .stream(amazonS3.getObjectAsString(System.getenv("bucketName"), System.getenv("tokens")).split("\\|"))
                .filter(token -> token != null && token.length() > 0)
                .map(this::mapToToken)
                .filter(this::isValid)
                .collect(Collectors.toList());
    }

    private Token mapToToken(String tokenAsString) {
        String[] parts = tokenAsString.split(",");
        LocalDateTime creationTime = LocalDateTime.parse(parts[1], ISO_DATE_TIME);
        return new Token(parts[0], creationTime);
    }

    private boolean isValid(Token token) {
        return token.getCreationDate().plusHours(4L).isBefore(LocalDateTime.now());
    }

    private void writeToken(List<Token> tokens) {
        String tokensAsString = tokens
                .stream()
                .map(this::mapTokenToString)
                .collect(Collectors.joining("|"));

        amazonS3.putObject(System.getenv("bucketName"), System.getenv("tokens"), tokensAsString);
    }

    private String mapTokenToString(Token token) {
        return token.getToken() + "," + token.getCreationDate().format(ISO_DATE_TIME);
    }
}
