Feed Buddy
==========

About
------------
_Feed Buddy_ is a simple RSS and Atom item processor.

Configuration
-------------
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

In this configuration file, you define the feeds you want to process.  For each feed, you can define any number of `handler`s to execute for each item in the feed.

### Elements

#### feeds

All configuration files must have a `feeds` root element.  You can specify any number of `feed` and `handler` sub-elements.

| Property | Required | Description |
| -------- | -------- | ----------- |
| `period` | No | Defines the default time (in minutes) to poll a feed for content. Defaults to 60 minutes. |

#### feed

Root element for an RSS or Atom feed.  These elements must be under the `feeds` element.  You can specify any number of `handler` sub-elements.

| Property | Required | Description |
| -------- | -------- | ----------- |
| `url` | Yes | The URL to the RSS or Atom feed |
| `period` | No | The time (in minutes) to poll this feed for content.  If no value is specified, then the default period from the `feeds` property is used. |
| `type` | No | Specifies the type of feed.  Use `rss` for RSS feeds or `atom` for Atom feeds.  Defaults to `rss`.|


#### handler

Root element to define a feed item handler.  These elements can be under the `feeds` element for shared handlers, or under an individual `feed` element.  You can specify any number of `property` sub-elements.

| Property | Required | Description |
| -------- | -------- | ----------- |
| `class` | No | Defines the Java class for this handler. Required if no `ref` property is specified. |
| `name` | No | Defines a name for this handler. |
| `ref` | No | Specifies the name of handler to use. Required if no `class` property is specified. |

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
	
	
	</feeds>
```