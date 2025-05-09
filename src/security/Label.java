package security;

import java.util.HashSet;
import java.util.Set;

public class Label {
    private Set<Principal> owners;

    public Label() {
        this.owners = new HashSet<>();
    }

    public Label(Principal p) {
        this();
        this.owners.add(p);
    }

    public void addOwner(Principal p) {
        owners.add(p);
    }

    public boolean canFlowTo(Label other) {
        return other.owners.containsAll(this.owners);
    }

    @Override
    public String toString() {
        return owners.toString();
    }
}
