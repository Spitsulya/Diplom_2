package model;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.github.javafaker.Faker;

@Data
@AllArgsConstructor
public class Credentials {
    private String email;
    private String password;
    private String name;

    private static final Faker faker = new Faker();

    // корректные данные для авторизации
    public static Credentials fromUserData(UserData userData) {
        return new Credentials(userData.getEmail(), userData.getPassword(), userData.getName());
    }

    // передаем неверный email
    public static Credentials invalidLogin(UserData userData) {
        String randomEmail = faker.internet().password(6, 10);
        return new Credentials(randomEmail, userData.getPassword(), null);
    }

    // передаем неверный пароль
    public static Credentials invalidPassword(UserData userData) {
        String randomPassword = faker.internet().password(6, 10);
        return new Credentials(userData.getEmail(), randomPassword, null);
    }

    public static Credentials updatePassword(UserData userData) {
        String randomPassword = faker.internet().password(6, 10);
        return new Credentials(userData.getEmail(), randomPassword, userData.getName());
    }

    public static Credentials updateName(UserData userData) {
        String randomName = faker.name().firstName();
        return new Credentials(userData.getEmail(), userData.getPassword(), randomName);
    }

    public static Credentials updateEmail(UserData userData) {
        String randomEmail = faker.internet().emailAddress();
        return new Credentials(randomEmail, userData.getPassword(), userData.getName());
    }
}