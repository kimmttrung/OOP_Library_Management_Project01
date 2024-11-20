package Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateStringFormatter {

    private DateTimeFormatter formatter;

    /**
     * Constructor với định dạng mặc định "dd/MM/yyyy".
     */
    public DateStringFormatter() {
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    /**
     * Constructor với định dạng tùy chỉnh.
     *
     * @param pattern Định dạng ngày (ví dụ: "yyyy-MM-dd", "MM/dd/yyyy").
     */
    public DateStringFormatter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * Chuyển đổi LocalDate thành chuỗi ngày tháng theo định dạng đã thiết lập.
     *
     * @param date LocalDate cần định dạng.
     * @return Chuỗi ngày đã định dạng.
     */
    public String formatDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.format(formatter);
    }

    /**
     * Phân tích chuỗi ngày tháng thành LocalDate.
     *
     * @param dateString Chuỗi ngày cần phân tích.
     * @return LocalDate đã phân tích.
     * @throws DateTimeParseException Nếu chuỗi không đúng định dạng.
     */
    public LocalDate parseDate(String dateString) throws DateTimeParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Kiểm tra xem chuỗi ngày có hợp lệ với định dạng hay không.
     *
     * @param dateString Chuỗi ngày cần kiểm tra.
     * @return true nếu hợp lệ, false nếu không.
     */
    public boolean isValidDate(String dateString) {
        try {
            parseDate(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Thay đổi định dạng ngày tháng của đối tượng này.
     *
     * @param pattern Định dạng mới.
     */
    public void setPattern(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }
}