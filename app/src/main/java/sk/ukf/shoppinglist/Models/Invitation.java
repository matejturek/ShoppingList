package sk.ukf.shoppinglist.Models;

public class Invitation {
    private int id;
    private int listId;
    private int userId;
    private String userEmail;
    private int status;

    public Invitation(int id, int listId, int userId, String userEmail, int status) {
        this.id = id;
        this.listId = listId;
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
