package api;

public class Courier {
    // создаем ключи login, password, firstName стал полем типа String
    private String login;
    private String password;
    private String firstName;
    private String id;

    // создаем конструктор со всеми параметрами "Создание курьера"
    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    // создаем конструктор со всеми параметрами "Логин курьера в системе"
    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // создаем конструктор со всеми параметрами "Удаление курьера"
    public Courier(String id) {
        this.id = id;
    }

    // конструктор без параметров
    public Courier() {
    }

    // геттер для поля login
    public String getLogin() {
        return login;
    }

    // сеттер для поля login
    public void setLogin(String login) {
        this.login = login;
    }

    // геттер для поля password
    public String getPassword() {
        return password;
    }

    // сеттер для поля password
    public void setPassword(String password) {
        this.password = password;
    }

    // геттер для поля firstName
    public String getFirstName() {
        return firstName;
    }

    // сеттер для поля firstName
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // геттер для поля id
    public String getId() {
        return id;
    }

    // сеттер для поля login
    public void setId(String id) {
        this.id = id;
    }
}
