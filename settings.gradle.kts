pluginManagement {
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven {
            url = uri ("https://jitpack.io/")
            isAllowInsecureProtocol = true
        }
        maven{
            url = uri ("https://jcenter.bintray.com/")
        }
    }
}

rootProject.name = "App RT"
include(":app")
 