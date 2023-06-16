# Gitlet Design Document

**Name**: Andy Dinh, Luke Li, Isaac Lucero

## Classes and Data Structures

### Main

#### Fields

1. `static final File GITLET_FOLDER = Repository.GITLET_DIR`
   Main metadata folder, from `Repository` class.
2. `static boolean initialized = GITLET_FOLDER.exists()`
   Flag to check if repository has been initialized before allowing other commands.

### Command

#### Fields

Contains all commands referenced by `Main` class.

1. `static final File CWD = Repository.CWD`
   Current Working Directory, from `Repository` class.
2. `static final File GITLET_FOLDER = Repository.GITLET_DIR`
   Main metadata folder, from `Repository` class.
3. `private static final Repository REPO = readRepo()`
   Repository file, read from file.
4. `private static final HashMap<String, String> STAGED = readMap()`
   Mapping of staged files and operations, read from file.
   

### Commit

#### Fields

1. `private final String message`
   The message of this commit.
2. `private Date date`
   Time at which commit was created as `Date` object for `merge` comparison.
3. `private final String timeFormat = "Date: %1$ta %1$tb %1$te %1$tT %1$tY %1$tz"`
   Format for printing to log.
4. `private final String timestamp`
   Time at which commit was created.
5. `private final Commit parent`
   Points to parent commit, init commit has no parent.
6. `private Commit secondParent`
   Second parent for merges.
7. `private final HashMap<String, File> blobNames`
   Mapping of file names to blob references.
8. `private String hash`
   Unique SHA-1 hash for this commit.


### Repository

#### Fields

1. `public static final File CWD = new File(System.getProperty("user.dir"))`
   Current working directory, contains files that `gitlet` will manage.
2. `public static final File GITLET_DIR = Utils.join(CWD, ".gitlet")`
   Main **.gitlet** directory.
   Contains useful persistence files and all subdirectories.
3. `public static final File STAGING = Utils.join(GITLET_DIR, "staging")`
   Staging directory contains temporary staged files.
4. `public static final File BLOBS = Utils.join(GITLET_DIR, "blobs")`
   Blobs directory contains all files tracked by all commits.
   Files are only added to this directory.
5. `public static final File COMMITS = Utils.join(GITLET_DIR, "commits") `
   Commits directory contains commit files.
   Files are only added to this directory.
7. `private static HashMap<String, File> heads`
   Mapping between branch head names and references to commit files.
8. `private static final File HEADS = Utils.join(GITLET_DIR, "HEADS")`
   Directory of file where branch mapping is saved.
9. `private String currBranch = "master"`
   Name of the current branch of the repository.
   

## Algorithms

All file operations used were from the provided `Utils` class.

1. `add/rm`
   Both operations wrote files and operations performed on them into a mapping, which was saved
   as **STAGED**.
2. `commit`
   Retrieves and performs staged operations from the **STAGED** file.
   Saves commit to file and resets **STAGED** mapping after operations completed.
   `merge` flag is false except when called upon by `merge`.
   Second parent of commit is null except when explicitly given by `merge`.
3. `log/global-log`
   - `Log`: Prints commit and recursively calls print on parent until reaching the initial commit.
   - `Global-log`: Reads commits directly from the **commits** subdirectory and prints them.
4. `find`
   Reads commits from the **commits** subdirectory and accesses their message field directly.
   Checks if message is found by maintaining and updating a boolean flag `found` that is set to `true`
   when at least one commit with the given message is found.
5. `status`
   For each section, retrieves filenames from their respective mappings, calls helper method
   `sorted` to sort items, and prints them.
   - `sorted` copies content of given collection into new list, sorts the list, and returns it.
6. `checkout`
   - `Item`: Special case of `Commit -- Item` where the commit is the head of the current branch.
   - `Commit -- Item`: Accesses given commit's blob mapping to retrieve files.
     Used for file operations for other `checkout` methods and `reset`.
   - `Branch`: Uses helper method `untrackedHelper` to handle untracked files. 
     File operations handled by `checkout Commit -- Item`.
     - `untrackedHelper`: Private helper method. 
       Takes in current and new commits as arguments. 
       Handles untracked files as described in spec.
7. `rm-branch`: Removes specified branch from repository's mapping.
8. `reset`
   Special case of `checkout Branch` where a commit hash ID is given instead of a branch name.
   Uses same helper methods.
9. `merge`: Implemented via various helper methods:
   - `mergeError`: Basic error checking, implemented similarly to `untrackedHelper` for untracked files.
   - `splitFind`: Finds split point between current and given branches recursively.
   - `mergeHelper`: Calls other helper methods.
   - `mergeConflict`: Reads file blob from current and given branch heads, writes to file, stages it,
   and returns a `boolean` to indicate if a merge conflict occurred.

Each class also contains various trivial methods used to access or modify private fields.


## Persistence

File Tree:

    CWD                 <==== Current Working Directory, where .gitlet is contained
    |-- .gitlet
        |-- blobs       <==== All hashed file blobs are stored in the directory
            |-- 02c725bf18b6c56590f49e0c229ec877cbcc6a7e
            |-- 749914fd3452fc10d36ed33cbbd8be1ab72b71a6
            |-- ...
        |-- commits     <==== All hashed commit files are stored in the directory
            |-- 7fd0c60790602276b351d77e6ec25faa006ae9bf
            |-- ec0b4f0b5c90ed0fa911a2972ccc452641b31563
            |-- ...
        |-- staging     <==== All unhashed staged files are stored here
            |-- hello.txt
            |-- world.txt
            |-- ...
        |-- REPO        <==== Repository is saved in this file
        |-- HEADS       <==== Mapping between branch heads and commits are saved in this file
        |-- STAGED      <==== Mapping between staged files and operations are saved in this file
 
`Main init` sets up persistence:
1. Creates the **.gitlet** directory if it doesn't exist yet 
2. Calls on `Repository` class to set up persistence.

`Repository` class sets up persistence:
1. Creates all subdirectories
2. Saves repository to **REPO**
3. Saves branch mapping to **HEADS**
4. Creates empty mapping for staged files and saves it to **STAGED**.

`Main` class handles persistence:
1. `private static Repository readRepo()`
   Reads repository **REPO** from file.
2. `private static HashMap<String, String> readMap()`
   Reads mapping **STAGED** from file.

`Repository` class maintains persistence:
1. `private void writeRepo()`
   Saves current repository instance to **REPO**.
2. `public void updateBranch(String branch, File commit)`
   Moves current head of given branch to point to the given commit.
3. `public static void updateMap(HashMap<String, String> map)`
   Updates **STAGED** after files and operations are added.
4. `public static void initMap()`
   Special instance of updateMap with an empty map as argument.
5. `private void saveHeads()`
   Writes map to **HEADS**.
6. `private HashMap<String, File> readHead()`
   Reads saved head file and return the HashMap object.
   
`Commit` class:
1. `public File commitToFile()`
   Writes commit to file in **commits** directory with SHA1 hash of the commit object as the filename.
