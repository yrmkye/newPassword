pluginManagement {
    repositories {
        maven { url = uri("D:\\Programming\\Maven-repository")}
        maven { url=uri ("https://repo.huaweicloud.com/repository/maven/") }
        maven { url=uri ("https://mirrors.huaweicloud.com/gradle/") }
        maven { url=uri ("https://maven.aliyun.com/repository/central") }
        maven { url=uri ("https://maven.aliyun.com/repository/public") }
        maven { url=uri ("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url=uri ("https://maven.aliyun.com/repository/apache-snapshots") }
        maven { url=uri ("https://repo.maven.apache.org/maven2/") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
//        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("D:\\Programming\\Maven-repository")}
        maven { url=uri ("https://repo.huaweicloud.com/repository/maven/") }
        maven { url=uri ("https://mirrors.huaweicloud.com/gradle/") }
        maven { url=uri ("https://maven.aliyun.com/repository/central") }
        maven { url=uri ("https://maven.aliyun.com/repository/public") }
        maven { url=uri ("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url=uri ("https://maven.aliyun.com/repository/apache-snapshots") }
        maven { url=uri ("https://repo.maven.apache.org/maven2/") }
//        google {
        google()
//        mavenCentral()
    }
}

rootProject.name = "NewPassword"
include(":app")
