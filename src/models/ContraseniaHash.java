package models;

public class ContraseniaHash {
    private String hash;
    private String salt;

    public ContraseniaHash(){
        this.hash = "";
        this.salt = "";
    }

    public ContraseniaHash(String hash, String salt) {
        this.hash = hash;
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "ContraseniaHash{" +
                "hash='" + hash + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }
}
