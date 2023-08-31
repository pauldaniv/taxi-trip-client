# taxi-trip-client

### Commands

- Login
```shell
docker run -e FACADE_API_HOST=http://api.yellow-taxi.me -e EVENT_FILE_LOCAL=false -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -v $PWD/token.txt:/workspace/token.txt -it --rm me.local/yt-client  login --username pavlo.daniv@sombrainc.com
```

- Push events
```shell
 docker run -e FACADE_API_HOST=http://api.yellow-taxi.me -e EVENT_FILE_LOCAL=false -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -v $PWD/token.txt:/workspace/token.txt -it --rm me.local/yt-client event --count 300 --concurrency 30
```

- Get totals
```shell
 docker run -e FACADE_API_HOST=http://api.yellow-taxi.me -e EVENT_FILE_LOCAL=false -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -v $PWD/token.txt:/workspace/token.txt -it --rm me.local/yt-client totals --year 2018 --month 3 --day 24
```
