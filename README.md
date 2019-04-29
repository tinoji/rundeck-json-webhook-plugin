# rundeck-json-webhook-plugin
A Rundeck plugin to POST JSON data to a Webhook URL. This plugin just sends JSON instead of XML to Webhook URL. The documentation of the builtin notification plugin for XML is: https://docs.rundeck.com/docs/manual/notifications/webhooks.html

This plugin was developed for [Huginn](https://github.com/huginn/huginn) which supports only Webhook in the JSON format.


## Installation
1. Download jar file from the [latest release](https://github.com/tinoji/rundeck-json-webhook-plugin/releases)
1. Move or copy jar file to `$RDECK_BASE/libext/`. The default path should be `/var/lib/rundeck/libext/`.


## Build
You can get jar file by building the source by gradle.
1. git clone
1. `./gradlew build`
1. jar file will be created at `build/libs`


## Configuration
The configuration setting is the same as the default Webhook notification. Check `JSON Webhook` box and enter Webhook URL.
It is not supported to POST to multiple destinations. Do not enter comma-separated URLs.

![configuration](https://github.com/tinoji/rundeck-json-webhook-plugin/blob/images/images/configuration.png)


## JSON format

```
{
  "execution": {
    "id": 5,
    "href": "http://192.168.33.101:4440/project/test/execution/follow/5",
    "status": "succeeded",
    "user": "user1",
    "dateStarted": "Oct 8, 2017 6:23:25 PM",
    "dateStartedUnixtime": 1507512205135,
    "dateStartedW3c": "2017-10-09T01:23:25Z",
    "description": "",
    "project": "test",
    "succeededNodeListString": "localhost",
    "succeededNodeList": ["localhost"],
    "loglevel": "INFO",
    "dateEnded": "Oct 8, 2017 6:23:26 PM",
    "dateEndedUnixtime": 1507512206211,
    "dateEndedW3c": "2017-10-09T01:23:26Z",
    "nodestatus": {
      "succeeded": 1,
      "failed": 0,
      "total": 1
    },
    "job": {
      "id": "3166e079-5374-450f-a594-a992478f15a9",
      "href": "http://192.168.33.101:4440/project/test/job/show/3166e079-5374-450f-a594-a992478f15a9",
      "name": "test notification",
      "group": "",
      "project": "test",
      "description": "",
      "averageDuration": 1796
    },
    "context": {
      "node": {
        "os-version": "3.10.0-693.2.2.el7.x86_64",
        "hostname": "localhost",
        "os-arch": "amd64",
        "name": "localhost",
        "os-family": "unix",
        "description": "Rundeck server node",
        "os-name": "Linux",
        "username": "rundeck",
        "tags": ""
      },
      "globals": {},
      "job": {
        "successOnEmptyNodeFilter": "false",
        "executionType": "user",
        "wasRetry": "false",
        "user.name": "admin",
        "project": "test",
        "url": "http://192.168.33.101:4440/project/test/execution/follow/5",
        "execid": "5",
        "serverUrl": "http://192.168.33.101:4440/",
        "loglevel": "INFO",
        "name": "test notification",
        "id": "3166e079-5374-450f-a594-a992478f15a9",
        "retryAttempt": "0",
        "username": "admin"
      },
      "option": {}
    }
  },
  "trigger": "success",
  "config": {
    "webhookURL": "http://192.168.10.10:8080"
  }
}
```


## License
Copyright (c) 2017 KIKUCHI Hiroaki.

Licensed under the Apache License-2.0(see `./LICENSE`)
