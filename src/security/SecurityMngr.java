package security;

import model.User;

public class SecurityMngr {

    public static boolean checkAccess(Label dataLabel, Principal requester) {
        // checks if data labeled as {dataLabel} can be accessed by {requester}
        return new Label(requester).canFlowTo(dataLabel);
    }

    public static boolean isAuthorizedToView(User user, Label label) {
        // checks if user has sufficient privileges to view data with 'label'
        return new Label(new Principal(user.name)).canFlowTo(label);
    }
}
