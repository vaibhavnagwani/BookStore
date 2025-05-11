package security;

import java.util.HashSet;
import java.util.Set;

public class Label {
    private Set<Principal> owners;

    public Label() {
        this.owners = new HashSet<>();
    }

    public Label(Principal p) {
        this(); // initialize empty set
        this.owners.add(p); // label starts with a single owner
    }

    public void addOwner(Principal p) {
        owners.add(p); // allow additional owners (for shared access)
    }

    public boolean canFlowTo(Label other) {
        // info can flow only if receiver's label includes all original owners
        // flow condition: L1.canFlowTo(L2) iff L1.owners ⊆ L2.owners
        return other.owners.containsAll(this.owners);
    }

    @Override
    public String toString() {
        return owners.toString(); // display label as set of owners
    }
}
