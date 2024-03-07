package sk.ukf.shoppinglist.Models;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private final int id;
    private final int parentId;
    private final String name;
    private final List<Item> items;

    public Category(int id, String name, int parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public int getParentId() {
        return parentId;
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