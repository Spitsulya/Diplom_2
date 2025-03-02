package model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {
    private String email;
    private String password;

    // корректные данные для авторизации
    public static Credentials fromUserData(UserData userData) {
        return new Credentials(userData.getEmail(), userData.getPassword());
    }

    // передан только логин
    public static Credentials fromCourierDataLoginOnly(UserData userData) {
        return new Credentials(userData.getEmail(), null);
    }

    // передан только пароль
    public static Credentials fromCourierDataPasswordOnly(UserData userData) {
        return new Credentials(null, userData.getPassword());
    }

    // передаем неверный логин
    public static Credentials invalidLogin(UserData userData) {
        return new Credentials("invalidLogin", userData.getPassword());
    }

    // передаем неверный пароль
    public static Credentials invalidPassword(UserData userData) {
        return new Credentials(userData.getEmail(), "invalidPassword");
    }
}