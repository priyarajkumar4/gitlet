# Gitlet Design Document

Author: Priya Rajkumar

------

## Classes and Data Structures

### Main

> This class will simply receive gitlet commands as command line arguments. This class holds the gitlet commands.

```java
Fields: 
	Repository repo: 'command line'
Fields{ 
  Command line: 'to call a function from the repository, we need the actual command line'
  Blob b : 'the blob we are looking at for a specific call'
  File Name file: 'file worked upon'
  String m : 'holds the message that could be used to commit and store within Blob class'
  Commits c : 'to use branch and view nodes'
  Branch pointer and Head pointer: we will be able to see where we are with these pointers
}
```

### Blob

> This class will hold the values of the blob as well as the hashmap for the blob id

```java
Fields{ 
  String logMessage = 'message given with commit'
  String timestamp = 'time that commit was made'
  // mapping of file names to blob references
  Hashcode firstParent = 'hold first parent method'
  Hashcode secondParent = 'hold second parent method'
}
```



### Commits

> This class will store the commits that we have gotten from the StagingArea

```java
Tree commit = 'holding commits in a commit tree'
StagingArea lst = 'take blob to commit from StagingArea'
```



### StagingArea

> This class will hold blobs to be placed later within commit

```java
ArrayList<Blob> = 'holding arraylist of all the blobs that have not been committed but have been added'
```

## Algorithms

### Main Class:

1. main (args) : in this constructor we will be looking at the command line and create a new Repository object to call on. Lets say the repository object is called repo.  we will look at all cases and see eg. repo.add which will check if you can do it with a switch call and try that case. if its possible and within the args, we will call repo.add. we do this with all other functions within repository.
2. should also hold the head pointer since it has all the classes to change pointer

1. init

    1. creates new version control system

2. add

    1. copies file and adds to the StagingArea class

3. commit

    1. saves snapshot of tracker files in commit and staging area
    2. calls commit class
    3. clears staging area

4. rm

    1. alters tree within commit

5. log

    1. displays all information from Blob class

6. global-log

    1. goes into commit tree and displays all blobs ever

7. find

    1. prints ids of all commits through blob class

8. status

    1. displays the Commit Class tree

   ….

### Blob Class:

1. blob class just holds all the variables of the type, the hashcode, the time stamp, simple meta data



### Commits Class:

1. Commit holds a commit tree that can store all the variables given with stagingArea class
    1. takes all blobs of staging area and uploads into new area
    2. clears staging area

### StagingArea:

1. holds blobs after add is called
2. StagingArea will hold all the variables until it is committed

## Persistence

1. we can hold and not lose state of my program by ensuring the hashcode we are looking at is the same. when we modify a txtfile to make sure we are looking at the correct commit being made. we can simply call git log-global as well as use normal git to make sure that we doing everything correct
2. I can have two terminals with one reunning my gitlet and one running actual git and replicate my test files to ensure that I am always looking at the proper commit











###  