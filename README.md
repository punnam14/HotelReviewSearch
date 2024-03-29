# HotelReviewSearch   
A Hotel Search website  <br><br>
* Core Technologies <br><br>
> Java: Primary programming language, used for the development of the server-side logic and functionalities. <br>
> Jetty Server: Used to deploy and manage the web application. Jetty is a lightweight, highly scalable Java-based web server and servlet engine. <br>
> Servlets: Used Java Servlets for handling requests and responses between the web server and clients. Servlets are Java programming language classes that are used to extend the capabilities of servers that host applications accessed by means of a request-response programming model.  
> Velocity Template Engine: Utilized for rendering HTML content on the server side. Velocity is a Java-based template engine that provides a simple and powerful way to generate web content.  
> MySQL Database: Used JDBC for database connectivity. Interacts with a MySQL database for data persistence, including operations like creating tables, inserting, and querying data.  
> Apache Commons and Gson: Libraries used for utility functions such as string escaping and JSON parsing, respectively.  
> Maven: Used for project management and build automation.  
> Google Gson: Used for JSON parsing and serialization/deserialization of Java objects to and from JSON.  
> Java Concurrency Utilities: Used ExecutorService and Phaser in ReviewManager.java for managing concurrent tasks and synchronization (Multithreaded reading a large number of reviews from JSON files concurrently).  
> HTML CSS and Bootstrap: For the front-end development  
> JavaScript and jQuery: For the validation functions in HTML forms. <br> <br>

* Features and Description: <br><br>
> Used Bootstrap templates to give the website a clean, "professional" look. Used templates (and Velocity) for all pages. This includes the registration and login pages too.   <br><br>
![Alt text for the image](https://raw.githubusercontent.com/punnam14/HotelReviewSearch/main/Website%20Demo%20Images/Screenshot%202024-02-21%20at%205.49.49%20PM.png) <br><br>
![Alt text for the image](https://raw.githubusercontent.com/punnam14/HotelReviewSearch/main/Website%20Demo%20Images/Screenshot%202024-02-21%20at%205.47.55%20PM.png) <br><br>
> Used MySql database to store all data, including hotels and reviews, and the data required to implement features such as "favorite hotels", "history of Expedia clicks" etc. All operations used the database (accessed using jdbc), including add/edit/delete a review.   <br><br>
![Alt text for the image](https://raw.githubusercontent.com/punnam14/HotelReviewSearch/main/Website%20Demo%20Images/Screenshot%202024-02-21%20at%205.51.19%20PM.png) <br><br>
> Used Ajax (to update only the part of the webpage without reloading the whole page) for the following features:  
> • Displayed the weather at the latitude and longitude of the given hotel  
![Alt text for the image](https://raw.githubusercontent.com/punnam14/HotelReviewSearch/main/Website%20Demo%20Images/Screenshot%202024-02-21%20at%205.48.31%20PM.png) <br><br>
> • Displayed a list of tourist attractions OR restaurants within a certain radius of the hotel   
> • Showed the hotel on the map  
> • Allowed the user to save favorite hotels (the corresponding table stored in the database), and to clear the list of favorite hotels  
> Stored a history of all expedia hotel links visited by the given user.   
> • Allowed the user to view and clear that history. This page is available only to users who successfully logged in.  
> Provided pagination:  
> • Showed a fixed number of reviews on each page;  <br><br>
![Alt text for the image](https://raw.githubusercontent.com/punnam14/HotelReviewSearch/main/Website%20Demo%20Images/Screenshot%202024-02-21%20at%205.48.58%20PM.png) <br><br>
> • The user is able to navigate through multiple pages with reviews.  
> Stored and displayed the last date and time the user logged in to the website before this time (the feature is available only for logged in users).  
> Followed good object-oriented design principles throughout the project.   <br><br>
![Alt text for the image](https://raw.githubusercontent.com/punnam14/HotelReviewSearch/main/Website%20Demo%20Images/Screenshot%202024-02-21%20at%205.49.19%20PM.png)
