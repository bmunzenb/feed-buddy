# Feed Buddy Core

This is the core module for *Feed Buddy* that contains the primary logic for reading, parsing, and processing feeds.

## Primary Components

### `FeedOperator`

A `FeedOperator` is the main entry point for *Feed Buddy* and is responsible for coordinating all other components for
reading, parsing, and processing feeds.

There are two implementations:

| Class | Description                                                                                                                                        |
| --- |----------------------------------------------------------------------------------------------------------------------------------------------------|
| `PollingFeedOperator` | Processes feeds by scheduling periodic polling of the feed source using a `Timer`. Once started, clients must call `cancel()` to shutdown polling. |
| `OnceFeedOperator` | Processes all feeds once. |

### `FeedProcessor`

A `FeedProcessor` handles reading a feed from a source and calling all configured `ItemProcessor`s for the feed.

The `FeedOperator` will create `FeedProcessor`s via a supplied `FeedProcessorFactory`.

### `ItemProcessor`

An `ItemProcessor` is a filter or handler that executes on an item in the feed.

An `ItemFilter` filters items for processing, and an `ItemHandler` processes items, such as sending an email or
downloading enclosures.

The `FeedOperator` will create `ItemProcessor` filters and handlers via supplied `ItemProcessorFactory`s.

### `ItemRegistry`

An `ItemRegistry` keeps track of which items in a given feed have already been processed.

The `FeedOperator` will create an `ItemRegistry` via a supplied `ItemRegistryFactory`.

### `FeedStatus`

`FeedStatus` is a sealed class used by the various components to notify a client when events occur during feed
processing. Specify a `Consumer<FeedStatus>` when creating the `FeedOperator`.