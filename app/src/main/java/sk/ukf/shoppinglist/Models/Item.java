package sk.ukf.shoppinglist.Models;

public class Item {

    private int id;
    private String name;
    private boolean status;
    private int quantity;

    public Item(int id, String name, boolean status, int quantity) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.quantity = quantity;
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
