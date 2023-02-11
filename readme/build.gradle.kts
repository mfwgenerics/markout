import org.gradle.deployment.internal.Deployment
import org.gradle.deployment.internal.DeploymentHandle
import org.gradle.deployment.internal.DeploymentRegistry

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
    nodeProjectDir.set(File("$rootDir/docusaurus"))
}

tasks.getByName("yarn_start") {
    dependsOn("yarn_install")
    dependsOn("markout")
}

class Handle(
    private val onStop: () -> Unit,
): DeploymentHandle {
    var stopped: Boolean = false

    /*Handle(NodeExecRunner runner, List<String> args, Runnable onStop) {
        Thread.start { run(runner, args) }

        // Gradle won't shut down deployments on SIGINT
        // Under some circumstances, the child process could detach
        Runtime.runtime.addShutdownHook {
            onStop.run()
        }
    }*/

    override fun start(deployment: Deployment) {
        error("yeet")
    }

    override fun isRunning(): Boolean = !stopped

    override fun stop() {
        onStop()
        stopped = true
    }
}

open class ContinuousTask: DefaultTask() {
    @TaskAction
    fun start() {
        if (project.gradle.startParameter.isContinuous) {
            val deploymentRegistry = services.get(DeploymentRegistry::class.java)

            val deploymentHandle = deploymentRegistry.get(path, Handle::class.java)

            if (deploymentHandle == null) {
                deploymentRegistry.start(
                    path,
                    DeploymentRegistry.ChangeBehavior.NONE,
                    Handle::class.java
                )
            }
        } else {
            error("should run in continuous")
        }
    }
}

tasks.register<ContinuousTask>("installDocusaurus") {
    dependsOn("markout")
    dependsOn("yarn_start")
}