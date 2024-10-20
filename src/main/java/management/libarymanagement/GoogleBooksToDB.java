//package management.libarymanagement;
//
//import API.GoogleBooksAPI;
//import DataAccessObject.BookDAO;
//import Entity.Book;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import management.libarymanagement.DataBase;
//
//
//
//public class GoogleBooksToDB {
//    public static void main(String[] args) {
//        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI();
//        BookDAO dbManager = new BookDAO();
//
//        try {
//            // Lấy dữ liệu từ Google Books API
//            String jsonData = googleBooksAPI.fetchBooksData("Swing");
//
//            // Phân tích JSON và lưu dữ liệu vào database
//            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
//            JsonArray items = jsonObject.getAsJsonArray("items");
//
//            for (int i = 0; i < items.size(); i++) {
//                JsonObject volumeInfo = items.get(i).getAsJsonObject().getAsJsonObject("volumeInfo");
//                String title = volumeInfo.has("title") ? volumeInfo.get("title").getAsString() : "Unknown";
//                String authors = volumeInfo.has("authors") ? volumeInfo.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
//                String publisher = volumeInfo.has("publisher") ? volumeInfo.get("publisher").getAsString() : "Unknown";
//                String publishedDate = volumeInfo.has("publishedDate") ? volumeInfo.get("publishedDate").getAsString() : "Unknown";
//
//                // Tạo đối tượng Book
//                Book book = new Book(title, authors, publisher, publishedDate, null);
//
//                // Lưu vào database
//                dbManager.insertBook(book);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
