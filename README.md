# compose-coverflow

Jetpack Compose implementation of 🍎 CoverFlow.

Triggerwarning: You might find logic that is not implemented in the most idiomatic way possible,
contains bugs, or is just inefficient.
If so, let me know!
This project was created as a learning experience since I'm trying to learn programming Android apps
and I thought this was an interesting challenge.
So if you have ideas on how to improve it, let me know, or create a pull request.
I use this project to learn how to properly configure a GitHub project as well, so if you think the
workflow is not the best it could be, let me know as well.
This code contains a little bit of everything, and although no part of it is perfect, it's a good
scaffold:

* theres a first test
* KDoc gets generated

I plan to publish the coverflow module as a library to a maven central eventually, automated by
GitHub Actions.
The app module can be run on an Android device as a demo to show what the Component is capable of
and how to configure it.

## Demonstration Video

[![Watch a demonstration vide](https://img.youtube.com/vi/dfrZBEqYYs8/0.jpg)](https://www.youtube.com/watch?v=dfrZBEqYYs8)

## Documentation

Read the [KDoc](https://pakohan.github.io/compose-coverflow/coverflow/com.pakohan.coverflow/).

## Usage

```kotlin
CoverFlow(modifier = Modifier.background(Color.Black)) {
    items(20) {
        Text(
            text = "This is element number $it",
            textAlign = TextAlign.Center,
            modifier = Modifier.background(Color.White)
        )
    }
}
```
