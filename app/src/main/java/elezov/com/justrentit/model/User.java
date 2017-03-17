package elezov.com.justrentit.model;

/**
 * Created by USER on 09.03.2017.
 */

public class User {

    String name="Ivan";
    String mail="example@haha.com";
    String id;

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
