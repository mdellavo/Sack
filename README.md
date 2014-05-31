Sack
====

A simple JSON Data Store for Android. Under the hood, Sack uses 
[GSON](https://sites.google.com/site/gson/) and 
[AtomicFile](https://developer.android.com/reference/android/support/v4/util/AtomicFile.html) to store 
blobs of JSON.  Sack supports both asynchronous and synchronous usage via AsyncTask and AsyncTask.get();

Example
=======
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
    
    
