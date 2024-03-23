package sk.ukf.shoppinglist.Utils;

public enum Endpoints {

    /* ACCOUNT */
    EDIT_ACCOUNT("account/editAccount.php"),
    GET_ACCOUNT_DETAILS("account/getAccountDetails.php"),
    LOGIN("account/login.php"),
    REGISTER("account/register.php"),


    /* LIST */
    CREATE_LIST("list/createList.php"),
    DELETE_LIST("list/deleteList.php"),
    SET_LIST("list/setList.php"),
    GET_LIST_DETAILS("list/getListDetails.php"),
    GET_LISTS("list/getLists.php"),

    /* INVITE */
    CREATE_INVITATION("invitation/createInvitation.php"),
    GET_INVITATIONS("invitation/getInvitations.php"),
    GET_MY_INVITATIONS("invitation/getMyInvitations.php"),
    GET_PENDING_INVITATIONS("invitation/getPendingInvitations.php"),
    ACCEPT_INVITATION("invitation/acceptInvitation.php"),
    DELETE_INVITATION("invitation/deleteInvitation.php"),

    /* CATEGORY */
    CREATE_CATEGORY("category/createCategory.php"),
    DELETE_CATEGORY("category/deleteCategory.php"),
    SET_CATEGORY("category/setCategory.php"),
    GET_CATEGORIES("category/getCategories.php"),
    GET_CATEGORY("category/getCategory.php"),

    /* ITEM */
    CREATE_ITEM("item/createItem.php"),
    DELETE_ITEM("item/deleteItem.php"),
    SET_ITEM("item/setItem.php"),
    SET_ITEM_STATUS("item/setItemStatus.php"),
    GET_ITEMS("item/getItems.php");





    private final String endpoint;

    Endpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
