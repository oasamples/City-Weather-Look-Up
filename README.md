# City-Weather-Look-Up
A native JavaFX desktop application that retrieves real-time weather metrics through external REST APIs.


## Project Details

### Name
City Weather Look Up

### Version
1.0.0

---

## Description
This is a high-performance native desktop application that leverages standard Java compilation environments to query real-time meteorological metrics. 

Because external climate APIs operate via spatial geolocation nodes rather than text strings, the application coordinates an automated two-stage backend routine. It captures a typed city string, sanitizes and encodes the text stream, translates it into coordinate parameters via a geocoding API handshake, and passes the geographic data to a global weather forecasting network to parse current temperatures.

To maintain frame stability, all external I/O polling operations are handled on a detached execution pipeline, ensuring that the main application rendering thread remains completely interactive.

---

## Instructions to Run the App

### IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Ensure you have **Java JDK 11 or higher** installed along with the **JavaFX SDK** setup in your environment paths.
2. Open the project folder in your preferred IDE environment.
3. Configure your IDE project structure to include the JavaFX library path modules: `--add-modules javafx.controls`.
4. Run the main entry class: `application.Main`.

### Terminal
1. Open your system command line tool and navigate to the project root directory.
2. Compile the source architecture:
   ```bash
   javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -d bin src/application/*.java

```

3. Execute the binary deployment target package:
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -cp bin application.Main

```



---

## Usage

1. Initialize the **City Weather Look Up** application launcher.
2. Provide an input location (e.g., `Houston` or `Tokyo`) in the central input field and hit enter or click the `Get Weather` action trigger.
3. Observe the immediate metric readout highlighting localized regional configurations alongside dual-unit conversions.
4. Watch the underlying canvas frame dynamically alter color states based on climate changes.

---

## Features

* [x] **Asynchronous Client Architecture:** Decoupled networking tasks preserve an active UI frame structure without hanging threads.
* [x] **Two-Stage API Handshake:** Combines Open-Meteo Geocoding data seamlessly into Forecast API metric collection patterns.
* [x] **Reactive Color Interpolation:** The interface layout changes on the fly from cold (blues) to mild (purples) and warm (reds) based on the temperature.
* [x] **Dual Unit Calculations:** Displays Celsius ($^\circ\text{C}$) and Fahrenheit ($^\circ\text{F}$) outputs concurrently.
* [x] **Zero Third-Party Dependency Overhead:** Implements lean regex mapping rules to parse inbound JSON data directly via core Java libraries.

---

## Additional Notes

* **Network Availability:** An active internet connection is required to talk to the remote REST targets (`*.open-meteo.com`).
* **Design Guidelines:** Style modifications are bound to an external `style.css` file file mapping inside the `application` folder, which keeps presentation separated from core program logic.

```
