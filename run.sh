#!/bin/sh

touch token.txt
docker run \
  -e FACADE_API_HOST=https://api.yellow-taxi.me \
  -e EVENT_FILE_LOCAL=false \
  -e AWS_ACCESS_KEY_ID \
  -e AWS_SECRET_ACCESS_KEY \
  -v $PWD/token.txt:/workspace/token.txt \
  -it \
  --pull=always \
  --rm $AWS_DOMAIN_OWNER_ID.dkr.ecr.us-east-2.amazonaws.com/taxi-trip-client:latest $@ | tee -a client.log

