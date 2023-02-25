package tradingdatadb;

public record OrderModel(String bookID, int userID, int orderID, double price, String side, int volume, int filledVolume, int timestamp, String status) { }
