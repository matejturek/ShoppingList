package sk.ukf.shoppinglist.Utils;

public enum Endpoints {

    /*ACCOUNT*/
    EDIT_ACCOUNT("account/editAccount.php"),
    GET_ACCOUNT_DETAILS("account/getAccountDetails.php"),
    LOGIN("account/login.php"),
    REGISTER("account/register.php"),


    /*LIST*/
    CREATE_LIST("list/createList.php"),
    DELETE_LIST("list/deleteList.php"),
    EDIT_LIST("list/editList.php"),
    GET_LIST_DETAILS("list/getListDetails.php"),
    GET_LISTS("list/getLists.php");



    private final String endpoint;

    Endpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
