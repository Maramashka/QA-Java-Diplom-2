package stellarburgers.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserCreds {

    private String email;
    private String password;
    private String name;

    public static UserCreds credsFromUser(User user) {
        return new UserCreds(user.getEmail(), user.getPassword(), user.getName());
    }
}
