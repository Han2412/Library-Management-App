const LOG_TYPE = Object.freeze({
    DATABASE: "~ DATABASE: ",
    NOTIFY: "~ NOTIFY: ",
    ERROR: "~ ERROR: ",
});

const EVENT = Object.freeze({
    LOGIN: "login",
    GET_DATA: "getData",
    ORDER: "order",
    ADD_USER: "addUser",
    CHECK_USERNAME: "checkUsernameExists",
    CHANGE_PASSWORD: "changePassword",
    ADD_BOOK: "addBook",
    ADD_CATEGORY: "addCategory",
    GET_CATEGORY: "getCategory",
    REMARK: "remark",
    UPDATE: "update",
    STATISTICAL: "Statistical",
    STATISTICALDETAIL: "StatisticalDetail",
})

// Export the enum for use in other scripts
module.exports = { LOG_TYPE, EVENT };