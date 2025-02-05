package com.example.project;

import com.example.project.entities.Book;
import com.example.project.entities.Category;
import com.example.project.entities.DataResponse;
import com.example.project.entities.Receipt;
import com.example.project.entities.User;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DataManager {
    private static DataManager instance;

    public String username;
    private Book[] books;
    public Book[] booksFilter;
    public Receipt[] receipts;
    public Receipt[] ReceiptsFillter;

    public Category[] categories;


    public List<Book> booksSelect = new ArrayList<>();
    public User user;

    public List<Receipt> receiptslect = new ArrayList<>();
    private DataManager() {

    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public Book getBookByID(String id){
        for (Book book : books) {
            if(book.id.equals(id)){
                return book;
            }
        }
        return null;
    }
//    public Receipt getReceiptByID(String id) {
//        // Kiểm tra nếu danh sách sách là null hoặc rỗng, trả về null
//        if (books == null || books.length == 0) {
//            return null;
//        }
//
//        // Duyệt qua từng sách trong danh sách
//        for (Book book : books) {
//            // Tách các ID sách từ chuỗi id_books bằng dấu phẩy
//            String[] bookIDs = book.getId().split(",");
//
//            // Duyệt qua từng ID sách trong danh sách ID của sách hiện tại
//            for (String bookID : bookIDs) {
//                // Loại bỏ khoảng trắng và dấu nháy đơn từ ID sách
//                String trimmedBookID = bookID.trim().replaceAll("'", "");
//
//                // So sánh ID sách hiện tại với ID được truyền vào
//                if (trimmedBookID.equals(id)) {
//                    // Nếu tìm thấy ID sách trùng khớp, trả về đối tượng sách
//                    return receipt;
//                }
//            }
//        }
//
//        // Nếu không tìm thấy ID sách trong danh sách ID của sách nào, trả về null
//        return null;
//    }


    public void UpdateData(DataResponse dataResponse){
        this.books = dataResponse.getBooks();
        this.receipts = dataResponse.getReceipts();
    }

    public Book[] getBooks() {
        if(booksFilter != null) return booksFilter;
        return books;
    }

    public Category[] getCategories(){
        return categories;
    }
//
    public void addBookSelect(Book book){
        booksSelect.add(book);
    }

    public Receipt[] getReceipts() {
        if(ReceiptsFillter != null) return ReceiptsFillter;
        return receipts;
    }

    public List<Book> getBooksSelect() {
        return booksSelect;
    }

    public void getUser(String data) {
        // Khởi tạo một đối tượng Gson
        Gson gson = new Gson();

        // Chuyển đổi chuỗi JSON thành một đối tượng User
        this.user = gson.fromJson(data, User.class);
    }

    public User getUser() {
        return this.user;
    }
}
