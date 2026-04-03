package io.github.sadellie.sukko.core.remote

import org.koin.dsl.module

val remoteModule = module { single<RemoteClient> { RemoteClient() } }
