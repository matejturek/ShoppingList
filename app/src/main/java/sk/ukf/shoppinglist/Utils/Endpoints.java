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
    EDIT_LIST("list/editList.php"),
    GET_LIST_DETAILS("list/getListDetails.php"),
    GET_LISTS("list/getLists.php"),

    /* INVITE */
    CREATE_INVITATION("invite/createInvitation.php"),
    SET_INVITATION("invite/setInvitation.php"),

    /* CATEGORY */
    CREATE_CATEGORY("category/createCategory.php"),
    DELETE_CATEGORY("category/deleteCategory.php"),
    SET_CATEGORY("category/setCategory.php"),

    /* ITEM */
    CREATE_ITEM("item/createItem.php"),
    DELETE_ITEM("item/deleteItem.php"),
    EDIT_ITEM("item/editItem.php"),
    GET_ITEMS("item/getItems.php");





    private final String endpoint;

    Endpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
