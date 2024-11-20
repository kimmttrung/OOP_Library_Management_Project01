package Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateStringFormatter {

    private DateTimeFormatter formatter;

    /**
     * Default constructor with the date format "dd/MM/yyyy".
     */
    public DateStringFormatter() {
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    /**
     * Constructor with a custom date format.
     *
     * @param pattern The date format (e.g., "yyyy-MM-dd", "MM/dd/yyyy").
     */
    public DateStringFormatter(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * Converts a LocalDate to a formatted string according to the specified date format.
     *
     * @param date The LocalDate object to be formatted.
     * @return The formatted date string.
     * @throws IllegalArgumentException if the date is null.
     */
    public String formatDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return date.format(formatter);
    }

    /**
     * Parses a date string into a LocalDate object according to the specified date format.
     *
     * @param dateString The date string to be parsed.
     * @return The parsed LocalDate object.
     * @throws DateTimeParseException if the date string does not match the expected format.
     * @throws IllegalArgumentException if the date string is null or empty.
     */
    public LocalDate parseDate(String dateString) throws DateTimeParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        return LocalDate.parse(dateString, formatter);
    }

    /**
     * Checks if a date string is valid according to the specified date format.
     *
     * @param dateString The date string to be checked.
     * @return true if the date string is valid, false otherwise.
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
     * Changes the date format pattern of this DateStringFormatter instance.
     *
     * @param pattern The new date format pattern.
     */
    public void setPattern(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }
}
