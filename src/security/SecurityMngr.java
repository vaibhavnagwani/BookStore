package security;

import model.User;

public class SecurityMngr {
    public static boolean checkAccess(Label dataLabel, Principal requester) {
        return dataLabel.canFlowTo(new Label(requester));
    }

    public static boolean isAuthorizedToView(User user, Label label) {
        return label.canFlowTo(new Label(new Principal(user.name)));
    }
}
