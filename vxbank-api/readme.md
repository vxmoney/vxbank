# env notes

For now this is not a standard readme. It provides accelerators and bash commands needed to get into flow
as fast as possible. Just use the bash commands that you need and start coding.

## swagger documentation

- [local swagger documentation](http://localhost:8080/swagger-ui/index.html)
- [appengine swagger documentation](https://backend-dot-vxbank-eu-dev.ew.r.appspot.com/swagger-ui/index.html)

## some bash accelerators

```bash
# start coding
. scripts/configDevEnviromentScripts.sh
openIntellij


# set dev env
export MY_PROTOCOL=https
export MY_BASE_URL=://backend-dot-vxbank-eu-dev.ew.r.appspot.com

# det localhost env
export MY_PROTOCOL=http
export MY_BASE_URL=://localhost:8080

# ping
curl --location --request GET "${MY_PROTOCOL}${MY_BASE_URL}/ping/getEnvironment"
```