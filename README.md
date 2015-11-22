Sack - A simple data store for Android
======================================

A simple JSON Data Store for Android. Under the hood, Sack uses 
[GSON](https://sites.google.com/site/gson/) and 
[AtomicFile](https://developer.android.com/reference/android/support/v4/util/AtomicFile.html) to store 
blobs of JSON.  Sack supports both asynchronous and synchronous usage via AsyncTask and AsyncTask.get();

Example
-------
````java
class Foo {
  int x,y,z
  String name
      
  Foo(String name, int x, int y, int z) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
    
class FooList extends List<Foo> {};
    
FooList foos = new FooList();
foos.add(new Foo("a", 1, 2, 3));
    
Sack<FooList> sack = Sack.open(FooList.class,  openFileOutput("foos.json", Context.MODE_PRIVATE));
sack.commit(foos, new Sack.Listener<FooList>() {
  void onResult(Sack.Status status, FooList obj) {
      
  }
});
    
Pair<Sack.Status, FooList> rv = sack.load().get();    // .get() makes the load synchronous 
````
    
Download
--------

via BinTray: https://bintray.com/mdellavo/maven/sack

via Gradle:

Add the maven respository:
```gradle
allprojects {
    repositories {
        jcenter()

        maven {
            url  "http://dl.bintray.com/mdellavo/maven"
        }
    }
}
```

Add the dependency
```gradle
dependencies {
    compile 'org.quuux.sack:sack:0.1'
}

```

License
-------

    Copyright 2015 Marc DellaVolpe

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.