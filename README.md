# Local Weather App

A modern, native Android weather application built with Kotlin and Jetpack Compose. This app provides real-time weather updates based on your current location or a searched city/ZIP code.

## Features

- **Real-time Local Weather**: Automatically detects your location to show current temperature and conditions.
- **Location Search**: Search for any city or ZIP code globally with built-in debounce for a smooth experience.
- **Hourly Forecast**: View temperature changes for the next 24 hours.
- **Weekly Forecast**: Plan ahead with a 7-day high-temperature forecast.
- **Interactive UI**: Select any hour or day to update the main weather display instantly.
- **Dynamic Imagery**: High-quality weather icons that reflect the current sky conditions.
- **Modern Tech Stack**: Built with MVVM architecture, Clean Architecture principles, and Dagger Hilt for DI.

## Screenshots

| Main Screen | Search Feature |
| :---: | :---: |
| ![Main Screen Placeholder](https://via.placeholder.com/300x600?text=Main+Weather+Screen) | ![Search Placeholder](https://via.placeholder.com/300x600?text=Search+Functionality) |

## Tech Stack

- **UI**: Jetpack Compose (Modern Toolkit)
- **Architecture**: MVVM + Clean Architecture (Domain, Data, Presentation)
- **Dependency Injection**: Dagger Hilt
- **Networking**: Retrofit & Gson
- **Image Loading**: Coil
- **Location**: Google Play Services Location
- **API**: [Open-Meteo](https://open-meteo.com/) (Free, No API Key required)

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio (Ladybug or newer recommended).
3. Sync Gradle and run the app on an emulator or physical device.
4. Grant location permissions when prompted to see your local weather.

## Project Structure

- `data`: API services, models, and repository implementations.
- `domain`: Business logic, use cases, and repository interfaces.
- `presentation`: ViewModels and UI state management.
- `ui`: Jetpack Compose themes, components, and screens.
- `di`: Hilt modules for dependency injection.
