# Materials for developer interview at Data Realm

## Purpose
The purpose of this exercise is not to give a "gotcha" question or puzzle, but a straight-forward (albeit contrived)
example of the kind of requirement that might arise in a real project so that we have shared context for a technical 
conversation during the interview. We are interested in how you approach a project, so you should feel free to add new 
class files as well modify the files that are provided as you see fit. Use of your favorite libraries or frameworks is
fine, but not required. How you demonstrate the correctness of your implementation is up to you.

## Requirements
We have a small application that provides population statistics for countries. Recently some of our customers
have reported some issues with the application and we would like you to investigate the problems.

### Issue One

One of our customers reported to us that sometimes the data they get back from our api seems incomplete, they noticed
that sometimes countries are missing from the `/all` response.

### Issue Two

We have had several reports that in the afternoon it takes a really long time to load data, this seems
to be affecting everyone and it seems to affect all of the api's. 

## Building and Running the code

You may import and run the project within the IDE of your choice or run the following gradle command to generate and
a jar to execute.

From the root dir execute `gradlew build` (`./gradlew build` in some environments) and then, `java -jar
build/libs/integration-interview.jar`.

_Note: why not maven? Primarily due to the convenience of the gradle wrapper, you don't need to install anything and
you will always be using the version specified in the project_