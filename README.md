# Feed Buddy
_Feed Buddy_ is a simple RSS and Atom feed processor written in Kotlin. It can be used standalone as a [command-line
application](./app), or as a [library](./core) for building a custom feed processor.

## Building from Source
This project uses [Gradle](https://gradle.org/) with the
[Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html). You can build a distribution
that contains the command-line application via the following Gradle command:

`gradlew build`

The resulting `app/build/distributions` directory will contain the distribution archives.

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
