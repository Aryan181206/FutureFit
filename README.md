# FutureFit –  AI-Based Career Guidance App 🎯
Get Future Ready with AI

FutureFit is an AI-powered career guidance app built for Android that helps users discover their ideal career paths based on personalized assessments.  
It combines aptitude testing, MBTI-based personality analysis, interest mapping, and user experience to deliver tailored career suggestions using Gemini AI.  
With Firebase integration, dynamic skill & experience tracking, and a modern Material Design UI, FutureFit is your smart assistant for career clarity.

## 🚀 Features

- 🔐 Authentication System
  - Firebase Email/Password Login & Signup
  - Auto-login using SharedPreferences
    
- 📋 User Profile
  - Editable Personal & Educational Info
  - Organized with TabLayout for easy access

- 🧠 Assessments
  - Aptitude Test (Logical, Numerical, Verbal Reasoning)
  - Personality Test (MBTI types like INTJ, ENFP, etc.)
  - Interest Survey to map user inclinations

- 💼 Experience & Skills
  - Add/Edit/Delete multiple Employment History entries
  - Add Technical & Soft Skills dynamically with spinner + 'Other' option
  - Display skills in a clean card-based RecyclerView

- 🧑‍💻 Career Prediction
  - Gemini AI integration for career recommendations
  - Replaced Flask backend with direct API integration
  - Save predictions to Firestore for future reference

- 📈 Progress & Reports
  - Visual progress bars for Aptitude test results
  - MBTI results displayed with full description
  - View and manage Saved Careers & Prediction History

- 🗃 Firestore Integration
  - Well-structured data model
  - Realtime updates and clean data handling
  - Unique IDs for dynamic list items


## 🛠 Tech Stack

| Layer        | Tools/Tech Used                        |
|--------------|----------------------------------------|
| Frontend | Android (Kotlin + XML), Material Design |
| Backend  | Firebase Authentication & Firestore, Cloudinary (Image Upload), Gemini AI API |
| Storage  | SharedPreferences (Session Persistence), Firestore (User Data) |
| AI       | Gemini AI (Career Prediction)        |
| Design   | Material Components, CardViews, RecyclerViews |



## 📦 Setup & Installation

Follow these steps to set up and run the FutureFit app locally using Android Studio.

### 🔧 Prerequisites
- Android Studio (latest version recommended)
- Firebase project with `google-services.json`
- Cloudinary account (for image uploads)
- Gemini AI API key (for career prediction)

---

### 🚀 Steps to Run the App

1. Clone the Repository
   ```bash
   git clone https://github.com/your-username/futurefit.git
   
2. Open Project in Android Studio
Open Android Studio → File → Open → Select the cloned FutureFit folder.

3. Configure Firebase
Go to your Firebase Console → Project Settings
Download the google-services.json file
Place it inside the /app directory

4.Set Up Cloudinary

1. Create a Cloudinary account at [https://cloudinary.com](https://cloudinary.com)
2. Navigate to your **Dashboard** and get your:
   - `cloud_name`
   - `api_key`
   - `api_secret`

3. Open your `MyApp.kt` file (inside `com.example.futurefit` package)
4. Replace the config values in the `onCreate()` method with your actual Cloudinary credentials:

   ```kotlin
   class MyApp : Application() {
       override fun onCreate() {
           super.onCreate()
           val config = HashMap<String, String>()
           config["cloud_name"] = "your_cloud_name"
           config["api_key"] = "your_api_key"
           config["api_secret"] = "your_api_secret"
           MediaManager.init(this, config)
       }
   }

5. 🤖 Set Up Gemini AI (Google AI)

1. Get your API key:
   - Go to the [Google AI Studio](https://makersuite.google.com/app) and create a project.
   - Generate a **Gemini API key** from your account settings.

2. Add the API key in your app code:

   - Open the file where you're initializing the Gemini model (e.g., `CareerPredictionActivity.kt` or similar).
   - Locate the `startPrediction()` function or wherever `GenerativeModel` is used.
   - Replace the placeholder key with your actual Gemini API key:

     ```kotlin
     val generativeModel = GenerativeModel(
         modelName = "gemini-2.0-flash",
         apiKey = "YOUR_GEMINI_API_KEY"
     )
     ```

3. **Important:**
   - **Never expose your actual API key publicly in GitHub.**
   - Use **local properties** or **secure storage** in production.
   - For development, you can use a `.properties` file and read it using `BuildConfig`.

4. The model will now be ready to generate career predictions based on the user prompt.


6. Build & Run the App

Make sure your emulator or physical device has internet access
Click the Run button (green play icon) in Android Studio
The app will launch and initialize with all features



## Screenshots 

### 📷 Screenshots

<div style="display: flex; flex-wrap: wrap; gap: 10px;">
  <img src="https://github.com/user-attachments/assets/3a8ab3ab-9a0b-4108-b9d9-78fd72a03c1a" width="200">
  <img src="https://github.com/user-attachments/assets/65e3414b-3e9d-4fa4-bdb6-6aa170e7da4b" width="200">
  <img src="https://github.com/user-attachments/assets/d79501f9-15e6-45b3-8ee9-b31e372327e0" width="200">
  <img src="https://github.com/user-attachments/assets/4a98eed2-52db-4dee-a577-553229b1dd76" width="200">
  <img src="https://github.com/user-attachments/assets/8afbcade-7d02-4474-b5b7-1e641a4851a0" width="200">
  <img src="https://github.com/user-attachments/assets/253e9343-6182-467b-ab89-16028aa5dac0" width="200">
  <img src="https://github.com/user-attachments/assets/1d340c4c-539f-45c2-9327-c4ec02769d66" width="200">
  <img src="https://github.com/user-attachments/assets/ce539add-cba0-4a8b-aa94-7d2ad3feb41f" width="200">
  <img src="https://github.com/user-attachments/assets/86bf64f8-fdbd-49c2-8ba3-dbcee5091e63" width="200">
  <img src="https://github.com/user-attachments/assets/3f7f16a4-5515-41be-b3b4-216dcb636a45" width="200">
  <img src="https://github.com/user-attachments/assets/8380f68e-fb70-4603-affb-9a3887ecdc95" width="200">
  <img src="https://github.com/user-attachments/assets/ba6fb0a2-3a2c-4ccd-9942-f766f2b5d60e" width="200">
  <img src="https://github.com/user-attachments/assets/ee49f08a-be6f-4c6d-9f6c-7d1b6c0af9f5" width="200">
  <img src="https://github.com/user-attachments/assets/0d3964b0-0103-46d9-ad0a-1633b3817028" width="200">
  <img src="https://github.com/user-attachments/assets/4837e05e-4b87-413d-b15c-06d7be08c85e" width="200">
  <img src="https://github.com/user-attachments/assets/de7549c2-f32a-4a1e-b625-2700af358b98" width="200">
  <img src="https://github.com/user-attachments/assets/53952118-b360-4bad-9b36-e90707e9ed19" width="200">
  <img src="https://github.com/user-attachments/assets/4f54b0b7-2727-4c3c-8086-e3d3651a85a6" width="200">
</div>


## Contributing

Contributions are welcome! Please follow these steps:

  - Fork the repository
  - Create a feature branch (git checkout -b feature/YourFeature)
  - Commit your changes (git commit -m 'Add some feature')
  - Push to the branch (git push origin feature/YourFeature)
  - Open a Pull Request


🌱 Future Scope

- Resume builder based on user data
- Voice-based AI mock interview
- PDF report generation for career predictions
- Multi-language support for accessibility


👨‍💻 Developer :
Aryan Sharma
📧 Email: aryan180906@gmail.com
🔗 LinkedIn: https://www.linkedin.com/in/aryan-sharma-26276131a/
Project Link - https://github.com/Aryan181206/FutureFit/





    
