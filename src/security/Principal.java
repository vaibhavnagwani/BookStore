package security;

public class Principal {
    public String name;

    public Principal(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        // two principals are equal if they have the same name
        return obj instanceof Principal && ((Principal) obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        // required for using Principal in sets/maps
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
