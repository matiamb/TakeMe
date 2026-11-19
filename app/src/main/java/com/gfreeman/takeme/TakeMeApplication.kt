package com.gfreeman.takeme

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp.
 * This triggers Hilt's code generation, creating a top-level Dependency Injection container
 * attached to the application lifecycle.
 */
@HiltAndroidApp
class TakeMeApplication : Application()
