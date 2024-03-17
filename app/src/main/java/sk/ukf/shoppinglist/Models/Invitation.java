package sk.ukf.shoppinglist.Models;

public class Invitation {
    private int id;
    private int listId;
    private int userId;
    private String userEmail;

    public Invitation(int id, int listId, int userId, String userEmail) {
        this.id = id;
        this.listId = listId;
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter and setter for userEmail
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
