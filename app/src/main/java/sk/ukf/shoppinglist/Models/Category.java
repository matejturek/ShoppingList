package sk.ukf.shoppinglist.Models;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private final int id;
    private final int parentId;
    private final String name;
    private List<Item> items;
    private List<Category> subcategories;

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

    public void setItems(List<Item> items) {
        this.items = items;
    }
    public void setSubcategories(List<Category> subcategories) {
        this.subcategories = subcategories;
    }

    public List<Category> getSubcategories() {
        return subcategories;
    }

    public List<Category> getSubcategories(List<Category> allCategories) {
        if (subcategories == null) {
            subcategories = new ArrayList<>();
            for (Category category : allCategories) {
                if (category.getParentId() == id) {
                    subcategories.add(category);
                }
            }
        }
        return subcategories;
    }
}