package sk.ukf.shoppinglist.Models;

public class Item {

    private int id;
    private int categoryId;
    private String name;
    private int quantity;
    private boolean status;
    private String link;
    private String shelf;

    public Item(int id, int categoryId, String name, int quantity, boolean status, String link, String shelf ) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.status = status;
        this.quantity = quantity;
        this.link = link;
        this.shelf = shelf;
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLink() {
        return link;
    }

    public String getShelf() {
        return shelf;
    }

    public String getName() {
        return name;
    }

    public Boolean getStatus() {
        return status;
    }
}
