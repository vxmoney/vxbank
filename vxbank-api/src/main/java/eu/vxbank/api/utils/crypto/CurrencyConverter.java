package eu.vxbank.api.utils.crypto;

public class CurrencyConverter {
    // Public fields for fiat currency, crypto currency, conversion rate, and decimal places
    public String fiatCurrency;
    public String cryptoCurrency;
    public String cryptoCurrencyTicker;
    public long conversionRate; // Conversion rate as long
    public long fiatDecimals;
    public long cryptoDecimals;
    public long conversionRateDecimals; // Conversion rate decimals as long

    // Base value for scaling calculations
    private static final long BASE = 10;

    // Constructor to initialize the model
    public CurrencyConverter(String fiatCurrency,
                             String cryptoCurrency,
                             String cryptoCurrencyTicker,
                             long conversionRate,
                             long fiatDecimals,
                             long cryptoDecimals,
                             long conversionRateDecimals) {
        this.fiatCurrency = fiatCurrency;
        this.cryptoCurrency = cryptoCurrency;
        this.cryptoCurrencyTicker = cryptoCurrencyTicker;
        this.conversionRate = conversionRate;
        this.fiatDecimals = fiatDecimals;
        this.cryptoDecimals = cryptoDecimals;
        this.conversionRateDecimals = conversionRateDecimals;
    }

    // Method to calculate fiat value based on crypto value
    public long calculateFiatValue(long cryptoValue) {
        // Scale up the crypto value by BASE^cryptoDecimals to handle the decimals
        double cryptoValueScaled = cryptoValue / Math.pow(BASE, cryptoDecimals);
        // Calculate the fiat value as a double
        double fiatValueDouble = cryptoValueScaled * (conversionRate / Math.pow(BASE, conversionRateDecimals));
        // Convert the fiat value to long, scaling by BASE^fiatDecimals to include decimals
        return Math.round(fiatValueDouble * Math.pow(BASE, fiatDecimals));
    }

    // Method to calculate crypto value based on fiat value
    public long calculateCryptoValue(long fiatValue) {
        // Scale down the fiat value by BASE^fiatDecimals to handle the decimals
        double fiatValueScaled = fiatValue / Math.pow(BASE, fiatDecimals);
        // Calculate the crypto value as a double
        double cryptoValueDouble = fiatValueScaled / (conversionRate / Math.pow(BASE, conversionRateDecimals));
        // Convert the crypto value to long, scaling by BASE^cryptoDecimals to include decimals
        return Math.round(cryptoValueDouble * Math.pow(BASE, cryptoDecimals));
    }

    // Method to get display value for crypto
    public String getCryptoDisplayValue(long cryptoValue) {
        return String.format("%d.%0" + cryptoDecimals + "d",
                cryptoValue / (long) Math.pow(BASE, cryptoDecimals),
                cryptoValue % (long) Math.pow(BASE, cryptoDecimals));
    }

    // Method to get display value for fiat
    public String getFiatDisplayValue(long fiatValue) {
        return String.format("%d.%0" + fiatDecimals + "d",
                fiatValue / (long) Math.pow(BASE, fiatDecimals),
                fiatValue % (long) Math.pow(BASE, fiatDecimals));
    }


}
