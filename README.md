OPFUtils
========

Common utils used among OPF libraries.

* Add snapshot repository
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
}
```

* Add dependency
```groovy
compile 'org.onepf:opfutils:0.1.1-SNAPSHOT'
```
