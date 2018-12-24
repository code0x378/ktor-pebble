# Ktor Pebble Template Feature

A feature to use the Pebble template engine with ktor. 

## Installation

### Add dependency to gradle

Add to the `repositories` block in the `build.gradle` file:

```groovy
maven { url  "https://dl.bintray.com/jeffsmithdev/maven" }
```

Include the artifact:

```groovy
compile 'com.tlogx.ktor:ktor-pebble:0.0.1'
```

Install the feature in ktor:

```kotlin
install(Pebble) {
        install(Pebble) {
            templateDir = "" // resource path,  i.e templateDir = "templates/"
            strictVariables = true // throw exception if variables are missing
            defaultLocale = Locale.US // override Locale.getDefault()
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

**0.0.2**
- Kotlin 1.3 support and minor code fixes/improvements - Thanks Preslav Rachev

**0.0.1**
- Basic functionality

## Notes

- Most feature content taken from [ktor-velocity](https://ktor.io/features/templates/velocity.html)
- [Pebble template engine](http://www.mitchellbosecke.com/pebble/home) by Mitchell Bösecke