# Gitlet

A mini version control system written in Java, modeled after Git. Built for the CS 61B course at UC Berkeley.

## Features

- `init`, `add`, `commit`, `rm`, `log`, `global-log`, `find`, `status`
- Branching (`branch`, `rm-branch`, `checkout`) and merging with conflict resolution
- Persistent storage using Java serialization
- Tracks untracked, modified, and removed files

## Example Usage

```bash
java gitlet.Main init
java gitlet.Main add file.txt
java gitlet.Main commit "initial commit"
java gitlet.Main branch feature
java gitlet.Main merge feature
```

## Merge Handling

- Performs 3-way merge using the latest common ancestor (split point)
- Detects and resolves conflicts by inserting conflict markers:

  ```text
  <<<<<<< HEAD
  // content from current branch
  =======
  // content from given branch
  >>>>>>>
  ```

- Supports fast-forward merges when the current branch is behind

## Testing

- Manually tested using real-world Git workflows (adding, committing, branching, merging)
- Verified behavior by comparing results with actual `git` commands in parallel
- Edge cases covered:
  - Merge conflicts
  - Fast-forwarding branches
  - Untracked or modified files interfering with merges
