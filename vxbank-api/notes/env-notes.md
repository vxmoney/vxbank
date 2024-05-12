# env notes

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/home/bogdan/Documents/mysandbox-v4-key.json"
```

## swagger documentation

- [local swagger documentation](http://localhost:8080/swagger-ui/index.html)
- [appengine swagger documentation](https://backend-dot-vxbank-eu-dev.ew.r.appspot.com/swagger-ui/index.html)

## some dev curl calls

```bash
# dev environment
export MY_PROTOCOL=https
export MY_BASE_URL=://backend-dot-vxbank-eu-dev.ew.r.appspot.com

# ping
curl --location --request GET "${MY_PROTOCOL}${MY_BASE_URL}/ping/getEnvironment"
```

# stripe accelerators
[stripe cli releases](https://github.com/stripe/stripe-cli/releases)
```bash
stripe login
stripe listen --forward-to localhost:8080/stripeWebhook
```

# some form data
Test card: 4000000000000077
User website: https://www.linkedin.com/in/bogdan-oloeriu