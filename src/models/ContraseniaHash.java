package models;

import models.exceptions.InvalidOrMissingHashPasswordException;

import java.util.Objects;

public class ContraseniaHash {
    private final String hash;
    private final String salt;

    public ContraseniaHash(){
        this.hash = "";
        this.salt = "";
    }

    public ContraseniaHash(String hash, String salt) throws InvalidOrMissingHashPasswordException {
        if (hash == null || hash.isEmpty() || salt == null || salt.isEmpty()) {
            throw new InvalidOrMissingHashPasswordException("Ni Hash ni Salt pueden ser nulos o vacíos.");
        }

        this.hash = hash;
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ContraseniaHash that)) return false;
        return Objects.equals(hash, that.hash) && Objects.equals(salt, that.salt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, salt);
    }

    @Override
    public String toString() {
        return "ContraseniaHash{" +
                "hash='" + hash + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
