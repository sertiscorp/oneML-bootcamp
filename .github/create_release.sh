#!/bin/sh

repo=sertiscorp/oneML-bootcamp

upload_url=$(curl -s -H "Authorization: token $GITHUB_TOKEN" \
     -d '{"tag_name": "'"$BOOTCAMP_TAG"'", "name":"'"$BOOTCAMP_TAG"'","body":"'"$RELEASE_BODY"'","prerelease":'"$PRERELEASE"', "target_commitish":"'"$BOOTCAMP_REF"'"}' \
     "https://api.github.com/repos/$repo/releases" | jq -r '.upload_url')

upload_url="${upload_url%\{*}"

count=0
for BINARY in ${BINARIES};
do
    count=$(($count+1));
    LABEL=$(echo ${LABELS} | cut -d' ' -f$count);
    curl -s -H "Authorization: token $GITHUB_TOKEN" \
            -H "Content-Type: application/zip" \
            --data-binary @build/$BINARY \
            "$upload_url?name=$LABEL&label=$LABEL"
done
