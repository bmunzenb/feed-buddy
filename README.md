# Feed Buddy
_Feed Buddy_ is a simple RSS and Atom feed processor written in Kotlin.

## Building from Source
This project uses [Gradle](https://gradle.org/) with the [Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html).  To build distributable archives, execute the following command:

`gradlew assemble`

This will produce ZIP and TAR archive files in the `build/distributions` directory.

## Running Feed Buddy

You will need a [Java Virtual Machine (version 11 or higher)](https://www.oracle.com/java/technologies/java-se-glance.html) installed on the host device in order to run Feed Buddy.

Extract the distribution archive to a local directory on the device and execute the script file from the `bin` folder that is appropriate for your operating system, using the following arguments:

```
Usage: feed-buddy [OPTIONS] FEEDS

Options:
  -r, --registry DIRECTORY     Path to processed items registry
  -m, --mode [POLL|ONCE|NOOP]  Sets the operating mode
  -t, --timeout INT            Sets the read timeout in seconds
  -h, --help                   Show this message and exit

Arguments:
  FEEDS  Path to feeds configuration file
```

You must specify a `FEEDS` argument as a path to a readable configuration file in XML, JSON, or YAML format.

Feed Buddy runs in one of three operating modes:

| Mode | Description |
| ---- | ----------- |
| POLL | Feed Buddy will poll the configured feeds and execute all item handlers on a schedule, marking the items as processed.  The application will run until the user force quits it.  This is the default operating mode.
| ONCE | Feed Buddy will read the configured feeds and execute all item handlers once, marking the items as processed, and then exit. |
| NOOP | Feed Buddy will read the configured feeds and mark the items as processed without executing any of the handlers, and then exit. |

By default, the registry of processed items is stored in the current working directory.  If you want to store the registry to a different location, specify its path using the `--registry` option.  The registry is stored as one file per feed.

### Feeds Configuration

Feed Buddy is configured by passing a path to a configuration file as an argument to the startup script.  XML, YAML, and JSON are supported, and Feed Buddy will detect the type of file from its extension, otherwise will assume the format is XML.

The sophistication of the Feed Buddy configuration system can lead to long and/or complex configuration files.  Though complete documentation is provided below, some [sample configurations](sample-config/) are available in this repo for reference since they are too large to reproduce here.

The root of the configuration structure is a `<feeds>` element for XML, and a hash for JSON and YAML.  The following properties are supported at the root level:

| Name | Type | Description |
| ---- | :--: | ----------- |
| `period` | Number | The default time in minutes between reads of a feed. Has a default value of `360` for 3 hours. |
| `handlers` | List | Defines global `handler`s that may be shared by feeds. |
| `filters` | List | Defines global `filter`s that may be shared by feeds. |
| `feeds` | List | Defines a list of `feed` sources. |

The following example root configurations in XML, JSON, and YAML are all equivalent:

```xml
<feeds period="240">
  <!-- Any number of <handler> elements -->
  <!-- Any number of <filter> elements -->
  <!-- One or more <feed> elements -->
</feeds>
```

```json
{
  "period" : 240,
  "handlers" : [],
  "filters" : [],
  "feeds" : []
}
```

```yaml
---
period: 240
handlers: []
filters: []
feeds: []
```

There are three configuration block types that are used: `feed`, `handler`, and `filter`.

#### `feed`
A `feed` block is used to define a feed source, using the following properties:

| Name | Type | Required | Description |
| ---- | :--: | :------: | ----------- |
| `url` | String | Yes | Source URL of the feed. |
| `period` | Number | No | The time in minutes between reads of the feed. If not specified, uses the globally defined period, which has a default value of `360` for 3 hours. |
| `userAgent` | String | No | The value to use in the "User-Agent" HTTP request header when reading the feed. |
| `handlers` | List | No | Defines the handlers to execute for each item in the feed. |
| `filters` | List | No | Defines the filters to apply to items in the feed. An item is only processed if matches the criteria for all specified filters. |

The following example configurations in XML, JSON, and YAML are all equivalent:

```xml
<feed url="http://www.example.com/feed.xml"
      period="480"
      userAgent="Custom/1.0 (Custom User Agent)">
  <!-- One or more <handler> elements -->
  <!-- One or more <filter> elements -->
</feed>
```

```json
{
  "url" : "http://www.example.com/feed.xml",
  "period" : 480,
  "userAgent" : "Custom/1.0 (Custom User Agent)",
  "handlers" : [],
  "filters" : []
}
```

```yaml
- url: "http://www.example.com/feed.xml"
  period: 480
  userAgent: "Custom/1.0 (Custom User Agent)"
  handlers: []
  filters: []
```

#### `handler`
A `handler` block is used to define an operation to execute for a feed item.  Feeds can define any number of handlers to execute for its items.  Handlers may also be defined globally with a `name` and referenced by a handler in a feed, thus allowing multiple feeds to share a common handler. Handlers support the following properties:

| Name | Type | Description |
| ---- | :--: | ----------- |
| `name` | String | The name of the handler, if intended to be shared by multiple feeds. |
| `ref` | String | The name of a shared handler. If specified, the properties for the referenced handler will be used. |
| `type` | String | The fully-qualified class name of the handler implementation to execute for each item in the feed. See the section on supported handlers for possible values. |
| `properties` | Hash | A hash of name to value pairs used to configure the handler. The contents depend on the `type` of the handler. See the section on supported handlers for possible values. |

Note that a `handler` must specify either a `type` for a local handler definition, or a `ref` if referencing a shared handler. Shared handlers must specify a `name` in order to be referenced.

#### `filter`
A `filter` block is used to define rules for filtering items in a feed for processing.  Feeds can define any number of filters for its items.  If multiple filters are defined for a feed, then items in that feed must match all of the defined filters to be processed.  Filters may also be defined globally with a `name` and referenced by filter in a feed, thus allowing multiple feeds to share a common filter. Filters support the following properties:

| Name | Type | Description |
| ---- | :--: | ----------- |
| `name` | String | The name of the filter, if intended to be shared by multiple feeds. |
| `ref` | String | The name of a shared filter. If specified, the properties for the referenced filter will be used. |
| `type` | String | The fully-qualified class name of the filter implementation to match on for each item in the feed. See the section on supported filters for possible values. |
| `properties` | Hash | A hash of name to value pairs used to configure the filter. The contents depend on the `type` of the filter. See the section on supported filters for possible values. |

Note that a `filter` must specify either a `type` for a local filter definition, or a `ref` if referencing a shared filter. Shared filters must specify a `name` in order to be referenced.

#### Supported Handlers

##### Send Email
Use this handler to send an email containing the item's content. Set the handler's `type` property to `com.munzenberger.feed.handler.SendEmail` and configure with the following properties:

| Name | Type | Required | Description |
| ---- | :--: | :------: | ----------- |
| `to` | String | Yes | Recipient email address. |
| `from` | String | Yes | From email address. |
| `smtpHost` | String | Yes | The SMTP hostname of the mail server to send mail through. |
| `smtpPort` | Number | Yes | The SMTP port of the mail server to send mail through. |
| `auth` | Boolean | No | Set to `true` if your SMTP server requires authentication. |
| `username` | String | No | The authentication username if the SMTP server requires authentication. |
| `password` | String | No | The authentication password if the SMTP server requires authentication. |
| `startTLSEnable` | Boolean | No | Set to `true` to enable the STARTTLS command. |
| `startTLSRequired` | Boolean | No | Set to `true` if your SMTP server requires use of the STARTTLS command. |

The following example configurations in XML, JSON, and YAML are all equivalent:

```xml
<handler name="Send Email" type="com.munzenberger.feed.handler.SendEmail">
  <properties>
    <to>recipient@email.com</to>
    <from>sender@email.com</from>
    <smtpHost>smtp.mail.com</smtpHost>
    <smtpPort>25</smtpPort>
    <auth>true</auth>
    <username>johndoe</username>
    <password>fizzbuzz</password>
  </properties>
</handler>
```

```json
{
  "name" : "Send Email",
  "type" : "com.munzenberger.feed.handler.SendEmail",
  "properties" : {
    "to" : "recipient@email.com",
    "from" : "sender@email.com",
    "smtpHost" : "smtp.mail.com",
    "smtpPort" : 25,
    "auth" : true,
    "username" : "johndoe",
    "password" : "fizzbuzz"
  }
}
```

```yaml
- name: "Send Email"
  type: "com.munzenberger.feed.handler.SendEmail"
  properties:
    to: "recipient@email.com"
    from: "sender@email.com"
    smtpHost: "smtp.mail.com"
    smtpPort: 25
    auth: true
    username: "johndoe"
    password: "fizzbuzz"
```

##### Download Enclosures
Use this handler to download enclosures present in a feed's item. Set the handler's `type` property to `com.munzenberger.feed.handler.DownloadEnclosures` and configure with the following properties:

| Name | Type | Required | Description |
| ---- | :--: | :------: | ----------- |
| `targetDirectory` | String | Yes | The target path to write downloaded files to. |

The following example configurations in XML, JSON, and YAML are all equivalent:

```xml
<handler name="Download Enclosures"
         type="com.munzenberger.feed.handler.DownloadEnclosures">
  <properties>
    <targetDirectory>C:\Downloads</targetDirectory>
  </properties>
</handler>
```

```json
{
  "name" : "Download Enclosures",
  "type" : "com.munzenberger.feed.handler.DownloadEnclosures",
  "properties" : {
    "targetDirectory" : "C:\\Downloads"
  }
}
```

```yaml
- name: "Download Enclosures"
  type: "com.munzenberger.feed.handler.DownloadEnclosures"
  properties:
    targetDirectory: "C:\\Downloads"
```

#### Supported Filters

##### Regular Expression Evaluator
Use this filter to match a feed item's title or content with a regular expression.  The item is only processed by the feed's handlers if all of the specified regular expressions match the item's properties.  Set the filter's `type` property to `com.munzenberger.feed.filter.RegexItemFilter` and configure with the following properties:

| Name | Type | Required | Description |
| ---- | :--: | :------: | ----------- |
| `title` | String | No | If specified, matches with the item's title value. |
| `content` | String | No | If specified, matches with the item's content value. |

The following example configurations in XML, JSON, and YAML are all equivalent:

```xml
<filter type="com.munzenberger.feed.filter.RegexItemFilter">
  <properties>
    <content>.*Matrix.*</content>
  </properties>
</handler>
```

```json
{
  "type" : "com.munzenberger.feed.filter.RegexItemFilter",
  "properties" : {
    "content" : ".*Matrix.*"
  }
}
```

```yaml
- type: "com.munzenberger.feed.filter.RegexItemFilter"
  properties:
    content: ".*Matrix.*"
```

### Custom Handler and Filters

You can create your own custom handlers and filters by implementing the `com.munzenberger.feed.handler.ItemHandler` and `com.munzenberger.feed.filter.ItemFilter` interfaces, respectively:

```kotlin
package com.example.feed

class MyFilter : ItemFilter {
  var customProperty: String = "default"
  override fun evaluate(context: FeedContext, item: Item, logger: Logger): Boolean {
    // Implement your filter's logic and return true to process the item
    return true
  }
}

class MyHandler : ItemHandler {
  var customProperty: Int = 42
  override fun execute(context: FeedContext, item: Item, logger: Logger) {
    // Implement your handler's logic
  }
}
```
You can then use the class names as the `type` for handlers and filters in your configuration.  Make sure your classes are on the classpath when you start Feed Buddy.

_Feed Buddy uses [Kotlin reflection](https://kotlinlang.org/docs/reference/reflection.html) to set the properties of handlers and filters.  Because of differences in configuration file format parsing, configuration property values may be coerced to conform to the property type in the class.  Currently only `String` to `Boolean` and `String` to `Int` are supported.  Use of other types will likely result in an exception at runtime when reading the configuration file._

## License
```
Copyright 2024 Brian Munzenberger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
