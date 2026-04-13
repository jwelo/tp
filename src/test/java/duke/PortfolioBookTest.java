package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PortfolioBookTest {
    @Test
    void createPortfolio_blankName_throws() {
        PortfolioBook book = new PortfolioBook();

        AppException ex = assertThrows(AppException.class, () -> book.createPortfolio("   "));

        assertEquals("Portfolio name cannot be blank", ex.getMessage());
    }

    @Test
    void createPortfolio_normalizesNameToLowercase() throws AppException {
        PortfolioBook book = new PortfolioBook();

        book.createPortfolio("Growth");

        assertEquals("growth", book.getActivePortfolioName());
        Portfolio created = book.getPortfolio("GROWTH");
        assertNotNull(created);
        assertEquals("growth", created.getName());
    }

    @Test
    void createPortfolio_duplicateIgnoringCase_throws() throws AppException {
        PortfolioBook book = new PortfolioBook();
        book.createPortfolio("Growth");

        AppException ex = assertThrows(AppException.class, () -> book.createPortfolio("growth"));

        assertEquals("Portfolio already exists: growth", ex.getMessage());
    }

    @Test
    void usePortfolio_acceptsMixedCaseName() throws AppException {
        PortfolioBook book = new PortfolioBook();
        book.createPortfolio("first");
        book.createPortfolio("second");

        book.usePortfolio("SeCoNd");

        assertEquals("second", book.getActivePortfolioName());
    }
}
