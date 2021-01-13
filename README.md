# Ktor Pebble Template Feature

A feature to use the Pebble template engine with ktor. 

**Note: Version 0.0.5 and above is for ktor 1.3+, Version 0.0.4 is for <= 1.2**

## Installation

### Add dependency to gradle

Add to the `repositories` block in the `build.gradle` file:

```groovy
maven { url  "https://dl.bintray.com/jeffsmithdev/maven" }
```

Include the artifact:

```groovy
compile 'com.tlogx.ktor:ktor-pebble:0.0.6'
```

Install the feature in ktor with optional config:

```kotlin
install(Pebble) {
        install(Pebble) {
            templateDir = "" // resource path,  i.e templateDir = "templates/"
            strictVariables = true // throw exception if variables are missing
            defaultLocale = Locale.US // override Locale.getDefault()
            cacheActive = true // flag to activate/deactivate template caching
            allowGetClass = false // throws an exception if you try to access the class/getClass attribute
            greedyMatchMethod = false  // greedy matching mode for finding java method
        }
}
```

## Usage

When Pebble is configured, you can call the `call.respond` method with a `PebbleContent` instance: 

```kotlin
routing {
    get("/") {
                    val model = mapOf(
                            "title" to "Ktor Pebble Test Page",
                            "description" to "A Ktor feature to use the pebble template engine by Mitchell Bösecke",
                            "myDogs" to listOf("Bebe", "Dot", "Brownie", "Bella")
                    )
                    call.respond(PebbleContent("example.peb", model, "e"))
    }
}
```

## Todo

- Add additional pebble configuration options

## Releases

**0.0.6**
- Updated ktor to 1.5.0, kotlin to 1.4.21 and pebble to 3.1.4

**0.0.5**
- Updated ktor to 1.3.0

**0.0.4**
- Updated kotlin (1.3.50), ktor (1.2.4) and pebble (3.1.0) - Thanks Richard Scorer

**0.0.3**
- Added additional pebble configuration options
- Upgraded pebble to v2.4.0

**0.0.2**
- Kotlin 1.3 support and minor code fixes/improvements - Thanks Preslav Rachev

**0.0.1**
- Basic functionality

## Notes

- Most feature content taken from [ktor-velocity](https://ktor.io/docs/velocity.html)
- [Pebble template engine](http://pebbletemplates.io) by [Mitchell Bösecke](http://www.mitchellbosecke.com)
