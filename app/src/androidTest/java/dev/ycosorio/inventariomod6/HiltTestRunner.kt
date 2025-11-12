package dev.ycosorio.inventariomod6

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Un Test Runner personalizado para configurar Hilt en los tests de instrumentación.
 */
class HiltTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // Le decimos a Hilt que use 'HiltTestApplication'
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}