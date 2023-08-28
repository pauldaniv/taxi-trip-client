#!/bin/sh

COMMAND=$1
docker run -e FACADE_API_HOST=http://api.yellow-taxi.me -e EVENT_FILE_LOCAL=false -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -v $PWD/token.txt:/workspace/token.txt -it --rm me.local/yt-client $@
