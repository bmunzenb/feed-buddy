# Feed Buddy
_Feed Buddy_ is a simple RSS and Atom item processor written in Java.


## Building
This project uses [Maven](http://maven.apache.org/).  To build, execute the following command:

`mvn package`

This will create a target directory containing the `feed-buddy.jar` file.  This file contains all of the dependecies baked in. 

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

#### feeds

All configuration files must have a `feeds` root element.  You can specify any number of `feed` and `handler` sub-elements.

| Property | Required | Description |
| -------- | -------- | ----------- |
| `period` | No | Specifies the default time (in minutes) to poll a feed for content. Defaults to 60 minutes. |

#### feed

Root element for an RSS or Atom feed.  These elements must be under the `feeds` element.  You can specify any number of `handler` sub-elements.

| Property | Required | Description |
| -------- | -------- | ----------- |
| `url` | Yes | The URL to the RSS or Atom feed. |
| `period` | No | The time (in minutes) to poll this feed for content.  If no value is specified, then the period from the `feeds` element is used. |
| `type` | No | Specifies the type of feed.  Use `rss` for RSS feeds or `atom` for Atom feeds.  Defaults to `rss`.|


#### handler

Root element to define a feed item handler.  These elements can be under the `feeds` element for shared handlers, or under an individual `feed` element.  You can specify any number of `property` sub-elements.

| Property | Required | Description |
| -------- | -------- | ----------- |
| `class` | No* | Specifies the Java class for this handler. |
| `name` | No | Defines a name for this handler. |
| `ref` | No* | Specifies the name of the shared handler to use. |

A `handler` element **must** have either a `class` or `ref` property.

#### property

Specifies a name-value pair to use as configuration for a `handler`.  This element can only be nested under a `handler` element.

| Property | Required | Description |
| -------- | -------- | ----------- |
| `name` | Yes | Name of the property. |
| `value` | Yes | Value of the property. |

### Handlers

The following handlers have been implemented:

#### Send Mail

Sends an email containing the content of the feed item.

**Class:** `com.munzenberger.feed.handler.SendEmail`

| Property Name | Property Value Description |
| ------------- | -------------------------- |
| `to`| Recipient email address. |
| `from` | Sender email address, if not specified or valid in the feed item. |
| `smtpHost` | SMTP server host address. |
| `smtpPort` | SMTP server port number. |
| `auth` | Set to `true` if your SMTP server requires authentication. |
| `username` | Username to use for SMTP authentication. |
| `password` | Password to use for SMTP authentication. |

#### Download Enclosures

Downloads all of the enclosures included in the feed item.

**Class:** `com.munzenberger.feed.handler.DownloadEnclosures`

| Property Name | Property Value Description |
| ------------- | -------------------------- |
| `targetDir` | The target directory to download enclosures to. Defaults to current directory. |
| `overwriteExisting` | If set to `true`, the handler will overwrite any files in the target directory that already exist.  Defaults to `false`. |

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
	
</feeds>
```

## Running
Run _Feed Buddy_ using the following command:

`java -jar feed-buddy.jar`

By default, it will look for a `feeds.xml` configuration file in the same directory as execution.

The following parameters are supported:

| Parameter | Description |
| --------- | ----------- |
| `-feeds <config>` | Specifies the location of the configuration file. |
| `-noop` | Executes in no-op mode.  This means all of the feed items are marked as processed, but none of the handlers are executed. |

The output is both printed to the console and written to a log file.

## Creating Your Own Handlers
To create your own handler, you just have to implement the `com.munzenberger.feed.handler.ItemHandler` interface.  Use your class in the handler definition in the configuration file and make sure your class is in the classpath when you run _Feed Buddy_.