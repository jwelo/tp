package duke;

import java.nio.file.Path;

public record ParsedCommand(
        CommandType type,
        String name,
        AssetType assetType,
        String ticker,
        Double quantity,
        Double price,
        //@@author zenweilow
        Double brokerageFee,
        Double fxFee,
        Double platformFee,
        //@@author RishabhShenoy03
        String listTarget,
        Path filePath
) {
    //@@author zenweilow
    public double totalFees() {
        return zeroIfNull(brokerageFee) + zeroIfNull(fxFee) + zeroIfNull(platformFee);
    }

    private static double zeroIfNull(Double value) {
        return value == null ? 0.0 : value;
    }
}
