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