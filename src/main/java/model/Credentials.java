package model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {
    private String email;
    private String password;
    private String name;

    // корректные данные для авторизации
    public static Credentials fromUserData(UserData userData) {
        return new Credentials(userData.getEmail(), userData.getPassword(), userData.getName());
    }

    // передан только логин
    public static Credentials fromCourierDataLoginOnly(UserData userData) {
        return new Credentials(userData.getEmail(), null, null);
    }

    // передан только пароль
    public static Credentials fromCourierDataPasswordOnly(UserData userData) {
        return new Credentials(null, userData.getPassword(), null);
    }

    // передаем неверный логин
    public static Credentials invalidLogin(UserData userData) {
        return new Credentials("invaliddddLogin@yandex.ru", userData.getPassword(), null);
    }

    // передаем неверный пароль
    public static Credentials invalidPassword(UserData userData) {
        return new Credentials(userData.getEmail(), "invaliddddPassword", null);
    }

    public static Credentials updatePassword(UserData userData) {
        return new Credentials(userData.getEmail(), "newPassword", null);
    }

    public static Credentials updateName(UserData userData) {
        return new Credentials(userData.getEmail(), userData.getPassword(), "NewName");
    }

    public static Credentials updateEmail(UserData userData) {
        return new Credentials("updatedemailelina@yandex.ru", "newwwwPassword", userData.getName());
    }

}