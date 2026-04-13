# Architecture

This page contains information about app architecture.

## Module ~~spaghetti~~ Graph

```mermaid
%%{
  init: {
    'theme': 'dark'
  }
}%%

graph LR
  subgraph :core
    :core:database["database"]
    :core:common["common"]
    :core:data["data"]
    :core:designsystem["designsystem"]
    :core:fontfiles["fontfiles"]
    :core:iconfiles["iconfiles"]
    :core:importexport["importexport"]
    :core:routes["routes"]
    :core:ui["ui"]
    :core:unglance["unglance"]
    :core:routes-ui["routes-ui"]
    :core:remote["remote"]
  end
  subgraph :feature
    :feature:importpreset["importpreset"]
    :feature:editor["editor"]
    :feature:widget["widget"]
    :feature:presetselector["presetselector"]
    :feature:settings["settings"]
    :feature:widgetinfo["widgetinfo"]
    :feature:iconpackeditor["iconpackeditor"]
    :feature:fontseditor["fontseditor"]
    :feature:home["home"]
    :feature:saveaspreset["saveaspreset"]
  end
  :core:database --> :core:common
  :feature:importpreset --> :core:common
  :feature:importpreset --> :core:data
  :feature:importpreset --> :core:designsystem
  :feature:importpreset --> :core:fontfiles
  :feature:importpreset --> :core:iconfiles
  :feature:importpreset --> :core:importexport
  :feature:importpreset --> :core:routes
  :feature:importpreset --> :core:ui
  :feature:importpreset --> :material-symbols
  :feature:editor --> :core:common
  :feature:editor --> :core:data
  :feature:editor --> :core:designsystem
  :feature:editor --> :core:fontfiles
  :feature:editor --> :core:iconfiles
  :feature:editor --> :core:routes
  :feature:editor --> :core:ui
  :feature:editor --> :feature:widget
  :feature:editor --> :material-symbols
  :feature:presetselector --> :core:common
  :feature:presetselector --> :core:data
  :feature:presetselector --> :core:designsystem
  :feature:presetselector --> :core:routes
  :feature:presetselector --> :core:ui
  :feature:presetselector --> :material-symbols
  :core:designsystem --> :themmo
  :core:unglance --> :core:common
  :core:unglance --> :core:designsystem
  :core:unglance --> :core:data
  :feature:settings --> :core:common
  :feature:settings --> :core:data
  :feature:settings --> :core:designsystem
  :feature:settings --> :core:iconfiles
  :feature:settings --> :core:routes
  :feature:settings --> :core:ui
  :feature:settings --> :material-symbols
  :core:fontfiles --> :core:common
  :core:fontfiles --> :core:designsystem
  :core:fontfiles --> :core:ui
  :feature:widget --> :core:common
  :feature:widget --> :core:data
  :feature:widget --> :core:unglance
  :feature:widget --> :core:routes
  :feature:widget --> :core:fontfiles
  :feature:widget --> :core:routes-ui
  :feature:widgetinfo --> :core:common
  :feature:widgetinfo --> :core:data
  :feature:widgetinfo --> :core:designsystem
  :feature:widgetinfo --> :core:routes
  :feature:widgetinfo --> :core:ui
  :feature:widgetinfo --> :material-symbols
  :feature:iconpackeditor --> :core:common
  :feature:iconpackeditor --> :core:data
  :feature:iconpackeditor --> :core:designsystem
  :feature:iconpackeditor --> :core:iconfiles
  :feature:iconpackeditor --> :core:routes
  :feature:iconpackeditor --> :core:ui
  :feature:iconpackeditor --> :material-symbols
  :androidApp --> :app
  :androidApp --> :feature:widget
  :app --> :core:common
  :app --> :core:data
  :app --> :core:database
  :app --> :core:designsystem
  :app --> :core:fontfiles
  :app --> :core:importexport
  :app --> :core:routes
  :app --> :core:routes-ui
  :app --> :core:ui
  :app --> :feature:editor
  :app --> :feature:fontseditor
  :app --> :feature:home
  :app --> :feature:iconpackeditor
  :app --> :feature:importpreset
  :app --> :feature:presetselector
  :app --> :feature:saveaspreset
  :app --> :feature:settings
  :app --> :feature:widget
  :app --> :feature:widgetinfo
  :core:routes-ui --> :core:designsystem
  :core:routes-ui --> :core:routes
  :core:routes-ui --> :themmo
  :core:iconfiles --> :core:common
  :core:iconfiles --> :core:designsystem
  :core:iconfiles --> :core:ui
  :core:iconfiles --> :material-symbols
  :feature:home --> :core:importexport
  :feature:home --> :core:common
  :feature:home --> :core:data
  :feature:home --> :core:designsystem
  :feature:home --> :core:fontfiles
  :feature:home --> :core:routes
  :feature:home --> :core:ui
  :feature:home --> :feature:widget
  :feature:home --> :material-symbols
  :core:importexport --> :core:common
  :core:importexport --> :core:data
  :core:importexport --> :core:fontfiles
  :core:importexport --> :core:iconfiles
  :core:importexport --> :core:database
  :core:data --> :core:database
  :core:data --> :core:common
  :core:data --> :core:designsystem
  :core:data --> :core:fontfiles
  :core:data --> :core:iconfiles
  :core:data --> :core:remote
  :core:data --> :material-symbols
  :core:ui --> :core:common
  :core:ui --> :core:designsystem
  :core:ui --> :material-symbols
  :feature:fontseditor --> :core:common
  :feature:fontseditor --> :core:data
  :feature:fontseditor --> :core:designsystem
  :feature:fontseditor --> :core:fontfiles
  :feature:fontseditor --> :core:routes
  :feature:fontseditor --> :core:ui
  :feature:fontseditor --> :material-symbols
  :feature:saveaspreset --> :core:common
  :feature:saveaspreset --> :core:data
  :feature:saveaspreset --> :core:designsystem
  :feature:saveaspreset --> :core:routes
  :feature:saveaspreset --> :core:ui
  :feature:saveaspreset --> :material-symbols
```