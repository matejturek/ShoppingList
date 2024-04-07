package sk.ukf.shoppinglist.Models;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private final int id;
    private final String name;
    private List<Item> items;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
        this.items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}