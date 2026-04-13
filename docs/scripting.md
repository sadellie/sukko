# Scripting

!!! info "In-app documentation"

    Methods, constants and templates are also available in the app. Try opening any script and click question mark (?) icon.

## Basic syntax

### Types

Scripts support 3 types of values.

#### Number

```kotlin
12
45.78
-90
```

#### Text

```kotlin
"texts must be in quotes"
```

#### Boolean
```kotlin
true
false
```

### Operators

#### Declare a local variable

Local variables can make your script more readable and efficient.

Use `=` to declare a variable:
```kotlin
x = 123
```

Local variables can be reassigned using same syntax:
```kotlin
x = "new value"
```

You can use multiple variables. In the following example `z` will be `10`:
```kotlin
x = 2
y = 5
z = x * y
```

!!! info "Variable types are dynamic"
    
    Same local variable can hold text, number or boolean

#### Basic math

You can add, subtract, multiply and divide numbers.

- Addition `+`
- Substraction `-`
- Multiplication `*`
- Division `/`

Example:

```kotlin
2 + 2
5 - (-3)
8 * 456.123
99.999 / 3
```

!!! info "Unary minus"

    Subtraction symbol can also be used to represent negative numbers

#### Equality checks

Equality checks work only between same types.

Supported checks:

- Equal `==`
- Not equal `!=`
- Less `<`
- Greater `>`
- Less or equal `<=`
- Greater or equal `>=`

Example:

```kotlin
(2 + 2) == 4
"text 1" != "text 2"
```

!!! info "Brackets matter"
    
    Use brackets to to avoid unexpected issues with complex expressions

#### Boolean operations

Boolean values can be combined to create a new value. This helps checking for multiple conditions.

Supported operations:
- `||` – OR (disjunction)
- `&&` – AND (conjunction)
- `!` – NOT (negation)

Example:
```kotlin
isMonday = ... // some boolean
isSunday = ... // some other boolean
if(isMonday || isSunday, "Weekend", "Not weekend")
```

This will return `Weekend` if either `isMonday` or `isSunday` is `true`.

## API

### Constants

#### Battery status [TEXT]

Current battery status: CHARGING, DISCHARGING, FULL, NOT_CHARGING, UNKNOWN

```kotlin
batteryStatus
```

#### Battery level [NUMBER]

Current battery level. From 0 to 100

```kotlin
batteryLevel
```

#### Battery charge/discharge in seconds [NUMBER]

How many seconds left until battery charges to 100% or discharges to 0%

```kotlin
batteryFullEmpty
```

#### Unix timestamp [NUMBER]

How many seconds passed since Jan 1st 1970

```kotlin
currentTimestamp
```

#### Media artist [TEXT]

The artist of the last playing media. Can be empty.

```kotlin
mediaArtist
```

#### Media title [TEXT]

The title of the last playing media. Can be empty.

```kotlin
mediaTitle
```

#### Media playback duration [NUMBER]

Playback duration of the last playing media in seconds

```kotlin
mediaDuration
```

#### Media playback position [NUMBER]

Playback position of the last playing media in seconds

```kotlin
mediaPosition
```

#### Media cover URI [TEXT]

URI (link) for currently media cover of currently playing media. Can be empty.

```kotlin
mediaCover
```

#### Media player name [TEXT]

Name of currently playing media source (app name). Can be empty.

```kotlin
playerName
```

#### Media player icon [TEXT]

Uri to icon of currently playing media source (app logo). Can be empty.

```kotlin
playerIcon
```

#### Media player state [TEXT]

Current state of player. Possible values: NONE, STOPPED, PAUSED, PLAYING, FAST_FORWARDING, REWINDING, BUFFERING, ERROR, SKIPPING_TO_PREVIOUS, SKIPPING_TO_NEXT, SKIPPING_TO_QUEUE_ITEM

```kotlin
playerState
```

#### Device model [TEXT]

Current device model

```kotlin
deviceModel
```

#### Minimal music volume [NUMBER]

Minimal volume of audio streams for music playback. Usually 0

```kotlin
volumeMusicMin
```

#### Current music volume [NUMBER]

Current volume of audio streams for music playback

```kotlin
volumeMusic
```

#### Max music volume [NUMBER]

Max volume of audio streams for music playback

```kotlin
volumeMusicMax
```

### Methods

#### Get global string [TEXT]

Get current value of a global string

```kotlin
globalString(id)
```

- `id` [NUMBER] - Id of a global string, see Globals tab in editor

#### Get global number [NUMBER]

Get current value of a global number

```kotlin
globalNumber(id)
```

- `id` [NUMBER] - Id of a global number, see Globals tab in editor

#### Get global boolean [BOOL]

Get current value of a global boolean

```kotlin
globalBoolean(id)
```

- `id` [NUMBER] - Id of a global boolean, see Globals tab in editor

#### Set global number [NUMBER]

Set value for global number and return it. Doesn't change initial value.

```kotlin
setGlobalNumber(id, value)
```

- `id` [NUMBER] - Id of a global number, see Globals tab in editor
- `value` [NUMBER] - New value

#### Set global string [TEXT]

Set value for global text and return it. Doesn't change initial value.

```kotlin
setGlobalString(id, value)
```

- `id` [NUMBER] - Id of a global text, see Globals tab in editor
- `value` [TEXT] - New value

#### Set global boolean [BOOL]

Set value for global boolean and return it. Doesn't change initial value.

```kotlin
setGlobalBoolean(id, value)
```

- `id` [NUMBER] - Id of a global boolean, see Globals tab in editor
- `value` [BOOL] - New value

#### Conditional check [TEXT, NUMBER, BOOL]

Methods accept parameters and return a new value

```kotlin
if (condition, ifTrue, ifFalse)
```

- `condition` [BOOL] - condition expression
- `ifTrue` [TEXT, NUMBER, BOOL] - returning value if `condition` is true
- `ifFalse` [TEXT, NUMBER, BOOL] - returning value if `condition` is false

#### Current date [TEXT]

Current formatted date

```kotlin
currentDate(format)
```

- `format` [TEXT] - Formatting pattern

#### Format timestamp [TEXT]

Format timestamp using specified pattern. Timestamp is converted to system time zone.

```kotlin
formatTimestamp(timestamp, format)
```

- `timestamp` [TEXT] - timestamp in seconds
- `format` [TEXT] - Formatting pattern

#### Current date in time zone [TEXT]

Current formatted date in a specified time zone

```kotlin
currentDateWitTimeZone(format, timeZoneId)
```

- `format` [TEXT] - Formatting pattern
- `timeZoneId` [TEXT] - ID of a time zone

#### Dynamic color [TEXT]

Extract a HEX value for a color from dynamic color scheme generated by system

```kotlin
dynamicColor(colorName)
```

- `colorName` [TEXT] - Color token, case-insensitive. Allowed values: PRIMARY, ON_PRIMARY, PRIMARY_CONTAINER, ON_PRIMARY_CONTAINER, INVERSE_PRIMARY, SECONDARY, ON_SECONDARY, SECONDARY_CONTAINER, ON_SECONDARY_CONTAINER, TERTIARY, ON_TERTIARY, TERTIARY_CONTAINER, ON_TERTIARY_CONTAINER, BACKGROUND, ON_BACKGROUND, SURFACE, ON_SURFACE, SURFACE_VARIANT, ON_SURFACE_VARIANT, SURFACE_TINT, INVERSE_SURFACE, INVERSE_ON_SURFACE, ERROR, ON_ERROR, ERROR_CONTAINER, ON_ERROR_CONTAINER, OUTLINE, OUTLINE_VARIANT, SCRIM, SURFACE_BRIGHT, SURFACE_DIM, SURFACE_CONTAINER, SURFACE_CONTAINER_HIGH, SURFACE_CONTAINER_HIGHEST, SURFACE_CONTAINER_LOW, SURFACE_CONTAINER_LOWEST, PRIMARY_FIXED, PRIMARY_FIXED_DIM, ON_PRIMARY_FIXED, ON_PRIMARY_FIXED_VARIANT, SECONDARY_FIXED, SECONDARY_FIXED_DIM, ON_SECONDARY_FIXED, ON_SECONDARY_FIXED_VARIANT, TERTIARY_FIXED, TERTIARY_FIXED_DIM, ON_TERTIARY_FIXED, ON_TERTIARY_FIXED_VARIANT

#### Color scheme from image [TEXT]

Extract a HEX value for a color from color scheme generated from image

```kotlin
colorScheme(colorName, source)
```

- `colorName` [TEXT] - Color token, case-insensitive. Allowed values: PRIMARY, ON_PRIMARY, PRIMARY_CONTAINER, ON_PRIMARY_CONTAINER, INVERSE_PRIMARY, SECONDARY, ON_SECONDARY, SECONDARY_CONTAINER, ON_SECONDARY_CONTAINER, TERTIARY, ON_TERTIARY, TERTIARY_CONTAINER, ON_TERTIARY_CONTAINER, BACKGROUND, ON_BACKGROUND, SURFACE, ON_SURFACE, SURFACE_VARIANT, ON_SURFACE_VARIANT, SURFACE_TINT, INVERSE_SURFACE, INVERSE_ON_SURFACE, ERROR, ON_ERROR, ERROR_CONTAINER, ON_ERROR_CONTAINER, OUTLINE, OUTLINE_VARIANT, SCRIM, SURFACE_BRIGHT, SURFACE_DIM, SURFACE_CONTAINER, SURFACE_CONTAINER_HIGH, SURFACE_CONTAINER_HIGHEST, SURFACE_CONTAINER_LOW, SURFACE_CONTAINER_LOWEST, PRIMARY_FIXED, PRIMARY_FIXED_DIM, ON_PRIMARY_FIXED, ON_PRIMARY_FIXED_VARIANT, SECONDARY_FIXED, SECONDARY_FIXED_DIM, ON_SECONDARY_FIXED, ON_SECONDARY_FIXED_VARIANT, TERTIARY_FIXED, TERTIARY_FIXED_DIM, ON_TERTIARY_FIXED, ON_TERTIARY_FIXED_VARIANT
- `source` [TEXT] - URI to image. Can be local (file://) or network (https://, http://) link

### Templates

#### Text based on battery status [TEXT]

```kotlin
if (batteryStatus == "CHARGING", "Phone is charging", "Phone is not charging")
```

#### Battery charge percentage [TEXT]

```kotlin
batteryLevel"%"
```

#### Text based battery charge [TEXT]

```kotlin
if (batteryLevel == 100, "Battery is full", if(batteryLevel > 30, "Battery is ok", "Battery is low"))
```

#### Current time [TEXT]

```kotlin
currentDate("HH:mm")
```

#### Time of device battery charge or discharge [TEXT]

```kotlin
formatTimestamp(currentTimestamp + batteryFullEmpty, "HH:mm")
```

#### Time in UTC [TEXT]

```kotlin
currentDateWithTimeZone("HH:mm", "UTC")
```

#### Primary dynamic color [TEXT]

```kotlin
m3Color("PRIMARY")
```

#### Outline dynamic color [TEXT]

```kotlin
m3Color("OUTLINE")
```

