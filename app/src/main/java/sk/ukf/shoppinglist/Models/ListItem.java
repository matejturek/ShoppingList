package sk.ukf.shoppinglist.Models;

public class ListItem {
    private final int id;
    private final String name;

    public ListItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}