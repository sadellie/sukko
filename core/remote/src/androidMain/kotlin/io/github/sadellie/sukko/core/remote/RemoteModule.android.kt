package io.github.sadellie.sukko.core.remote

import org.koin.dsl.lazyModule

val remoteModule = lazyModule { single<RemoteClient> { RemoteClient() } }
