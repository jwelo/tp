package duke;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    public PortfolioBook load() throws AppException {
        createFileIfMissing();

        PortfolioBook portfolioBook = new PortfolioBook();
        String activePortfolioName = null;

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                String recordType = parts[0];

                switch (recordType) {
                case "ACTIVE":
                    if (parts.length >= 2 && !parts[1].isBlank()) {
                        activePortfolioName = parts[1];
                    }
                    break;
                case "PORTFOLIO":
                    if (parts.length != 2) {
                        throw new AppException("Corrupted storage file.");
                    }
                    portfolioBook.createPortfolio(parts[1]);
                    break;
                case "HOLDING":
                    if (parts.length != 7 && parts.length != 8) {
                        throw new AppException("Corrupted storage file.");
                    }
                    loadHolding(parts, portfolioBook);
                    break;
                case "PORTFOLIO_PNL":
                    if (parts.length != 4) {
                        throw new AppException("Corrupted storage file.");
                    }
                    loadPortfolioPnl(parts, portfolioBook);
                    break;
                default:
                    throw new AppException("Corrupted storage file.");
                }
            }

            if (activePortfolioName != null) {
                portfolioBook.usePortfolio(activePortfolioName);
            }

            return portfolioBook;
        } catch (IOException e) {
            throw new AppException("Unable to read storage file.");
        }
    }

    public void save(PortfolioBook portfolioBook) throws AppException {
        createFileIfMissing();

        List<String> lines = new ArrayList<>();
        lines.add("ACTIVE|" + nullToEmpty(portfolioBook.getActivePortfolioName()));

        for (Portfolio portfolio : portfolioBook.getPortfolios()) {
            lines.add("PORTFOLIO|" + portfolio.getName());
            lines.add("PORTFOLIO_PNL|" + portfolio.getName() + "|" + portfolio.getTotalRealizedPnl() + "|v1");
            for (Holding holding : portfolio.getHoldings()) {
                String priceText = holding.hasPrice() ? String.valueOf(holding.getLastPrice()) : "";
                lines.add("HOLDING|"
                        + portfolio.getName() + "|"
                        + holding.getAssetType().name() + "|"
                        + holding.getTicker() + "|"
                        + holding.getQuantity() + "|"
                        + priceText + "|"
                        + holding.getAverageBuyPrice() + "|"
                        + "v2");
            }
        }

        try {
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new AppException("Unable to save storage file.");
        }
    }

    public BulkUpdateResult loadPriceUpdates(Path csvPath, Portfolio portfolio) throws AppException {
        if (!Files.exists(csvPath)) {
            throw new AppException("File not found: " + csvPath);
        }

        int successCount = 0;
        int failedCount = 0;
        List<String> failures = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(csvPath);
            if (lines.isEmpty()) {
                throw new AppException("CSV file is empty.");
            }

            String header = lines.get(0).trim().toLowerCase();
            if (!header.equals("ticker,price")) {
                throw new AppException("CSV header must be: ticker,price");
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length != 2) {
                    failedCount++;
                    failures.add("line " + (i + 1) + " - ticker: ? reason: invalid CSV row");
                    continue;
                }

                String ticker = parts[0].trim().toUpperCase();
                String priceText = parts[1].trim();

                try {
                    double price = Double.parseDouble(priceText);
                    if (price <= 0) {
                        throw new NumberFormatException();
                    }

                    int updatedCount = portfolio.setPriceForTicker(ticker, price);
                    if (updatedCount == 0) {
                        failedCount++;
                        failures.add("line " + (i + 1) + " - ticker: " + ticker + " reason: holding not found");
                    } else {
                        successCount++;
                    }
                } catch (NumberFormatException e) {
                    failedCount++;
                    failures.add("line " + (i + 1) + " - ticker: " + ticker + " reason: price must be > 0");
                }
            }

            return new BulkUpdateResult(successCount, failedCount, failures);
        } catch (IOException e) {
            throw new AppException("Unable to read CSV file.");
        }
    }

    private void loadHolding(String[] parts, PortfolioBook portfolioBook) throws AppException {
        String portfolioName = parts[1];
        AssetType assetType = AssetType.fromString(parts[2]);
        String ticker = parts[3].toUpperCase();
        double quantity;

        try {
            quantity = Double.parseDouble(parts[4]);
        } catch (NumberFormatException e) {
            throw new AppException("Corrupted storage file.");
        }

        portfolioBook.ensurePortfolioExists(portfolioName);
        Portfolio portfolio = portfolioBook.getPortfolio(portfolioName);

        Double lastPrice = null;
        if (!parts[5].isBlank()) {
            try {
                lastPrice = Double.parseDouble(parts[5]);
            } catch (NumberFormatException e) {
                throw new AppException("Corrupted storage file.");
            }
        }

        double averageBuyPrice;
        if (parts.length == 7) {
            averageBuyPrice = lastPrice == null ? 0.0 : lastPrice;
        } else {
            try {
                averageBuyPrice = Double.parseDouble(parts[6]);
            } catch (NumberFormatException e) {
                throw new AppException("Corrupted storage file.");
            }
        }

        portfolio.restoreHolding(assetType, ticker, quantity, lastPrice, averageBuyPrice);
    }

    private void loadPortfolioPnl(String[] parts, PortfolioBook portfolioBook) throws AppException {
        String portfolioName = parts[1];
        double realizedPnl;
        try {
            realizedPnl = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new AppException("Corrupted storage file.");
        }

        portfolioBook.ensurePortfolioExists(portfolioName);
        Portfolio portfolio = portfolioBook.getPortfolio(portfolioName);
        portfolio.setTotalRealizedPnl(realizedPnl);
    }

    private void createFileIfMissing() throws AppException {
        try {
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new AppException("Unable to create storage file.");
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public record BulkUpdateResult(int successCount, int failedCount, List<String> failures) {
    }
}
