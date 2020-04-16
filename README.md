# Feed Buddy
_Feed Buddy_ is a simple RSS and Atom item processor written in Java.


## Building
This project uses [Gradle](https://gradle.org/) with the [Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html).  To build distributable archives, execute the following command:

`gradlew assemble`

This will produce ZIP and TAR archive files in the `build/distributions` directory.

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

### Configuration Elements

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
| `period` | No | The time (in minutes) to poll this feed for content.  If no value is specified, then the period from the parent `feeds` element is used. |
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
| `class` | See note below | Specifies the Java class for this handler. |
| `name` | No | Defines a name for this handler. |
| `ref` | See note below | Specifies the name of the shared handler to use. |

_Note: A `handler` element must provide a value for either the `class` or `ref` property._

#### `property`

Specifies a name-value pair to use as configuration for a `filter` or `handler`.  This element can only be nested under a `filter` or `handler` element.

| Property | Required | Description |
| :------- | :------- | :---------- |
| `name` | Yes | Name of the property. |
| `value` | Yes | Value of the property. |

### Filters

Filters can be used to selectively process items from a feed.  Items that do not match filter criteria are not processed by any handlers.  If multiple filters are defined for a feed, then the item must match all filters to be included for processing.

The following filters are available by default:

#### Regular Expression Evaluator

Filters items by any combination of their title, description, or categories.

**Class:** `com.munzenberger.feed.filter.RegexItemFilter`

| Property Name | Property Value Description |
| :------------ | :------------------------- |
| `title`| If specified, only items with a title matching this regular expression are processed. |
| `description` | If specified, only items with a description matching this regular expression are processed. |
| `category` | If specified, only items with a category matching this regular expression are processed. At least one category needs to match the filter to be processed. If no categories are present, then the item is not processed. |

### Handlers

Handlers are used to process individual feed items.

The following handlers are available by default:

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
| `filter` | If specified, only enclosures with a URL matching this regular expression are downloaded. |

### Example Configuration

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
The distribution archive files contain scripts in the `bin` directory.  Execute the one that is appropriate for your platform.

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

To create your own filter, implement the `com.munzenberger.feed.filter.ItemFilter` interface:

```Java
package com.mypackage;

import com.munzenberger.feed.filter.ItemFilter;
import com.munzenberger.feed.filter.ItemFilterException;
import com.munzenberger.feed.parser.rss.Item;

public class MyItemFilter implements ItemFilter {

    public void setFizz(String value) {
        // Will be called during configuration for a property named 'fizz'
    }

    @Override
    public boolean evaluate(Item item) throws ItemFilterException {
        // Implement your filter's logic, returning true if the item should be processed
    }
}
```

To create your own handler, implement the `com.munzenberger.feed.handler.ItemHandler` interface:

```Java
package com.mypackage;

import com.munzenberger.feed.handler.ItemHandler;
import com.munzenberger.feed.handler.ItemHandlerException;
import com.munzenberger.feed.log.Logger;
import com.munzenberger.feed.parser.rss.Item;

public class MyItemHandler implements ItemHandler {

    public void setFoo(String value) {
        // Will be called during configuration for a property named 'foo'
    }

    @Override
    public void process(Item item, Logger logger) throws ItemHandlerException {
        // Implement your handler logic
    }
}
```

Then include your filter and/or handler in the `feeds.xml` configuration file and add your classes to the classpath when you run Feed Buddy: 

```xml
<feeds>
	<feed url="http://example.com/feed">
		
		<filter class="com.mypackage.MyItemFilter">
			<!-- Will call setFizz("true") in your item filter -->
			<property name="fizz" value="true"/>
		</filter>
		
		<handler class="com.mypackage.MyItemHandler">
			<!-- Will call setFoo("bar") in your item handler -->
			<property name="foo" value="bar"/>
		</handler>
		
	</feed>
</feeds>
```

## License
```
Copyright 2020 Brian Munzenberger

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
