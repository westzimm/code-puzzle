### Start the server
```
lein run
```

### order-id-asc

```bash
curl http://localhost:8088/analytics/report\?order-by\=order-id-asc | python -m json.tool
```
### session-type-dec

```bash
curl http://localhost:8088/analytics/report\?order-by\=session-type-desc | python -m json.tool
```
### unit-price-dollars-asc

```bash
curl http://localhost:8088/analytics/report\?order-by\=unit-price-dollars-asc | python -m json.tool
```
