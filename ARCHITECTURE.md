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
    :core:unglance["unglance"]
    :core:common["common"]
    :core:designsystem["designsystem"]
    :core:model["model"]
    :core:data["data"]
    :core:fontfiles["fontfiles"]
    :core:iconfiles["iconfiles"]
    :core:importexport["importexport"]
    :core:ui["ui"]
    :core:database["database"]
    :core:medialistener["medialistener"]
    :core:script["script"]
    :core:widget["widget"]
    :core:remote["remote"]
    :core:routes["routes"]
  end
  subgraph :feature
    :feature:importpreset["importpreset"]
    :feature:editor["editor"]
    :feature:icopackeditor["icopackeditor"]
    :feature:presetselector["presetselector"]
    :feature:settings["settings"]
    :feature:fontseditor["fontseditor"]
    :feature:home["home"]
    :feature:saveaspreset["saveaspreset"]
  end
  :core:unglance --> :core:common
  :core:unglance --> :core:designsystem
  :core:unglance --> :core:model
  :feature:importpreset --> :core:common
  :feature:importpreset --> :core:data
  :feature:importpreset --> :core:designsystem
  :feature:importpreset --> :core:fontfiles
  :feature:importpreset --> :core:iconfiles
  :feature:importpreset --> :core:importexport
  :feature:importpreset --> :core:model
  :feature:importpreset --> :core:ui
  :feature:importpreset --> :material-symbols
  :core:database --> :core:common
  :feature:editor --> :core:common
  :feature:editor --> :core:data
  :feature:editor --> :core:designsystem
  :feature:editor --> :core:fontfiles
  :feature:editor --> :core:iconfiles
  :feature:editor --> :core:medialistener
  :feature:editor --> :core:model
  :feature:editor --> :core:script
  :feature:editor --> :core:ui
  :feature:editor --> :core:widget
  :feature:editor --> :material-symbols
  :feature:icopackeditor --> :core:common
  :feature:icopackeditor --> :core:data
  :feature:icopackeditor --> :core:designsystem
  :feature:icopackeditor --> :core:iconfiles
  :feature:icopackeditor --> :core:model
  :feature:icopackeditor --> :core:ui
  :feature:icopackeditor --> :material-symbols
  :feature:presetselector --> :core:common
  :feature:presetselector --> :core:data
  :feature:presetselector --> :core:designsystem
  :feature:presetselector --> :core:model
  :feature:presetselector --> :core:ui
  :feature:presetselector --> :material-symbols
  :core:designsystem --> :themmo
  :feature:settings --> :core:common
  :feature:settings --> :core:data
  :feature:settings --> :core:designsystem
  :feature:settings --> :core:iconfiles
  :feature:settings --> :core:medialistener
  :feature:settings --> :core:model
  :feature:settings --> :core:ui
  :feature:settings --> :material-symbols
  :app --> :core:database
  :app --> :core:importexport
  :app --> :feature:fontseditor
  :app --> :feature:home
  :app --> :feature:icopackeditor
  :app --> :feature:importpreset
  :app --> :feature:settings
  :app --> :core:common
  :app --> :core:data
  :app --> :core:designsystem
  :app --> :core:fontfiles
  :app --> :core:iconfiles
  :app --> :core:medialistener
  :app --> :core:model
  :app --> :core:remote
  :app --> :core:routes
  :app --> :core:script
  :app --> :core:ui
  :app --> :core:widget
  :app --> :feature:editor
  :app --> :feature:presetselector
  :app --> :feature:saveaspreset
  :app --> :themmo
  :core:fontfiles --> :core:common
  :core:fontfiles --> :core:designsystem
  :core:fontfiles --> :core:ui
  :core:importexport --> :core:database
  :core:importexport --> :core:common
  :core:importexport --> :core:data
  :core:importexport --> :core:fontfiles
  :core:importexport --> :core:iconfiles
  :core:importexport --> :core:model
  :feature:home --> :core:common
  :feature:home --> :core:data
  :feature:home --> :core:designsystem
  :feature:home --> :core:fontfiles
  :feature:home --> :core:importexport
  :feature:home --> :core:model
  :feature:home --> :core:ui
  :feature:home --> :core:widget
  :feature:home --> :material-symbols
  :feature:fontseditor --> :core:common
  :feature:fontseditor --> :core:data
  :feature:fontseditor --> :core:designsystem
  :feature:fontseditor --> :core:fontfiles
  :feature:fontseditor --> :core:model
  :feature:fontseditor --> :core:ui
  :feature:fontseditor --> :material-symbols
  :core:model --> :core:common
  :core:model --> :core:designsystem
  :core:model --> :core:fontfiles
  :core:model --> :core:iconfiles
  :core:model --> :core:script
  :core:model --> :material-symbols
  :core:iconfiles --> :core:common
  :core:iconfiles --> :core:designsystem
  :core:iconfiles --> :core:ui
  :core:iconfiles --> :material-symbols
  :core:data --> :core:database
  :core:data --> :core:common
  :core:data --> :core:fontfiles
  :core:data --> :core:iconfiles
  :core:data --> :core:medialistener
  :core:data --> :core:model
  :core:data --> :core:remote
  :core:data --> :core:script
  :core:ui --> :core:common
  :core:ui --> :core:designsystem
  :core:ui --> :material-symbols
  :core:medialistener --> :core:common
  :core:medialistener --> :core:model
  :feature:saveaspreset --> :core:common
  :feature:saveaspreset --> :core:data
  :feature:saveaspreset --> :core:designsystem
  :feature:saveaspreset --> :core:model
  :feature:saveaspreset --> :core:ui
  :feature:saveaspreset --> :material-symbols
  :core:widget --> :core:common
  :core:widget --> :core:data
  :core:widget --> :core:medialistener
  :core:widget --> :core:model
  :core:widget --> :core:unglance
```