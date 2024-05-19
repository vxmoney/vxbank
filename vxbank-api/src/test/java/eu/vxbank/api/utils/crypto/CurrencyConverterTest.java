package eu.vxbank.api.utils.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyConverterTest {


    @Test
    void calculateWith4Decimals() {
        // Conversion rate from USDT to EUR (1 USDT = 0.9100 EUR, represented as 9100 with 4 decimals)
        long conversionRate = 9100;
        long conversionRateDecimals = 4;

        // Decimal places for EUR and USDT
        long eurDecimals = 2;
        long usdtDecimals = 6;

        // Initialize the converter with EUR as fiat currency and USDT as crypto currency
        CurrencyConverter converter = new CurrencyConverter("EUR", "USDT", conversionRate, eurDecimals, usdtDecimals, conversionRateDecimals);

        // Example 1: Calculate fiat value from crypto value
        long cryptoValue = 1234567890; // Example crypto value in USDT (1.234567890 USDT)
        long fiatValue = converter.calculateFiatValue(cryptoValue);
        System.out.println("Crypto Value: " + converter.getCryptoDisplayValue(cryptoValue) + " " + converter.cryptoCurrency);
        System.out.println("Fiat Value: " + converter.getFiatDisplayValue(fiatValue) + " " + converter.fiatCurrency);


        {
            // Example 2: Calculate crypto value from fiat value
            long targetFiatValue = 125925; // Example target fiat value in cents (1259.25 EUR)
            long requiredCryptoValue = converter.calculateCryptoValue(targetFiatValue);
            System.out.println("Target Fiat Value: " + converter.getFiatDisplayValue(targetFiatValue) + " " + converter.fiatCurrency);
            System.out.println("Required Crypto Value: " + converter.getCryptoDisplayValue(requiredCryptoValue) + " " + converter.cryptoCurrency);
        }


        // Example 2: Calculate crypto value from fiat value
        long targetFiatValue = 125925; // Example target fiat value in cents (1259.25 EUR)
        long requiredCryptoValue = converter.calculateCryptoValue(targetFiatValue);
        long computedFiatValue = converter.calculateFiatValue(requiredCryptoValue);
        System.out.println("Target Fiat Value: " + targetFiatValue + " " + converter.fiatCurrency);
        System.out.println("Required Crypto Value: " + requiredCryptoValue + " " + converter.cryptoCurrency);
        System.out.println("Computed fiat value: " + computedFiatValue);

        Assertions.assertEquals(targetFiatValue, computedFiatValue);

    }

    @Test
    void calculateWith2Decimals() {
        // Conversion rate from USDT to EUR (1 USDT = 0.9100 EUR, represented as 9100 with 4 decimals)
        long conversionRate = 91;
        long conversionRateDecimals = 2;

        // Decimal places for EUR and USDT
        long eurDecimals = 2;
        long usdtDecimals = 6;

        // Initialize the converter with EUR as fiat currency and USDT as crypto currency
        CurrencyConverter converter = new CurrencyConverter("EUR", "USDT", conversionRate, eurDecimals, usdtDecimals, conversionRateDecimals);

        // Example 1: Calculate fiat value from crypto value
        long cryptoValue = 1234567890; // Example crypto value in USDT (1.234567890 USDT)
        long fiatValue = converter.calculateFiatValue(cryptoValue);
        System.out.println("Crypto Value: " + converter.getCryptoDisplayValue(cryptoValue) + " " + converter.cryptoCurrency);
        System.out.println("Fiat Value: " + converter.getFiatDisplayValue(fiatValue) + " " + converter.fiatCurrency);


        {
            // Example 2: Calculate crypto value from fiat value
            long targetFiatValue = 125925; // Example target fiat value in cents (1259.25 EUR)
            long requiredCryptoValue = converter.calculateCryptoValue(targetFiatValue);
            System.out.println("Target Fiat Value: " + converter.getFiatDisplayValue(targetFiatValue) + " " + converter.fiatCurrency);
            System.out.println("Required Crypto Value: " + converter.getCryptoDisplayValue(requiredCryptoValue) + " " + converter.cryptoCurrency);
        }


        // Example 2: Calculate crypto value from fiat value
        long targetFiatValue = 125925; // Example target fiat value in cents (1259.25 EUR)
        long requiredCryptoValue = converter.calculateCryptoValue(targetFiatValue);
        long computedFiatValue = converter.calculateFiatValue(requiredCryptoValue);
        System.out.println("Target Fiat Value: " + targetFiatValue + " " + converter.fiatCurrency);
        System.out.println("Required Crypto Value: " + requiredCryptoValue + " " + converter.cryptoCurrency);
        System.out.println("Computed fiat value: " + computedFiatValue);

        Assertions.assertEquals(targetFiatValue, computedFiatValue);

    }


}