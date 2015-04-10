OPFUtils
========

Common utils used among OPF libraries. Intended for the internal use.

* Add snapshot repository
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
}
```

* Add the release dependency:
```groovy
compile 'org.onepf:opfutils:0.1.20'
```

or the latest snapshot dependency:
```groovy
compile 'org.onepf:opfutils:0.1.20-SNAPSHOT'
```
