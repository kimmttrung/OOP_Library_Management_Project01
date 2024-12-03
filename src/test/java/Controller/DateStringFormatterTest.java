package Controller;

import Tools.DateStringFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class DateStringFormatterTest {

    private DateStringFormatter defaultFormatter;
    private DateStringFormatter customFormatter;

    @BeforeEach
    void setUp() {
        defaultFormatter = new DateStringFormatter(); // Default pattern "dd/MM/yyyy"
        customFormatter = new DateStringFormatter("yyyy-MM-dd");
    }

    @Test
    void testFormatDateWithDefaultPattern() {
        LocalDate date = LocalDate.of(2024, 11, 27);
        String formattedDate = defaultFormatter.formatDate(date);
        assertEquals("27/11/2024", formattedDate);
    }

    @Test
    void testFormatDateWithCustomPattern() {
        LocalDate date = LocalDate.of(2024, 11, 27);
        String formattedDate = customFormatter.formatDate(date);
        assertEquals("2024-11-27", formattedDate);
    }

    @Test
    void testFormatDateWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> defaultFormatter.formatDate(null));
    }

    @Test
    void testParseDateWithDefaultPattern() {
        String dateString = "27/11/2024";
        LocalDate date = defaultFormatter.parseDate(dateString);
        assertEquals(LocalDate.of(2024, 11, 27), date);
    }

    @Test
    void testParseDateWithCustomPattern() {
        String dateString = "2024-11-27";
        LocalDate date = customFormatter.parseDate(dateString);
        assertEquals(LocalDate.of(2024, 11, 27), date);
    }

    @Test
    void testParseDateWithInvalidFormatThrowsException() {
        String invalidDateString = "27-11-2024";
        assertThrows(DateTimeParseException.class, () -> defaultFormatter.parseDate(invalidDateString));
    }

    @Test
    void testParseDateWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> defaultFormatter.parseDate(null));
    }

    @Test
    void testParseDateWithEmptyStringThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> defaultFormatter.parseDate(""));
    }

    @Test
    void testIsValidDateWithCorrectFormat() {
        assertTrue(customFormatter.isValidDate("2024-11-27"));
    }

    @Test
    void testIsValidDateWithIncorrectFormat() {
        assertFalse(customFormatter.isValidDate("27/11/2024"));
    }

    @Test
    void testIsValidDateWithInvalidDate() {
        assertFalse(customFormatter.isValidDate("2024-13-01")); // Invalid month
    }

    @Test
    void testSetPattern() {
        defaultFormatter.setPattern("yyyy-MM-dd");
        String formattedDate = defaultFormatter.formatDate(LocalDate.of(2024, 11, 27));
        assertEquals("2024-11-27", formattedDate);
    }
}
