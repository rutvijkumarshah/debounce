# Debounce

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/rutvijkumarshah/debounce.svg?branch=master)](https://travis-ci.org/rutvijkumarshah/debounce)
[![codecov](https://codecov.io/gh/rutvijkumarshah/debounce/branch/master/graph/badge.svg)](https://codecov.io/gh/rutvijkumarshah/debounce)

Download
--------
Add jitpack repo in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
 Add the debouce dependency:
```groovy
dependencies {
    implementation 'com.github.rutvijkumarshah:debounce:1.0-beta'
}
```

Why
---
Many times we need a behavior that should only happen after a repeated action has completed.

For example, when making network call based on user type in searchbox , it may reduce number of network call.
But if delayed a bit little, before making network call in case user may type few other keywords.

This is a tiny library with andorid specific implementation to achive "debouce" effect.

If you are alreay using Rx-Java it has debouce operator for similar effect in this case this library is not useful.

How
---

```java
debouncer = Debouncer.getInstance();
 ...
 public void onClick(View view) {
    debouncer.debounce("logClicked",() -> Log.d(TAG,"OnClick"));
 }

```
Debouncer uses default delay time to achieve debounce behavior.
You can set default delay time by calling `Debouncer.setDefaultDelayTime(defaultDelayTime)`  before calling `getInstance()`

Deboucer by default uses Main thread to call-back passed runnable, but debouce method can take
optional `Handler` to specify as target thread to execute runnable.

```java
debouncer.debounce("logClicked",() -> Log.d(TAG,"OnClick"),handler);
```

Deboucer can clear delayed taks by calling `debouncer.clear("logClicked");` or clearing all taks by calling `debouncer.clearAll()`



License
-------

    Copyright 2018 Rutvijkumar Shah

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

