# repo
```gradle
build.gradle for allprojects
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
add to app build.gradle
```gradle
dependencies {
    compile 'com.github.binhbt:Vegarequest:1.0.1'
}
