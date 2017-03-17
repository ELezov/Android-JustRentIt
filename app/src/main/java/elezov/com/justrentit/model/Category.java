package elezov.com.justrentit.model;

/**
 * Created by USER on 14.03.2017.
 */

public class Category {
    String id="";
    String name="";
    String stringPhoto="";

    public String getStringPhoto() {
        return stringPhoto;
    }

    public void setStringPhoto(String stringPhoto) {
        this.stringPhoto = stringPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
