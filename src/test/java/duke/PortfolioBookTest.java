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
    void createPortfolio_preservesOriginalNameCasing() throws AppException {
        PortfolioBook book = new PortfolioBook();

        book.createPortfolio("Growth");

        assertEquals("Growth", book.getActivePortfolioName());
        Portfolio created = book.getPortfolio("GROWTH");
        assertNotNull(created);
        assertEquals("Growth", created.getName());
    }

    @Test
    void createPortfolio_duplicateIgnoringCase_throws() throws AppException {
        PortfolioBook book = new PortfolioBook();
        book.createPortfolio("Growth");

        AppException ex = assertThrows(AppException.class, () -> book.createPortfolio("growth"));

        assertEquals("Portfolio already exists: Growth", ex.getMessage());
    }

    @Test
    void usePortfolio_acceptsMixedCaseName() throws AppException {
        PortfolioBook book = new PortfolioBook();
        book.createPortfolio("first");
        book.createPortfolio("SecondPortfolio");

        book.usePortfolio("sEcOnDpOrTfOlIo");

        assertEquals("SecondPortfolio", book.getActivePortfolioName());
    }
}
