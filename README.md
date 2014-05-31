Sack
====

A simple JSON Data Store for Android. 

Example
=======

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
    
    sack.load(new Sack.Listener<FooList>() {
      void onResult(Sack.Status status, FooList obj) {
      
      }
    }); 
    
    
    
