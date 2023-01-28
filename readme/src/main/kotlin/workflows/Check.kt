package workflows

import yaml

val CHECK_WORKFLOW = yaml {
    "name" - "Build and check"
    "on" - "[push, pull_request]"
    "jobs" - {
        "build" - {
            "runs-on" - "ubuntu-latest"
            "steps" - {
                li { "uses" - "actions/checkout@v2" }
                li { "uses" - "gradle/wrapper-validation-action@v1" }
                li {
                    "uses" - "actions/setup-java@v1"
                    "with" - {
                        "java-version" - "19"
                    }
                }

                li { "run" - "./gradlew check" }
            }
        }
    }
}