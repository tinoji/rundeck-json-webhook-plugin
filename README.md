# rundeck-json-webhook-plugin
A Rundeck plugin to POST JSON data to a list of Webhook URL(s). This plugin just sends JSON instead of XML to Webhook URL. The documentation of the builtin notification plugin for XML is: https://docs.rundeck.com/docs/manual/notifications/webhooks.html

## Installation
1. Download jar file from the [latest release](https://github.com/tinoji/rundeck-json-webhook-plugin/releases)
1. Move or copy jar file to `$RDECK_BASE/libext/`. The default path should be `/var/lib/rundeck/libext/`.


## Build
You can get jar file by building the source by gradle.
1. git clone
1. `./gradlew build`
1. jar file will be created at `build/libs`


## Configuration
The configuration setting is the same as the default Webhook notification. Check `JSON Webhook` box and enter comma-separated Webhook URLs.

![configuration](https://github.com/tinoji/rundeck-json-webhook-plugin/blob/images/images/configuration.png)


## JSON format
```
{
  "trigger": "start",
  "execution": {
    "id": 7,
    "href": "http://127.0.0.1:4440/project/test/execution/follow/7",
    "status": "running",
    "user": "admin",
    "dateStarted": "Aug 10, 2019 7:55:00 AM",
    "dateStartedUnixtime": 1565423700644,
    "dateStartedW3c": "2019-08-10T07:55:00Z",
    "description": "",
    "argstring": "-testval1 1234",
    "project": "test",
    "loglevel": "DEBUG",
    "job": {
      "id": "58a0ef70-9457-4e44-b053-94b886adeba2",
      "href": "http://127.0.0.1:4440/project/test/job/show/58a0ef70-9457-4e44-b053-94b886adeba2",
      "name": "testjob",
      "group": "",
      "schedule": "",
      "project": "test",
      "description": "",
      "averageDuration": 2609
    },
    "context": {
      "globals": {},
      "job": {
        "successOnEmptyNodeFilter": "false",
        "executionType": "user",
        "wasRetry": "false",
        "user.name": "admin",
        "project": "test",
        "threadcount": "1",
        "url": "http://127.0.0.1:4440/project/test/execution/follow/7",
        "execid": "7",
        "serverUUID": "a14bc3e6-75e8-4fe4-a90d-a16dcc976bf6",
        "serverUrl": "http://127.0.0.1:4440/",
        "loglevel": "DEBUG",
        "name": "testjob",
        "retryInitialExecId": "0",
        "id": "58a0ef70-9457-4e44-b053-94b886adeba2",
        "retryAttempt": "0",
        "username": "admin"
      },
      "option": {
        "testval1": "1234"
      }
    }
  },
  "config": {
    "webhookURL": "http://192.168.1.3:3000, http://192.168.1.3:3001/hoge/foo?val=1234"
  }
}
```


## License
Copyright (c) 2017 Hiroaki Kikuchi.

Licensed under the Apache License-2.0(see `./LICENSE`)
