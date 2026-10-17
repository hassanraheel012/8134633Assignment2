# CourseNest (8134633Assignment2)

**NIT3213 Mobile Application Development – Final Assignment** · Footscray

CourseNest is an Android app that logs in to the nit3213 API with a student ID and first name, then shows the topic it returns as a list of courses. It uses a warm cream, orange and dark colour scheme with the Fredoka font throughout, and an owl-and-book logo.

The app has the three screens the spec asks for: **Login**, **Dashboard** and **Details**.

---

## Screens

**Login**
The CourseNest logo and name sit above a Learn → Practise → Achieve illustration, followed by the Student ID and Password fields. Pressing **Log in** sends a POST request to `/footscray/auth`. While the request runs, a spinner replaces the button text. Messages are shown under the fields for:
- empty fields
- wrong details (4xx response)
- a server error (5xx)
- no internet connection

If the login works, the app goes straight to the Dashboard.

**Dashboard**
A dark welcome header shows the logged-in student ID, the topic (from the `keypass`) and how many courses were loaded, with a log-out button in the corner. The app uses the `keypass` to call `/dashboard/{keypass}` and shows the courses in a RecyclerView. Each rounded card has a course icon, a course-code badge (only when the API sends a code), the course name and its other fields. The description is left out here, as the spec says. There are also loading, empty and error states, with a **Try again** button for errors. Tapping a card opens the Details screen.

**Details**
A header card shows the course code and name. Below it, **Course information** lists every field with a matching icon (e.g. a person icon for an instructor and a clock for a duration), and the **Description** sits in its own card underneath.

---

## How the data is handled

The field names inside each entity depend on the topic, so each entity is read as a `Map<String, Any?>` instead of a fixed data class. `CourseMapper` splits off the description and keeps every other field in the order the API sent them. The `Course` model then works out:
- the **code**: the first field whose name contains "code" (e.g. `courseCode`)
- the **title**: the first field whose name contains "name" or "title", or otherwise the first other field
- the **summary**: everything else

Nothing is hard-coded or made up. If the API doesn't send a code, the badge simply isn't shown.

---

## Project structure

```
com.s8134633.coursenest
├── CourseNestApplication.kt     @HiltAndroidApp
├── MainActivity.kt              single activity hosting the NavHostFragment
├── di/NetworkModule.kt          Hilt module providing Moshi, OkHttp, Retrofit and ApiService
├── data/
│   ├── network/ApiService.kt    Retrofit endpoints
│   ├── model/                   LoginRequest, LoginResponse, DashboardResponse
│   ├── repository/              CourseRepository (injected with ApiService)
│   └── session/SessionManager   @Singleton holding the student ID and keypass
├── model/                       Course, DetailRow
├── util/CourseMapper.kt         turns API entities into Course objects and readable labels
└── ui/
    ├── login/                   LoginFragment, LoginViewModel, LoginUiState
    ├── dashboard/               DashboardFragment, DashboardViewModel, DashboardUiState, CourseAdapter
    ├── details/                 DetailsFragment, DetailRowAdapter
    └── common/                  CourseIcons, WindowInsetsHelper
```

**Architecture:** MVVM with the repository pattern. ViewModels expose a `StateFlow` of UI state, and fragments collect it with `repeatOnLifecycle`.

**Dependency injection (Hilt):** `NetworkModule` provides the networking classes, and `CourseRepository` and `SessionManager` use `@Inject` constructors. ViewModels are `@HiltViewModel` and fragments are `@AndroidEntryPoint`. Nothing creates its own dependencies.

**Navigation:** the Navigation component with safe-args. The selected `Course` is passed to the Details screen as a `Serializable` argument.

---

## How to run it

**What you need**
- An Android Studio version that supports Android Gradle Plugin 9.4.1
- JDK 17 (the one bundled with Android Studio is fine)
- An emulator or phone running Android 7.0 (API 24) or newer
- An internet connection

**Steps**
1. Open the project folder in Android Studio (**File > Open**). It shows up as **8134633Assignment2**.
2. Let Gradle sync.
3. Choose an emulator or phone and press **Run**.

**Logging in**
- **Student ID:** your ID without the "s" (e.g. `12345678`)
- **Password:** your first name (case sensitive)

**Heads up:** the API runs on a free server that sleeps when it isn't being used, so the first login can take up to a minute. The timeout is set to 60 seconds, and the app shows a message while it waits.

---

## Running the tests

Right-click `app/src/test/java` in Android Studio and choose **Run Tests**, or run:

```bash
./gradlew testDebugUnitTest
```

The tests use JUnit 4, MockK and kotlinx-coroutines-test. ViewModel tests swap the Main dispatcher for a `StandardTestDispatcher` and use `runTest` and `advanceUntilIdle()`.

What's tested:
- **LoginViewModelTest:** empty fields, the loading state, a successful login saving the session, trimming the student ID, wrong details, server errors, no internet, and double taps sending only one request
- **DashboardViewModelTest:** loading courses with the keypass, loading, empty and error states, retrying, a missing session and logging out
- **CourseRepositoryTest:** the login request and keypass, passing errors on, and mapping dashboard entities
- **CourseMapperTest / CourseTest:** separating the description, keeping field order, formatting values and labels, and working out the code, title and summary
- **CourseIconsTest:** picking the right icon for common course fields
- **SessionManagerTest, CourseAdapterTest, DetailRowAdapterTest**

---

## Libraries

Hilt · Retrofit · Moshi · OkHttp logging interceptor · Navigation component + safe-args · AndroidX Lifecycle (ViewModel, StateFlow) · Material Components · JUnit 4 · MockK · kotlinx-coroutines-test

Fredoka font: SIL Open Font License.
