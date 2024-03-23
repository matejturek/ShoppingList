package sk.ukf.shoppinglist.Models;

public class Invitation {
    private int id;
    private int listId;
    private String listName;
    private int userId;
    private String userEmail;
    private int status;

    public Invitation(int id, int listId, String listName, int userId, String userEmail, int status) {
        this.id = id;
        this.listId = listId;
        this.listName = listName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
