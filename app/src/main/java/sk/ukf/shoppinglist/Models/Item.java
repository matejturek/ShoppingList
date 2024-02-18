package sk.ukf.shoppinglist.Models;

public class Item {

    private final int id;
    private final String name;
    private final boolean status;

    public Item(int id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Boolean getStatus() {
        return status;
    }
}
