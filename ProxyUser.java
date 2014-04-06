package eProctor;

public class ProxyUser {
    private User user = null;
    private int userID;
    private String password;
    private String email;
    private String name;

    public ProxyUser(final int u, final String p, final String e, final String n) {
        userID = u;
        password = p;
        email = e;
        name = n;
    }

    public User authenticate(String userName, String pass) {
        if (user != null) {
            System.out.println("Account logined already.");
        } else {
            user = new User(userID, password, email, name);
        }
        return user;
    }
}
