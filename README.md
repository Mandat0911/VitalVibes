# VitalVibe App  

## **Overview**  
VitalVibe is an Android application designed to facilitate and manage blood donation campaigns. It provides seamless user and admin experiences, offering functionalities such as campaign site management, user role assignments, notifications, and reporting. The app is powered by Firebase for real-time data operations and notifications.  

---

## **Info Details**  
**Name:** [Nguyen Man Dat]  

---

## **Features**  

### **User Features:**  
- **User Registration and Authentication:** Secure login and registration using Firebase Authentication.  
- **Blood Donation Campaigns:** Explore nearby donation sites using map integration.  
- **Profile Management:** Manage personal details and donation history.  
- **Notifications:** Receive timely updates on upcoming campaigns.  

### **Admin Features:**  
- **User Management:** View, delete, and manage users.  
- **Campaign Management:** Add, update, and monitor donation campaigns.  
- **Reporting:** Generate and view reports for donation statistics.  

---

## **Technologies Used**  

### **Frontend:**  
- Java (Android Development)  
- XML for layouts  

### **Backend:**  
- Firebase Realtime Database for data storage and real-time updates  
- Firebase Cloud Messaging for notifications  

### **Maps Integration:**  
- Google Maps API for locating nearby donation sites  

### **UI Components:**  
- ChipNavigationBar for bottom navigation  
- RecyclerView for displaying lists  
- Material Design Components for a modern UI  

---

## **Prerequisites**  
- Android Studio (latest version recommended)  
- Firebase project configured with:  
  - Realtime Database  
  - Authentication  
  - Cloud Messaging  
- Google Maps API key  

---

## **Installation**  
1. Clone the repository:  
   ```bash  
   git clone https://github.com/Mandat0911/vitalvibe.git  
   ```  
2. Open the project in Android Studio.  
3. Configure Firebase:  
   - Download the `google-services.json` file from the Firebase Console.  
   - Place it in the `app` directory of the project.  
4. Configure Google Maps:  
   - Add your Google Maps API key to the `AndroidManifest.xml`:  
     ```xml  
     <meta-data  
         android:name="com.google.android.geo.API_KEY"  
         android:value="YOUR_API_KEY" />  
     ```  
5. Build and run the project on an emulator or physical device.  

---

## **Project Structure**  
- **Activities:** Contains all screens like `HomeActivity`, `ManageUser`, and `CampaignActivity`.  
- **Adapters:** Includes `UserListAdapter` for managing `RecyclerView` data.  
- **Models:** Defines data structures like `Donor` and `Campaign`.  
- **Layouts:** XML files for UI design.  
- **Firebase:** Real-time data and authentication integration.  

---

## **Usage**  
1. **Admin:**  
   - Log in as an admin to manage users and campaigns.  
   - Use the admin dashboard for accessing reports and creating campaigns.  
2. **User:**  
   - Register and log in to explore donation campaigns.  
   - Use the map feature to locate nearby donation sites.  

---

## **Drawbacks**  
1. **Limited Offline Access:** The app requires an internet connection for most features, such as fetching donor and campaign data.  
2. **Admin-Only Features:** Some functionalities, such as campaign management, are currently restricted to admins, limiting donor interaction.  
3. **Scalability Issues:** Firebase Realtime Database is efficient for small-scale applications but may require migration for large-scale use.  
4. **Map Integration:** Map functionality is basic and needs further enhancements, such as geolocation filtering for nearby donation sites.  

---

## **Screenshots**  
Coming soon!  

---

## **Roadmap**  
- [ ] Implement detailed analytics for campaigns.  
- [ ] Add support for push notifications for upcoming campaigns.  
- [ ] Enhance the user interface with more dynamic features.  

---

## **Contributions**  
We welcome contributions! Please follow these steps:  
1. Fork the repository.  
2. Create a feature branch:  
   ```bash  
   git checkout -b feature-name  
   ```  
3. Commit your changes and push the branch:  
   ```bash  
   git push origin feature-name  
   ```  
4. Open a pull request.  

---

## **License**  
This project is licensed under the MIT License. See the LICENSE file for details.  

---

## **Contact**  
For any queries or support, please contact:  
- **Email:** nguyenmandat000@gmail.com  
