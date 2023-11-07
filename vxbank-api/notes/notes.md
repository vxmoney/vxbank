# just notes

these are just notes for this project

# some usefull curl
```bash
curl https://vxbank-eu-dev.ew.r.appspot.com
curl https://vxbank-eu-dev.ew.r.appspot.com/testDatastore
curl https://vxbank-eu-dev.ew.r.appspot.com/testSimpleService

curl localhost:8080
curl localhost:8080/testDatastore
curl localhost:8080/testSimpleService
curl localhost:8080/testExampleService
```
[checkout](http://localhost:8080/checkout.html)


# start datastore emulator needed for tests
```bash
gcloud beta emulators datastore start --project=my-project-id --no-store-on-disk --consistency 1.0
```


# stripe notes
[stripe setup quickstart](https://stripe.com/docs/development/quickstart)

[prices](https://stripe.com/docs/api/prices/create)

```bash
curl https://api.stripe.com/v1/prices \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b: \
  -d unit_amount=2000 \
  -d currency=eur \
  -d "recurring[interval]"=month \
  -d product=prod_OxtTkNOpZkOhNU
  

curl https://api.stripe.com/v1/prices/price_1O9yCZB6aHGAQTGCAeYvZooF \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b:

  
```

# create product Gold Special
```bash
curl https://api.stripe.com/v1/products \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b: \
  -d name="Gold Special"
```
```javascript
{
  "id": "prod_OxuHSvDaem0kwC",
  "object": "product",
  "active": true,
  "attributes": [],
  "created": 1699398828,
  "default_price": null,
  "description": null,
  "features": [],
  "images": [],
  "livemode": false,
  "metadata": {},
  "name": "Gold Special",
  "package_dimensions": null,
  "shippable": null,
  "statement_descriptor": null,
  "tax_code": null,
  "type": "service",
  "unit_label": null,
  "updated": 1699398828,
  "url": null
}
```
# create price
```bash
curl https://api.stripe.com/v1/prices \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b: \
  -d unit_amount=1 \
  -d currency=eur \
  -d product=prod_OxuHSvDaem0kwC

```
```javascript
{
  "id": "price_1O9yaNB6aHGAQTGC9epIyP2F",
  "object": "price",
  "active": true,
  "billing_scheme": "per_unit",
  "created": 1699399291,
  "currency": "eur",
  "custom_unit_amount": null,
  "livemode": false,
  "lookup_key": null,
  "metadata": {},
  "nickname": null,
  "product": "prod_OxuHSvDaem0kwC",
  "recurring": null,
  "tax_behavior": "unspecified",
  "tiers_mode": null,
  "transform_quantity": null,
  "type": "one_time",
  "unit_amount": 1,
  "unit_amount_decimal": "1"
}(
```
