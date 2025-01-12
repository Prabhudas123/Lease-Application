package lease.Approval.auth.token;

import jakarta.persistence.*;
import lease.Approval.auth.user.User;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Private constructor for Builder
    private Token(Builder builder) {
        this.id = builder.id;
        this.token = builder.token;
        this.tokenType = builder.tokenType;
        this.revoked = builder.revoked;
        this.expired = builder.expired;
        this.user = builder.user;
    }

    // Default no-args constructor (required by JPA)
    public Token() {
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Static Builder class
    public static class Builder {
        private Integer id;
        private String token;
        private TokenType tokenType = TokenType.BEARER; // Default value
        private boolean revoked;
        private boolean expired;
        private User user;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder tokenType(TokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder revoked(boolean revoked) {
            this.revoked = revoked;
            return this;
        }

        public Builder expired(boolean expired) {
            this.expired = expired;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Token build() {
            return new Token(this);
        }
    }
}


