# HotelReviewSearch
A Hotel Search website  
Features and Description: Used Bootstrap templates to give the website a clean, "professional" look. Used templates (and Velocity) for all pages. This includes the registration and login pages too.   
Used MySql database to store all data, including hotels and reviews, and the data required to implement features such as "favorite hotels", "history of Expedia clicks" etc. All operations used the database (accessed using jdbc), including add/edit/delete a review.  
Used Ajax (to update only the part of the webpage without reloading the whole page) for the following features:  
• Displayed the weather at the latitude and longitude of the given hotel  
• Displayed a list of tourist attractions OR restaurants within a certain radius of the hotel  
• Showed the hotel on the map  
• Allowed the user to save favorite hotels (the corresponding table stored in the database), and to clear the list of favorite hotels  
Storeed a history of all expedia hotel links visited by the given user.   
• Allowed the user to view and clear that history. This page is available only to users who successfully logged in.  
Provided pagination:  
• Showed a fixed number of reviews on each page;  
• The user is able to navigate through multiple pages with reviews.  
Storeed and displayed the last date and time the user logged in to the website before this time (the feature is available only for logged in users).  
Floowed good object-oriented design principles throughout the project.   
