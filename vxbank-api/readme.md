# env notes

For now this is not a standard readme. It provides accelerators and bash commands needed to get into flow
as fast as possible. Just use the bash commands that you need and start coding.

## swagger documentation

- [local swagger documentation](http://localhost:8080/swagger-ui/index.html)
- [appengine swagger documentation](https://backend-dot-vxbank-eu-dev.ew.r.appspot.com/swagger-ui/index.html)

## frontend app browser links

- [localhost frontend](http://localhost:3000/)
- [dev frontend](https://vxbank-eu-dev.ew.r.appspot.com/)
- [prod frontend](https://vxbank-eu-prod.ew.r.appspot.com)

## run all tests

- go here and add funds: https://vxbank-eu-dev.ew.r.appspot.com/usageExamples#
- run all datastore tests
- run all vxbank tests

## some bash accelerators

```bash
# start coding
. scripts/configDevEnviromentScripts.sh
openIntellij


# set dev env
export MY_PROTOCOL=https
export MY_BASE_URL=://backend-dot-vxbank-eu-dev.ew.r.appspot.com

# set dev prod
export MY_PROTOCOL=https
export MY_BASE_URL=://backend-dot-vxbank-eu-prod.ew.r.appspot.com

# det dev localhost
export MY_PROTOCOL=http
export MY_BASE_URL=://localhost:8080

# ping
curl --location --request GET "${MY_PROTOCOL}${MY_BASE_URL}/ping/getEnvironment"

# deploy index
gcloud app deploy src/main/appengine/index.yaml
```

## some testing notes

### stripe specific tests

Stripe is resetting all data on their servers every 24 hours. Because of this if you want that all stripe related
tests to pass you should initialize platform euro and ron funds

- [testing corner](https://vxbank-eu-dev.ew.r.appspot.com/usageExamples#)
- login if needed
- Test initialization tools / Initiate platform euro funds
- Test initialization tools / Initiate platform ron funds
- Use card: 4000000000000077

### oauth specific tests

There is a test that demonstrates how to side link an user to a valid stripe id.
This technique is used in order to have an emulator and datastore use linked to a specific stripe id

- set special tests value = true
- [TestEnvValues RUN_SPECIAL_TESTS](./src/test/java/eu/vxbank/api/testutils/TestEnvValues.java)
- [SideLinkUserTest](./src/test/java/eu/vxbank/api/stripe/SideLinkUserTest.java)

Instructions

- bootRun
- . scripts/configDevEnviromentScripts.sh
- openVsCode
- . scripts/configDevEnviromentScripts.sh
- initEnvLocalhost
- npm run dev
- DeveloperExamples/GenerateUserAndLogin
- Copy vxToken
- Update TestEnvValues.SPECIAL_VX_TOKEN value

# percentage settings

On java side the percentage is configured with 2 decimals
Java percentage examples

- integrationPercentage = 200 is equivalent with 2%

## stripe flow

- On stripe side the client pays using PublicEventEndpoint.clientDepositFunds
    - this calls createStripeSessionClientDepositFunds
    - we compute the percentage and we have this that sets the vxmoney fee and also event organizer received value
        ```
        paymentIntentDataBuilder.setApplicationFeeAmount(applicationFee)
          .setTransferData(SessionCreateParams.PaymentIntentData.TransferData.builder()
              .setDestination(vxStripeConfig.stripeAccountId) // this is accout id of event organizer
              .build()
        );
        ```

- when the webhook hits to do not move stripe funds but only vxmoney funds.
- we mark the payment intent as completed. This automaticaly render all get client report to consider also this new
  values as part of the total debit. 
 

