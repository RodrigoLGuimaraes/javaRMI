package LibrarySystem;

import java.io.Serializable;

/**
 * Created by hudo on 10/2/16.
 */
public class Book implements Serializable {
    private String name;
    private String owner;

    public Book(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

