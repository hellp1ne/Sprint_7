package json;

public class LoginCourierRequest {
    private String login;
    private String password;

    public LoginCourierRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Getters and setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}