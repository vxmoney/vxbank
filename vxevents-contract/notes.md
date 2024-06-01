# dev notes

```bash
cargo install multiversx-sc-meta --locked
sc-meta new --template empty --name vx-events
sc-meta install wasm32
sc-meta test-gen
sc-meta all build
sc-meta test
```

- other tools: https://utils.multiversx.com/converters#string-converters-base64-to-string
- local testnet: https://docs.multiversx.com/developers/setup-local-testnet/
- faucet: https://r3d4.fr/faucet
- on stripe we use 2 decimals. integrationPercentage: 200 = 2% 
- on smart contract we use 4 decimals. integrationPercentage 35000 = 3.5%


