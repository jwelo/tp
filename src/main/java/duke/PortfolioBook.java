package duke;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PortfolioBook {
    private final Map<String, Portfolio> portfolios;
    private String activePortfolioName;

    public PortfolioBook() {
        this.portfolios = new LinkedHashMap<>();
        this.activePortfolioName = null;
    }

    public void createPortfolio(String name) throws AppException {
        String normalisedName = requireNormalizedPortfolioName(name);
        if (portfolios.containsKey(normalisedName)) {
            throw new AppException("Portfolio already exists: " + normalisedName);
        }

        portfolios.put(normalisedName, new Portfolio(normalisedName));

        if (activePortfolioName == null) {
            activePortfolioName = normalisedName;
        }
    }

    public void ensurePortfolioExists(String name) throws AppException {
        String normalisedName = requireNormalizedPortfolioName(name);
        if (!portfolios.containsKey(normalisedName)) {
            createPortfolio(name);
        }
    }

    public void usePortfolio(String name) throws AppException {
        String normalisedName = requireNormalizedPortfolioName(name);
        if (!portfolios.containsKey(normalisedName)) {
            throw new AppException("Portfolio not found: " + normalisedName);
        }
        activePortfolioName = normalisedName;
    }

    public boolean hasActivePortfolio() {
        return activePortfolioName != null;
    }

    public Portfolio getActivePortfolio() throws AppException {
        if (activePortfolioName == null) {
            throw new AppException("No active portfolio selected. Use: /use NAME");
        }
        return portfolios.get(activePortfolioName);
    }

    public String getActivePortfolioName() {
        return activePortfolioName;
    }

    public List<Portfolio> getPortfolios() {
        return new ArrayList<>(portfolios.values());
    }

    public Portfolio getPortfolio(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return portfolios.get(name.trim().toLowerCase(Locale.ROOT));
    }

    private String requireNormalizedPortfolioName(String name) throws AppException {
        if (name == null || name.isBlank()) {
            throw new AppException("Portfolio name cannot be blank");
        }
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
