package io.github.sadellie.sukko.core.data

import io.github.sadellie.sukko.core.model.InstalledApp

interface InstalledAppsProvider {
  suspend fun getAllApps(): List<InstalledApp>
}
