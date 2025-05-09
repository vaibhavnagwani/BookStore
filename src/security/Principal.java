package security;

public class Principal {
    public String name;

    public Principal(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Principal && ((Principal) obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return name;
    }
}
