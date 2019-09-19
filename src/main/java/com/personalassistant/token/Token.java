package com.personalassistant.token;

import java.time.LocalDateTime;
import java.util.Objects;

public class Token {

    private final String token;
    private final LocalDateTime creationDate;

    public Token(String token, LocalDateTime creationDate) {
        this.token = token;
        this.creationDate = creationDate;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return token.equals(token1.token) &&
                creationDate.equals(token1.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, creationDate);
    }
}
