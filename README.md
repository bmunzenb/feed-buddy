# Feed Buddy
_Feed Buddy_ is a simple RSS and Atom item processor written in Java.


## Building
This project uses [Gradle](https://gradle.org/) with the [Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html).  To build a distributable archive, execute the following command:

`gradlew distZip`

This will produce a `feed-buddy.zip` file in the `build/distributions` directory.

## Configuration
_Feed Buddy_ is configured using an XML file:

```xml
<feeds period="120">
	<feed url="http://feeds.feedburner.com/codinghorror/">
		<handler class="com.munzenberger.feed.handler.SendEmail">
			<property name="to" value="me@email.com"/>
			<property name="smtpHost" value="smtp.mailserver.net"/>
		</handler>
	</feed>
</feeds>
```

In this configuration file, you define the feeds you want to process.  For each feed, you can define any number of handlers to execute for each item in the feed.

### Elements

#### `feeds`

All configuration files must have a `feeds` root element.  You can specify any number of `feed` and `handler` sub-elements.

| Property | Required | Description |
| :------- | :------- | :---------- |
| `period` | No | Specifies the default time (in minutes) to poll a feed for content. Defaults to 60 minutes. |

#### `feed`

Root element for an RSS or Atom feed.  These elements must be under the `feeds` element.  You can specify any number of `handler` and `filter` sub-elements.

| Property | Required | Description |
| :------- | :------- | :---------- |
| `url` | Yes | The URL to the RSS or Atom feed. |
| `period` | No | The time (in minutes) to poll this feed for content.  If no value is specified, then the period from the `feeds` element is used. |
| `type` | No | Specifies the type of feed.  Use `rss` for RSS feeds or `atom` for Atom feeds.  Defaults to `rss`.|

#### `filter`

Root element to define a feed item filter.  These elements must be under the `feed` element.  You can specify any number of `property` sub-elements.

| Property | Required | Description |
| :------- | :------- | :---------- |
| `class` | Yes | Specifies the Java class for this filter. |

#### `handler`

Root element to define a feed item handler.  These elements can be under the `feeds` element for shared handlers, or under an individual `feed` element.  You can specify any number of `property` sub-elements.

| Property | Required | Description |
| :------- | :------- | :---------- |
| `class` | No* | Specifies the Java class for this handler. |
| `name` | No | Defines a name for this handler. |
| `ref` | No* | Specifies the name of the shared handler to use. |

A `handler` element **must** have either a `class` or `ref` property.

#### `property`

Specifies a name-value pair to use as configuration for a `handler`.  This element can only be nested under a `handler` element.

| Property | Required | Description |
| :------- | :------- | :---------- |
| `name` | Yes | Name of the property. |
| `value` | Yes | Value of the property. |

### Filters

Filters can be used to selectively process items from a feed.  Items that do not match filter criteria are not processed by any handlers.  If multiple filters are defined for a feed, then the item must match all filters to be included for processing.

The following filters have been implemented:

#### Regular Expression Evaluator

**Class:** `com.munzenberger.feed.filter.RegexItemFilter`

| Property Name | Property Value Description |
| :------------ | :------------------------- |
| `title`| (Optional) Regular expression pattern to filter item titles on. |
| `description` | (Optional) Regular expression pattern to filter item descriptions on. |
| `category` | (Optional) Regular expression pattern to filter item categories on. At least one category needs to match the filter to be processed. If no categories are present, then the item is not processed. |

### Handlers

Handlers are used to process individual feed items.

The following handlers have been implemented:

#### Send Mail

Sends an email containing the content of the feed item.

**Class:** `com.munzenberger.feed.handler.SendEmail`

| Property Name | Property Value Description |
| :------------ | :------------------------- |
| `to`| Recipient email address. |
| `from` | Sender email address.  If not specified, the address of the feed item's author is used. |
| `smtpHost` | SMTP server host address. |
| `smtpPort` | SMTP server port number. |
| `auth` | Set to `true` if your SMTP server requires authentication. |
| `startTLSEnable` | Set to `true` to enable the STARTTLS command. |
| `startTLSRequired` | Set to `true` if your SMTP server requires use of the STARTTLS command. |
| `username` | Username to use for SMTP authentication. |
| `password` | Password to use for SMTP authentication. |

#### Download Enclosures

Downloads enclosures included in the feed item.

**Class:** `com.munzenberger.feed.handler.DownloadEnclosures`

| Property Name | Property Value Description |
| :------------ | :------------------------- |
| `targetDir` | The target directory to download enclosures to. Defaults to current directory. |
| `overwriteExisting` | If set to `true`, the handler will overwrite any files in the target directory that already exist.  Defaults to `false`. |
| `useFullPathForFilename` | If set to `true`, the handler will use the full URL path to generate the local filename, otherwise just the filename will be used. |
| `filter` | (Optional) A regular expression that the URL of the enclosure must match in order for it to be downloaded.  If omitted, then all enclosures are downloaded. |

### Example

```xml
<!-- By default, check for content updates every 2 hours -->
<feeds period="120">
	
	<!-- Define a global handler for sending email -->
	<handler name="email" class="com.munzenberger.feed.handler.SendEmail">
		<property name="to" value="me@email.com"/>
		<property name="smtpHost" value="smtp.mailserver.net"/>
	</handler>

	<!-- Check this feed every 6 hours -->
	<feed url="http://feeds.feedburner.com/codinghorror/" period="360">
		<!-- Send an email using the globally defined handler -->
		<handler ref="email"/>
	</feed>
	
	<!-- This is an Atom feed -->
	<feed url="http://mattgemmell.com/feed" type="atom">
		<handler ref="email" />
	</feed>	

	<feed url="http://www.npr.org/rss/podcast.php?id=510289">
		<!-- Download this podcast -->
		<handler class="com.munzenberger.feed.handler.DownloadEnclosures">
			<property name="targetDir" value="C:\Downloads\Planet Money Podcast"/>
		</handler>
	</feed>
		
	<feed url="http://feeds.feedburner.com/filmcast">
		<!-- Send an email and download any enclosures -->
		<handler ref="email" />
		<handler class="com.munzenberger.feed.handler.DownloadEnclosures">
			<property name="targetDir" value="C:\Downloads\Filmcast"/>
		</handler>
	</feed>
	
	<feed url="http://whats-on-netflix.com/feed/">
		<!-- Send an email only if "Matrix" is found in the item description -->
		<filter class="com.munzenberger.feed.filter.RegexItemFilter">
			<property name="description" value=".*Matrix.*"/>
		</filter>
		<handler ref="email" />
	</feed>
	
</feeds>
```

## Executing
The distribution zip file contains scripts in the `bin` directory.  Execute the one that is appropriate for your platform.

The following parameters are supported:

| Parameter | Description |
| :-------- | :---------- |
| `-feeds <file>` | Specifies the location of the configuration file.  Defaults to `feeds.xml` in the current working directory. |
| `-processed <directory>` | Specifies the location where the processed items files are written to.  Defaults to the current working directory. |
| `-log <file>` | Writes the log to the specified file in addition to the console. |
| `-once` | Processes all feeds and handlers once then exits. |
| `-noop` | Executes in no-op mode.  This means all of the feed items are marked as processed, but none of the handlers are executed. |
| `-help` | Prints the help message. |

## Creating Your Own Filters and Handlers

To create your own filter, implement the `com.munzenberger.feed.filter.ItemFilter` interface.  Use your class in the filter definition in the configuration file and make sure your class is in the classpath when you run _Feed Buddy_.  Create bean style setters for any properties that you need.

To create your own handler, implement the `com.munzenberger.feed.handler.ItemHandler` interface.  Use your class in the handler definition in the configuration file and make sure your class is in the classpath when you run _Feed Buddy_.  Create bean style setters for any properties that you need.

For example:

```xml
<feeds>
	<feed url="http://example.com/feed">
		
		<filter class="com.package.MyItemFilter">
			<!-- Will call setWithEnclosure("true") in your item filter -->
			<property name="withEnclosure" value="true"/>
		</filter>
		
		<handler class="com.package.MyItemHandler">
			<!-- Will call setFoo("bar") in your item handler -->
			<property name="foo" value="bar"/>
		</handler>
		
	</feed>
</feeds>
```
## License
```
Copyright 2017 Brian Munzenberger

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