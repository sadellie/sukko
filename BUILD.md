# Icon packs

Icon packs are not included in VC. Manually download icons and use the script below to copy them into the app.

```bash
gradlew generateIconPackFiles -Pdirectory=/absolute/path -Poutput=/absolute/path
```

Valid values may change in the future.

- `size` - optical size in px. Only existing sizes, check source folder
- `style` - `outlined`, `rounded` or `sharp`
- `directory` - path to folder with SVGs. Absolute path preferred
- `output` - path for generated icon pack files. Absolute path preferred

# Useful commands

Regenerate resources without building entire project

```bash
gradlew generateResourceAccessorsForCommonMain
```

Run common unit tests

```bash
gradlew testDebugUnitTest
# or specify
gradlew testDebugUnitTest --tests "package.name.here.*"
```

# Code quality and formatting

- `ktfmt` with Google style
- `ktlint` with tasks declared in this project
