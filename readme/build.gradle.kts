import com.github.gradle.node.yarn.exec.YarnExecRunner
import com.github.gradle.node.exec.NodeExecConfiguration
import org.gradle.deployment.internal.Deployment
import org.gradle.deployment.internal.DeploymentHandle
import org.gradle.deployment.internal.DeploymentRegistry
import com.github.gradle.node.NodeExtension
import com.github.gradle.node.util.ProjectApiHelper
import com.github.gradle.node.util.PlatformHelper
import com.github.gradle.node.variant.VariantComputer
import com.github.gradle.node.yarn.task.YarnTask
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

repositories {
    mavenCentral()
}

plugins {
    id("conventions")

    id("io.koalaql.markout")

    id("io.koalaql.kapshot-plugin") version "0.1.1"

    id("com.github.node-gradle.node") version "3.5.1"
}

markout {
    mainClass = "MainKt"
}

dependencies {
    implementation(project(":markout"))
    implementation(project(":markout-markdown"))
    implementation(project(":markout-docusaurus"))
    implementation(project(":markout-github-workflows-kt"))

    implementation(kotlin("reflect"))
}

node {
    download.set(true)

    nodeProjectDir.set(File("$rootDir/docusaurus"))
}

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
    val yarnCommand = objects.listProperty<String>()

    @get:Optional
    @get:Input
    val args = objects.listProperty<String>()

    @get:Input
    val ignoreExitValue = objects.property<Boolean>().convention(false)

    @get:Internal
    val workingDir = objects.directoryProperty()

    @get:Input
    val environment = objects.mapProperty<String, String>()

    @get:Internal
    val execOverrides = objects.property<Action<ExecSpec>>()

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
            listOf("--use-yarnrc=linux.yarnrc", "start")
        } else {
            listOf("start")
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
            val start = buildYarnStart()

            start()
        }
    }
}

tasks.register<RunDocusaurus>("runDocusaurus") {
    dependsOn("yarn_install")
    dependsOn("markout")
}
