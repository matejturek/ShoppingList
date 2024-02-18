package sk.ukf.shoppinglist.Models;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private final String name;
    private final List<Item> items;

    public Category(String name) {
        this.name = name;
        this.items = new ArrayList<>();
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
}