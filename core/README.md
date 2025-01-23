# Feed Buddy Core

This is the core module for *Feed Buddy* that contains the primary logic for reading, parsing, and processing feeds.

## User Guide

### Basic Use

The primary class and main entry point for *Feed Buddy* is the `FeedOperator`. Classes of this type are responsible for
coordinating all other components that read and parse a feed, process feed items, and mark them as processed.

There are two implementations:

| Class                 | Description                                                                                                                                                                  |
|-----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `PollingFeedOperator` | Processes feeds by scheduling periodic polling of each feed source using a `Timer`. The `start()` function is asynchronous and clients must call `cancel()` to stop polling. |
| `OnceFeedOperator`    | Processes all feeds once. In this case, the `start()` function is synchronous and will return after all feeds are processed.                                                 |

When creating one of these implementations for use, you will need to supply parameters of the following types in the
constructor:

#### `ItemRegistryFactory`

An instance of this interface creates `ItemRegistry` objects used to look up whether an item has been processed and mark
an item as processed. The only provided implementation is `FileItemRegistryFactory` which writes processed item GUIDs to
a file for each feed processed.

#### `ConfigProvider`

Implementations of this interface provide an instance of an `OperatorConfig` when requested.  The only provided
implementation is `FileConfigProvider` which reads the configuration from a JSON, YAML, or XML file.

#### `ItemProcessorFactory<ItemHanlder>` and `ItemProcessorFactory<ItemFilter>`

These factories are responsible for creating instances of feed item processors, specifically filters which determine
which items in a feed are eligible for processing, and handlers which process each item in a feed. The only provided
implementation is `DefaultItemProcessorFactory<T : ItemProcessor>` which can be used for both filters and handlers.

Note that implementations of `ItemProcessorFactory` must support lookup for item processors that are global to the
operator, and reused for any feed that references them by name via a `ref` property.

#### `Consumer<FeedStatus>`

This is a callback that accepts instances of `FeedStatus` for the purposes of lifecycle notifications. This is commonly
used for logging purposes.  The *Feed Buddy* application module implements a logger using this interface, see 
`LoggingStatusConsumer`.

#### Sample Code

Here is a basic implementation for creating a `FeedOperator` that will poll one or more feeds on a schedule:

```kotlin
// Use the default item registry as a file per feed
val itemRegistryFactory = FileItemRegistryFactory(
    Path("/path/to/registry")
)

// Specify the configuration from a file
val configProvider = FileConfigProvider(
    File("/path/to/config/feeds.json")
)

// Use the default filter and handler factories
val filterFactory = DefaultItemProcessorFactory<ItemFilter>()
val handlerFactory = DefaultItemProcessorFactory<ItemHandler>()

val statusConsumer = Consumer<FeedStatus> { status ->
    // Do something with `status`, e.g. log it
}

val operator = PollingFeedOperator(
    registryFactory = itemRegistryFactory,
    configProvider = configProvider,
    filterFactory = filterFactory,
    handlerFactory = handlerFactory,
    statusConsumer = statusConsumer
)

// Start polling--this is an asynchronous call
operator.start()

// Make sure to eventually call `operator.cancel()`, e.g. from a shutdown hook
```

### Advanced Use

If you want more control over how a feed is read and processed, you can use the `FeedProcessor` class directly instead
of the `FeedOperator`.  This class is a `Runnable` implementation that reads a feed, filters and processes its items,
then marks them as processed.  To create a `FeedProcessor`, you will need to supply parameters of the following types in
the constructor:

#### `FeedSource`

This interface is the source for a feed. The default implementation is an `XMLFeedSource`, which takes a `URL` that
points to an XML formatted RSS or Atom feed.  The `read()` function will access the URL and parse the feed into a `Feed`
object that can then be processed.

#### `ItemRegistry`

This interface defines the methods to look up whether a feed item has been processed, and mark feed items as processed.
The default implementation is a `FileItemRegistry` that requires a path to a file to store the list of item GUIDs that
have been processed.

#### `ItemFilter`

An `ItemFilter` is used to determine which unprocessed items are eligible for processing.  You must pass an object here,
even if it does nothing but evaluate all items as eligible for processing.  Provided implementations:

- `RegexItemFilter` for filtering item title and/or content by regular expressions

*Note that you can compose multiple filters together using the plus operator.*

#### `ItemHandler`

An `ItemHandler` is used to process eligible items.  Provided implementations:

- `SendEmail` for sending an email with item content
- `DownloadEnclosures` for downloading the contents of item enclosures

*Note that you can compose multiple handlers together using the plus operator.*

#### `Consumer<FeedStatus>`

This is a callback that accepts instances of `FeedStatus` for the purposes of lifecycle notifications. This is commonly
used for logging purposes.

#### Sample Code

Here is a basic implementation for creating a `FeedProcessor` that will print the title for any items in a feed where
the item's title contains the word "Foo":

```kotlin
val feedUrl = URI.create("http://www.example.com/feed.xml").toURL()
val feedSource = XMLFeedSource(feedUrl)

// Use an in-memory map of processed items.  A real implementation should persist
// to the filesystem or a database.  See `FileItemRegistry`.
val itemRegistry = object : ItemRegistry {
    private val processed = mutableSetOf<String>()

    override fun contains(item: Item): Boolean {
        // This is just an example.  Be careful as some feeds don't include
        // GUIDs for their items, so implement something more robust.
        return processed.contains(item.guid)
    }

    override fun add(item: Item) {
        processed.add(item.guid)
    }
}

val itemFilter = ItemFilter { context, item, logger ->
    item.title.contains("Foo", ignoreCase = true)
}

val itemHandler = ItemHandler { context, item, logger ->
    // logging sends a `FeedStatus` event to the status consumer
    logger.println(item.title)
}

val statusConsumer = Consumer<FeedStatus> { status ->
    // print messages logged from item processors to the console
    if (status is FeedStatus.ItemProcessorMessage) {
        println(status.message)
    }
}

val processor = FeedProcessor(
    source = feedSource,
    itemRegistry = itemRegistry,
    itemFilter = itemFilter,
    itemHandler = itemHandler,
    statusConsumer = statusConsumer
)

processor.run()
```