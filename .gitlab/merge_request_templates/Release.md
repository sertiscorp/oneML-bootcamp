# Information for Requester

## Detail

Release branch is used to quickly patch production release. It branches off of Develop branch.

# Requester checklist (to be checked by requester)

- [ ] 1. Release Detail
  - 1.1 Branches off of Develop
  - 1.2 Update CHANGELOG.md
  - 1.3 Update tag number
- [ ] 2. Branch Management
  - 2.1 Do not squash or rebase
  - 2.2 Merge to Master before Develop
  - 2.3 Do not delete branch when merge to Master
  - 2.4 Only Bug fix can be added after this point

# Information for Reviewer

# Review checklist (to be checked by reviewer)

- [ ] 1. CHANGELOG
  - 1.1 Does CHANGELOG.md include all necessary information?
- [ ] 2. Release Branch Management
  - 2.1 Is this Release branched off of Develop branch?
  - 2.2 Is tag number correct?
  - 2.3 Is code version in project files correct?
