# Suite Deployer
[![Build Status](http://shc-jenkins-itsma.hpeswlab.net:8080/buildStatus/icon?job=suite-deployer/master)](http://shc-jenkins-itsma.hpeswlab.net:8080/view/Dashboard/job/suite-deployer/job/master/)

The suite deployment controller for services.

### Rules for Pull Request

- Do NOT create branch of https://github.houston.softwaregrp.net/SMA-RnD/suite-deployer.  Using fork.
- One PR, one commit.
  Strongly recommended that one task, one commit. And Not allowed more than one commit in a single PR
  Best Practice workflow:
  1. git fetch upstream
  2. git checkout -b task upstream/master
  3. Implement the task
  4. git commit (Refer to rule 3 for commit message)
  5. git push origin
  6. go to your forked repo, branch task, create the PR      
- Commit message 
  Short (50 chars or less) summary of changes
  
  More detailed explanatory text, if necessary.  Wrap it to about 72 characters or so.  In some contexts, the first line is treated as the subject of an email and the rest of the text as the body.  The blankline separating the summary from the body is critical (unless you omit the body entirely); tools like rebase can get confused if you run the two together.
  Further paragraphs come after blank lines.
  - Bullet points are okay, too
  - Typically a hyphen or asterisk is used for the bullet, preceded by a
    single space, with blank lines in between, but conventions vary here
- Using format "Promote Itsma service XXX to version XXX"
