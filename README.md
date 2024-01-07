# Done Tracker

**RoomDoneTracker: Coroutines & SwipeGesture**

This project, RoomDoneTracker, showcases the implementation of a task-tracking app with a focus on seamless user interaction. It utilizes the Room Persistence Library for efficient local data storage, Coroutines for asynchronous operations, and SwipeGesture for intuitive task management. The app allows users to add, delete, and archive tasks with smooth swipe gestures, providing a user-friendly and responsive experience. Dive into the code to explore the integration of Room, Coroutines, and SwipeGesture for a comprehensive understanding of modern Android app development.

[![Watch the video](https://img.youtube.com/vi/OYqNX1Sak-c/0.jpg)](https://youtube.com/shorts/OYqNX1Sak-c)
1. **Room Persistence Library:**
    - **Entity Definition:** Defined data entities using Room annotations, specifying the structure of the database.
    - **DAO (Data Access Object):** Created DAO interfaces with methods for database operations, including insert, delete, and queries.
    - **Database Initialization:** Initialized the Room database instance and obtained the DAOs for accessing data.
2. **Kotlin Coroutines:**
    - **Asynchronous Programming:** Implemented Kotlin Coroutines to perform asynchronous tasks without blocking the main thread, enhancing app responsiveness.
    - **CoroutineScope:** Used **`CoroutineScope`** to launch coroutines and manage their lifecycles, ensuring proper handling of background tasks.
3. **SwipeGesture with ItemTouchHelper:**
    - **User Interaction:** Enhanced the user experience by implementing swipe gestures for tasks using **`ItemTouchHelper`**.
    - **Swipe Actions:** Enabled left and right swipe actions for deleting and archiving tasks, providing an intuitive interface.
4. **Android Architecture Components:**
    - **ViewModel:** Leveraged ViewModel to manage UI-related data and handle communication between the UI and data layers.
    - **Repository Pattern:** Organized data access and provided a clean API to the rest of the application.
5. **User Interface Design:**
    - **Dialogs:** Implemented dialogs for adding new tasks, enhancing user interaction.
    - **Snackbar Feedback:** Utilized Snackbars to provide feedback on task actions, including deletion, archiving, and undoing actions.
6. **Gradle Configuration:**
    - **Dependency Management:** Configured Gradle build files for managing dependencies, plugins, and other project configurations.
7. **Localization:**
    - **Multilingual Support:** Supported localization by providing string resources in multiple languages, improving accessibility for a diverse user base.
8. **GitHub Repository Management:**
    - **Repository Setup:** Created a GitHub repository to host and share the project code.
    - **Version Control:** Utilized Git for version control, allowing collaborative development and tracking changes.

This project provided a hands-on experience in building a feature-rich Android app, integrating key technologies and best practices in modern Android development. The combination of Room, Coroutines, and SwipeGesture showcased the ability to create responsive, user-friendly applications with a solid architecture.
