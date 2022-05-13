package com.soywiz.korge.gradle.util

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.ByteArrayOutputStream

fun Project.debugExecSpec(exec: ExecSpec) {
	logger.info("COMMAND: ${exec.commandLine.joinToString(" ")}")
}

fun Project.execLogger(action: (ExecSpec) -> Unit): ExecResult {
	return exec {
		action(this)
		debugExecSpec(this)
	}
}

fun ExecSpec.commandLineCompat(vararg args: String): ExecSpec {
	return when {
		Os.isFamily(Os.FAMILY_WINDOWS) -> commandLine("cmd", "/c", *args)
		else -> commandLine(*args)
	}
}

fun Project.execOutput(vararg cmds: String, log: Boolean = true): String {
	val stdout = ByteArrayOutputStream()
	exec {
		commandLineCompat(*cmds)
		standardOutput = stdout
		if (log) {
			debugExecSpec(this)
		}
	}
	return stdout.toString("UTF-8")
}

