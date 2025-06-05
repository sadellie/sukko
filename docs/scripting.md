# Scripting

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

!!! warning "Full documentation is not available yet"

    Methods, constants and templates are currently available only in the app. Try opening any script and click question mark (?) icon.

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
