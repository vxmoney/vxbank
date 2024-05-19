# working on first test
```javascript
{
      "step": "checkState",
      "accounts": {
        "address:owner": {
          "nonce": "*",
          "balance": "0",
          "storage": {},
          "code": ""
        },
        "sc:vxevents": {
          "nonce": "*",
          "balance": "1000000",
          "storage": {
            "str:tokenPercentage":"u64:10000"
          }
        }
      }
    }
```

# conversion idea
```java
public class CryptoToFiatConverter {
    // Attributes for fiat currency, crypto currency and conversion rate
    private String fiatCurrency;
    private String cryptoCurrency;
    private double conversionRate; // Conversion rate as a double

    // Constructor to initialize the model
    public CryptoToFiatConverter(String fiatCurrency, String cryptoCurrency, double conversionRate) {
        this.fiatCurrency = fiatCurrency;
        this.cryptoCurrency = cryptoCurrency;
        this.conversionRate = conversionRate;
    }

    // Method to calculate fiat value based on crypto value
    public long calculateFiatValue(double cryptoValue) {
        // Calculate the fiat value as a double
        double fiatValueDouble = cryptoValue * conversionRate;
        // Convert the fiat value to long, scaling by 100 to include cents
        return Math.round(fiatValueDouble * 100);
    }

    // Getters for the attributes (if needed)
    public String getFiatCurrency() {
        return fiatCurrency;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public static void main(String[] args) {
        // Example usage:
        // Define the conversion rate from USDT to EUR (example rate: 1 USDT = 0.85 EUR)
        double conversionRate = 0.85;

        // Initialize the converter with EUR as fiat currency and USDT as crypto currency
        CryptoToFiatConverter converter = new CryptoToFiatConverter("EUR", "USDT", conversionRate);

        // Define a specific amount of crypto currency (e.g., 1234.56 USDT)
        double cryptoValue = 1234.56;

        // Calculate the corresponding fiat value
        long fiatValue = converter.calculateFiatValue(cryptoValue);

        // Print the result
        System.out.println("Crypto Value: " + cryptoValue + " " + converter.getCryptoCurrency());
        System.out.println("Fiat Value: " + fiatValue + " " + converter.getFiatCurrency());
        System.out.println("Fiat Value (formatted): " + (fiatValue / 100) + "." + (fiatValue % 100) + " " + converter.getFiatCurrency());
    }
}


```

# example with decimals
```java
public class CurrencyConverter {
    // Public fields for fiat currency, crypto currency, conversion rate, and decimal places
    public String fiatCurrency;
    public String cryptoCurrency;
    public long conversionRate; // Conversion rate as long
    public long fiatDecimals;
    public long cryptoDecimals;
    public long conversionRateDecimals; // Conversion rate decimals as long

    // Base value for scaling calculations
    private static final long BASE = 10;

    // Constructor to initialize the model
    public CurrencyConverter(String fiatCurrency, String cryptoCurrency, long conversionRate, long fiatDecimals, long cryptoDecimals, long conversionRateDecimals) {
        this.fiatCurrency = fiatCurrency;
        this.cryptoCurrency = cryptoCurrency;
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

    public static void main(String[] args) {
        // Example usage:
        // Define the conversion rate from USDT to EUR (1 USDT = 0.9192 EUR, represented as 9192 with 4 decimals)
        long conversionRate = 9192;
        long conversionRateDecimals = 4;

        // Initialize the converter with EUR as fiat currency (2 decimals) and USDT as crypto currency (6 decimals)
        CurrencyConverter converter = new CurrencyConverter("EUR", "USDT", conversionRate, 2, 6, conversionRateDecimals);

        // Example 1: Calculate fiat value from crypto value
        long cryptoValue = 1234567890; // Example crypto value in USDT (1.234567890 USDT)
        long fiatValue = converter.calculateFiatValue(cryptoValue);
        System.out.println("Crypto Value: " + cryptoValue + " (" + (cryptoValue / 1_000_000) + "." + String.format("%06d", (cryptoValue % 1_000_000)) + ") " + converter.cryptoCurrency);
        System.out.println("Fiat Value: " + fiatValue + " (" + (fiatValue / 100) + "." + String.format("%02d", (fiatValue % 100)) + ") " + converter.fiatCurrency);

        // Example 2: Calculate crypto value from fiat value
        long targetFiatValue = 125925; // Example target fiat value in cents (1259.25 EUR)
        long requiredCryptoValue = converter.calculateCryptoValue(targetFiatValue);
        System.out.println("Target Fiat Value: " + targetFiatValue + " (" + (targetFiatValue / 100) + "." + String.format("%02d", (targetFiatValue % 100)) + ") " + converter.fiatCurrency);
        System.out.println("Required Crypto Value: " + requiredCryptoValue + " (" + (requiredCryptoValue / 1_000_000) + "." + String.format("%06d", (requiredCryptoValue % 1_000_000)) + ") " + converter.cryptoCurrency);
    }
}




```