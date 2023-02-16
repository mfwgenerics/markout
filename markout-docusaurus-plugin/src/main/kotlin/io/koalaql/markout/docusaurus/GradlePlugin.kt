package io.koalaql.markout.docusaurus

import com.github.gradle.node.NodeExtension
import com.github.gradle.node.exec.NodeExecConfiguration
import com.github.gradle.node.util.PlatformHelper
import com.github.gradle.node.util.ProjectApiHelper
import com.github.gradle.node.variant.VariantComputer
import com.github.gradle.node.yarn.exec.YarnExecRunner
import org.gradle.api.Plugin
import org.gradle.api.Project

import com.github.gradle.node.yarn.task.YarnTask
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.deployment.internal.Deployment
import org.gradle.deployment.internal.DeploymentHandle
import org.gradle.deployment.internal.DeploymentRegistry
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.concurrent.thread

class StopHandle {
    private companion object {
        val uninitMarker = { }
        val stoppedMarker = { }
    }

    private val onStop = AtomicReference(uninitMarker)

    val isStopped = onStop.get() === stoppedMarker

    fun onStop(stop: () -> Unit) {
        val calledYet = AtomicBoolean(false)

        val runsOnce = {
            if (!calledYet.getAndSet(true)) stop()
        }

        val marker = onStop.getAndSet(runsOnce)

        if (marker === stoppedMarker) {
            runsOnce()
        } else check (marker === uninitMarker) {
            "internal error: onStop was called twice"
        }
    }

    fun stop() {
        onStop.getAndSet(stoppedMarker).invoke()
    }
}

fun interface Spawn {
    operator fun invoke(dispose: StopHandle)
}

open class DocusaurusHandle @Inject constructor (
    private val spawn: Spawn
): DeploymentHandle {
    private val handle = StopHandle()

    override fun start(deployment: Deployment) { spawn(handle) }
    override fun isRunning(): Boolean = !handle.isStopped
    override fun stop() { handle.stop() }
}

abstract class RunDocusaurus: DefaultTask() {
    @get:Inject
    abstract val objects: ObjectFactory

    @get:Inject
    abstract val providers: ProviderFactory

    @get:Optional
    @get:Input
    val yarnCommand = objects.listProperty(String::class.java)

    @get:Optional
    @get:Input
    val args = objects.listProperty(String::class.java)

    @get:Input
    val ignoreExitValue = objects.property(Boolean::class.java).convention(false)

    @get:Internal
    val workingDir = objects.directoryProperty()

    @get:Input
    val environment = objects.mapProperty(String::class.java, String::class.java)

    @get:Internal @Suppress("unchecked_cast")
    val execOverrides = objects.property(
        Action::class.java as Class<Action<ExecSpec>>
    )

    @get:Internal
    val projectHelper = ProjectApiHelper.newInstance(project)

    @get:Internal
    val nodeExtension = NodeExtension[project]

    @get:Internal
    var platformHelper = PlatformHelper.INSTANCE

    @get:Internal
    internal val variantComputer by lazy {
        VariantComputer(platformHelper)
    }

    private fun buildYarnStart(): () -> ExecResult {
        val os = DefaultNativePlatform
            .getCurrentOperatingSystem()

        val args = if (os.isLinux) {
            listOf("--non-interactive", "--use-yarnrc=linux.yarnrc", "--silent", "start")
        } else {
            listOf("--non-interactive", "--silent", "start")
        }

        val nodeExecConfiguration = NodeExecConfiguration(
            args,
            environment.get(),
            workingDir.asFile.orNull,
            ignoreExitValue.get(),
            execOverrides.orNull
        )

        val yarnExecRunner = objects.newInstance(YarnExecRunner::class.java)

        return {
            yarnExecRunner.executeYarnCommand(
                projectHelper,
                nodeExtension,
                nodeExecConfiguration,
                variantComputer
            )
        }
    }

    @TaskAction
    fun start() {
        if (project.gradle.startParameter.isContinuous) {
            val deploymentRegistry = services.get(DeploymentRegistry::class.java)

            val deploymentHandle = deploymentRegistry.get(path, DocusaurusHandle::class.java)

            if (deploymentHandle == null) {
                val start = buildYarnStart()

                deploymentRegistry.start(
                    path,
                    DeploymentRegistry.ChangeBehavior.NONE,
                    DocusaurusHandle::class.java,
                    Spawn {
                        val thread = thread { start() }

                        it.onStop {
                            thread.interrupt()
                        }
                    }
                )
            }
        } else {
            project.logger.warn("""
                WARNING: $path was run non-continuously and will not hot reload changes
                WARNING: You probably want to run this with `./gradlew $path --continuous` instead 
            """.trimIndent())

            val start = buildYarnStart()

            start()
        }
    }
}

class GradlePlugin: Plugin<Project> {
    override fun apply(target: Project) = with (target) {
        plugins.apply("com.github.node-gradle.node")

        extensions.configure(NodeExtension::class.java) {
            it.download.set(true)
            it.nodeProjectDir.set(File("$rootDir/docusaurus"))
        }

        tasks.register("docusaurusInstall", YarnTask::class.java) {
            it.dependsOn("markout")

            it.args.set(listOf(
                "--silent",
                "--non-interactive",
                "install",
                "--frozen-lockfile",
            ))
        }

        tasks.register("docusaurusCheckInstall", YarnTask::class.java) {
            it.dependsOn("markoutCheck")

            it.args.set(listOf(
                "--silent",
                "--non-interactive",
                "install",
                "--frozen-lockfile",
            ))
        }

        tasks.register<RunDocusaurus>("docusaurusStart", RunDocusaurus::class.java) {
            it.dependsOn("docusaurusInstall")
            it.dependsOn("markout")

            it.doLast {
                gradle.startParameter.setExcludedTaskNames(
                    mutableSetOf("docusaurusInstall").apply { addAll(gradle.startParameter.excludedTaskNames) }
                )
            }
        }

        tasks.register("docusaurusBuild", YarnTask::class.java) {
            it.dependsOn("markoutCheck")
            it.dependsOn("docusaurusCheckInstall")

            it.args.set(listOf(
                "--silent",
                "--non-interactive",
                "build",
            ))
        }

        Unit
    }
}