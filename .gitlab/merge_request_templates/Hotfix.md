# Information for Requester

## Detail

Hotfix branch is used to quickly patch production release. It branches off of Master branch.

# Requester checklist (to be checked by requester)

- [ ] 1. Hotfix Detail
  - 1.1 Branches off of Master
  - 1.2 Update CHANGELOG.md
  - 1.3 Update patch number tag
- [ ] 2. Branch Management
  - 2.1 Do not squash or rebase
  - 2.2 Merge to Master before Develop
  - 2.3 Do not delete branch when merge to Master
- [ ] 3. Tag after merging to Master (Can be checked after this MR is done)
  - 3.1 Tag Master branch with the same hotfix tag number

# Information for Reviewer

## What is this hotfix for

(Describe what the issue is)

## What is the solution/hotfix?

(Describe at a high level how the issue was fixed)

## What areas of the application does it impact?

(Describe what areas of the applications are the impacted the most by this change)

## How did you test the feature?

(Describe what tests you wrote/changed if there's anything the reviewer should know when running the tests)

## Other notes

(Anything else)

# Review checklist (to be checked by reviewer)

- [ ] 1. Code Design
  - 1.1 Do the interactions of various pieces of code make sense?
  - 1.2 Does it integrate well with the rest of your system?
  - 1.3 Can you see any performance issues with the new changes?
  - 1.4 Is now a good time to add this functionality?
- [ ] 2. Tests
  - 2.1 Is the test coverage adequate? (80%+)
  - 2.2 Are the tests testing the right thing?
  - 2.3 Do all unit tests pass?
  - 2.4 Do all integration tests pass?
  - 2.5 Do all manual build jobs on gitlab CI pass?
- [ ] 3. Readability
  - 3.1 Is the code change reasonably understandable by a human with little experience or knowledge of the codebase?
  - 3.2 Has code formatting been applied?
- [ ] 4. Documentation & Logs
  - 4.1 Do in-code comments exist and describe the intent of the code?
  - 4.2 Do all methods, functions and classes have docstrings/explanations?
  - 4.3 Is there an appropriate amount of logging and do log levels make sense?
  - 4.4 If applicable, has the README and/or Wiki been updated to reflect the new changes?
- [ ] 5. Security & Privacy
  - 5.1 Is there no sensitive data being logged or stored anywhere else? If there is, is it justified?
  - 5.2 If applicable, is client input validated?
- [ ] 6. Hotfix Branch Management
  - 6.1 Is this hotfix branched off of Master branch?
  - 6.2 Is tag number correct?
  - 6.3 Is code version in project files correct?
