# just notes

these are just notes for this project

# some usefull curl
```bash
curl https://backend-dot-vxbank-eu-dev.ew.r.appspot.com/
curl https://backend-dot-vxbank-eu-dev.ew.r.appspot.com/stripeWebhook

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

## create payment intent

```bash
curl https://api.stripe.com/v1/payment_intents \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b: \
  -d amount=2000 \
  -d currency=eur \
  -d "automatic_payment_methods[enabled]"=true
```
```javascript
{
  "id": "pi_3O9ygUB6aHGAQTGC16aAACTZ",
  "object": "payment_intent",
  "amount": 2000,
  "amount_capturable": 0,
  "amount_details": {
    "tip": {}
  },
  "amount_received": 0,
  "application": null,
  "application_fee_amount": null,
  "automatic_payment_methods": {
    "allow_redirects": "always",
    "enabled": true
  },
  "canceled_at": null,
  "cancellation_reason": null,
  "capture_method": "automatic",
  "client_secret": "pi_3O9ygUB6aHGAQTGC16aAACTZ_secret_FI8Btf20OuTjPYeQxzHzwgSuv",
  "confirmation_method": "automatic",
  "created": 1699399670,
  "currency": "eur",
  "customer": null,
  "description": null,
  "invoice": null,
  "last_payment_error": null,
  "latest_charge": null,
  "livemode": false,
  "metadata": {},
  "next_action": null,
  "on_behalf_of": null,
  "payment_method": null,
  "payment_method_configuration_details": {
    "id": "pmc_1O9xKwB6aHGAQTGCEkwu30iT",
    "parent": null
  },
  "payment_method_options": {
    "bancontact": {
      "preferred_language": "en"
    },
    "card": {
      "installments": null,
      "mandate_options": null,
      "network": null,
      "request_three_d_secure": "automatic"
    },
    "eps": {},
    "giropay": {},
    "ideal": {},
    "link": {
      "persistent_token": null
    }
  },
  "payment_method_types": [
    "card",
    "bancontact",
    "eps",
    "giropay",
    "ideal",
    "link"
  ],
  "processing": null,
  "receipt_email": null,
  "review": null,
  "setup_future_usage": null,
  "shipping": null,
  "source": null,
  "statement_descriptor": null,
  "statement_descriptor_suffix": null,
  "status": "requires_payment_method",
  "transfer_data": null,
  "transfer_group": null
}
```

## Stripe Checkout Session
```bash
curl https://api.stripe.com/v1/checkout/sessions \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b: \
  --data-urlencode success_url="https://example.com/success" \
  -d "line_items[0][price]"=price_1O9yaNB6aHGAQTGC9epIyP2F \
  -d "line_items[0][quantity]"=2 \
  -d mode=payment \
  
  

```
```javascript
{
  "id": "cs_test_a16na9dYJBL2MsTRIW8MyktSAut2jQByHHaWOSTMG416CgOCkb8lqdBg3w",
  "object": "checkout.session",
  "after_expiration": null,
  "allow_promotion_codes": null,
  "amount_subtotal": 2,
  "amount_total": 2,
  "automatic_tax": {
    "enabled": false,
    "status": null
  },
  "billing_address_collection": null,
  "cancel_url": null,
  "client_reference_id": null,
  "client_secret": null,
  "consent": null,
  "consent_collection": null,
  "created": 1699400847,
  "currency": "eur",
  "currency_conversion": null,
  "custom_fields": [],
  "custom_text": {
    "shipping_address": null,
    "submit": null,
    "terms_of_service_acceptance": null
  },
  "customer": null,
  "customer_creation": "if_required",
  "customer_details": null,
  "customer_email": null,
  "expires_at": 1699487247,
  "invoice": null,
  "invoice_creation": {
    "enabled": false,
    "invoice_data": {
      "account_tax_ids": null,
      "custom_fields": null,
      "description": null,
      "footer": null,
      "metadata": {},
      "rendering_options": null
    }
  },
  "livemode": false,
  "locale": null,
  "metadata": {},
  "mode": "payment",
  "payment_intent": null,
  "payment_link": null,
  "payment_method_collection": "if_required",
  "payment_method_configuration_details": {
    "id": "pmc_1O9xKwB6aHGAQTGCEkwu30iT",
    "parent": null
  },
  "payment_method_options": {},
  "payment_method_types": [
    "ideal"
  ],
  "payment_status": "unpaid",
  "phone_number_collection": {
    "enabled": false
  },
  "recovered_from": null,
  "setup_intent": null,
  "shipping_address_collection": null,
  "shipping_cost": null,
  "shipping_details": null,
  "shipping_options": [],
  "status": "open",
  "submit_type": null,
  "subscription": null,
  "success_url": "https://example.com/success",
  "total_details": {
    "amount_discount": 0,
    "amount_shipping": 0,
    "amount_tax": 0
  },
  "ui_mode": "hosted",
  "url": "https://checkout.stripe.com/c/pay/cs_test_a16na9dYJBL2MsTRIW8MyktSAut2jQByHHaWOSTMG416CgOCkb8lqdBg3w#fidkdWxOYHwnPyd1blpxYHZxWjA0Sjw2c05HM2RNQkRUUUJGQmNBckNiUnJdNWpfYVJiMDBmSX1fUXFGfHxzQXZNMkFjfXNOYFc3Z1RUcUtfSn9fUUNBZ1FMNExMSXc2PVBwVGFrX3VpS18wNTVUYE12SlwwdycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"
}
```


## Stripe Checkout Session with line items
```bash
curl https://api.stripe.com/v1/checkout/sessions \
  -u sk_test_secret_key: \
  --data-urlencode success_url="https://checkout.stripe.com/success" \
  --data-urlencode cancel_url="https://checkout.stripe.com/cancel" \
  -d "payment_method_types[]=card" \
  -d "payment_method_types[]=ideal" \
  -d "payment_method_types[]=sofort" \
  -d mode=payment \
  -d "line_items[0][price_data][currency]"="eur" \
  -d "line_items[0][price_data][product_data][name]"="Test Product 2 Name" \
  -d "line_items[0][price_data][unit_amount]"=3000 \
  -d "line_items[0][quantity]"=1
  
```
```javascript
{
  "id": "cs_test_a1wB7ja7EtYUlSERFXr76gE2hphr9up4Bjj8R0Rc8s8WoOwgjSbFgsJU0A",
  "object": "checkout.session",
  "after_expiration": null,
  "allow_promotion_codes": null,
  "amount_subtotal": 3000,
  "amount_total": 3000,
  "automatic_tax": {
    "enabled": false,
    "status": null
  },
  "billing_address_collection": null,
  "cancel_url": "https://checkout.stripe.com/cancel",
  "client_reference_id": null,
  "client_secret": null,
  "consent": null,
  "consent_collection": null,
  "created": 1699636633,
  "currency": "eur",
  "currency_conversion": null,
  "custom_fields": [],
  "custom_text": {
    "shipping_address": null,
    "submit": null,
    "terms_of_service_acceptance": null
  },
  "customer": null,
  "customer_creation": "if_required",
  "customer_details": null,
  "customer_email": null,
  "expires_at": 1699723033,
  "invoice": null,
  "invoice_creation": {
    "enabled": false,
    "invoice_data": {
      "account_tax_ids": null,
      "custom_fields": null,
      "description": null,
      "footer": null,
      "metadata": {},
      "rendering_options": null
    }
  },
  "livemode": false,
  "locale": null,
  "metadata": {},
  "mode": "payment",
  "payment_intent": null,
  "payment_link": null,
  "payment_method_collection": "if_required",
  "payment_method_configuration_details": null,
  "payment_method_options": {},
  "payment_method_types": [
    "card",
    "ideal",
    "sofort"
  ],
  "payment_status": "unpaid",
  "phone_number_collection": {
    "enabled": false
  },
  "recovered_from": null,
  "setup_intent": null,
  "shipping_address_collection": null,
  "shipping_cost": null,
  "shipping_details": null,
  "shipping_options": [],
  "status": "open",
  "submit_type": null,
  "subscription": null,
  "success_url": "https://checkout.stripe.com/success",
  "total_details": {
    "amount_discount": 0,
    "amount_shipping": 0,
    "amount_tax": 0
  },
  "ui_mode": "hosted",
  "url": "https://checkout.stripe.com/c/pay/cs_test_a1wB7ja7EtYUlSERFXr76gE2hphr9up4Bjj8R0Rc8s8WoOwgjSbFgsJU0A#fidkdWxOYHwnPyd1blpxYHZxWjA0Sjw2c05HM2RNQkRUUUJGQmNBckNiUnJdNWpfYVJiMDBmSX1fUXFGfHxzQXZNMkFjfXNOYFc3Z1RUcUtfSn9fUUNBZ1FMNExMSXc2PVBwVGFrX3VpS18wNTVUYE12SlwwdycpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl"
}
```

# get suceeded payments
```bash
curl https://api.stripe.com/v1/payment_intents \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b: \
  -d "status"="succeeded"

```


## lets find out the collected fee

timeStamp: `1699647266991`
sessionId: `cs_test_a1wYz2BavhP0ATB5SaBWEEk27w4guQ4COkHlef3l7Xgxhto9VkAwqIW6sm`
paymentIntentId: `pi_3OB17aB6aHGAQTGC0hTBzewV`
balanceTransactionId: `txn_3OB17aB6aHGAQTGC0ijmhsog`

To find out the collected fee you have to:
- get the session by sessionId
- get the paymentIntent by paymentIntentId
- get the balanceTransaction by balanceTransactionId
- You find hte fee in the fee field In this example fee is 1.23 Euro: Example  "fee": 123, 

```bash
# get session by id
curl https://api.stripe.com/v1/checkout/sessions/cs_test_a1wYz2BavhP0ATB5SaBWEEk27w4guQ4COkHlef3l7Xgxhto9VkAwqIW6sm \
-u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b:

## get intent
curl https://api.stripe.com/v1/payment_intents/pi_3OB17aB6aHGAQTGC0hTBzewV \
  -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b:


curl https://api.stripe.com/v1/balance_transactions/txn_3OB17aB6aHGAQTGC0ijmhsog -u sk_test_51O93vKB6aHGAQTGCjNsNa75J2T8ilFFZpS4a441LBEceglDwUnll3GvpzaeIvCkw6nnWgFxsQY2J34ex4oJjoinm00TmBT4a0b:
```


