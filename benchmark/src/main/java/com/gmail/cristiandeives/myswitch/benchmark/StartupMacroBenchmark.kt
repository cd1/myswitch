package com.gmail.cristiandeives.myswitch.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartupMacroBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.gmail.cristiandeives.myswitch",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilationMode,
        startupMode = StartupMode.COLD,
        iterations = 5,
        setupBlock = {
            pressHome()
        },
    ) {
        startActivityAndWait()
    }

    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())

    @Test
    fun startupPartialCompilation() = startup(CompilationMode.Partial())

    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())
}
