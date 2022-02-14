# Online-Video-Learning-Platform
Project using Spring Mvc, Spring Data JPA, Spring Security and Thymeleaf.



Project Summary
Course	Web Application & Service Development with Spring Framework v5 - 2021

Project author 
№	Pseudonym	Face-to-face/ online
1	JGGSH	online

Project name	Online Video Learning Platform

1.	Short project description (Business needs and system features)
Nowadays we all work and learn online. The Online Video Learning Platform (OVLP) provides ability for moderators and admins to create online lessons for users, which can search them. It allows anonymous users to sign up and admins can manage them and their roles. The users, moderators and admins can watch the lessons and comment them. The admins can upload, update and delete the lessons as well as the moderators. The system will be developed using Spring 5 Application Development Framework. It will implement web-based front-end client using Thymeleaf templates. Each page will have a distinct URL, and the routing between pages will be done server side using SpringMVC. The backend will be implemented as a REST using JSON data serialization. There will be also a video streaming from the server to the web client using Spring Content(Spring MVC). The main user roles (actors in UML) are:
•	User – Can watch and comment lessons. 
•	Moderator – Can watch and comment lessons and also can create and modify his/her created lessons. 
•	Administrator – Can watch, comment, upload, update and delete lessons, also manage (access, edit and delete user data) all Registered Users.


2.	Main Use Cases / Scenarios
Use case name	Brief Descriptions	Actors Involved
2.1.	Browse information	The User can browse the information views (Home,  My Profile) in OVLP.	All users
2.2.	Register	Anonimous User can register himself in the system by providing a username and password. By default, all new registered users have User role. 

By default there will be one admin user in the system with username ‘admin’ and password ‘Adminpass1!’.	All users
2.3.	Change User Data 	User can update his/her username and password, also can upload and remove his/her profile picture.
Administrator can view and edit User Data of all Users and assign them Roles: User or Administrator.	User, Administrator
2.4.	Manage Users 	Administrator can browse and filter user by username containing a keyword(case insensitive). 
Administrator can choose a User to manage, and can manage the chosen User – access, edit (Update username, password, remove profile picture and change his/her roles) or delete.
	Administrator
2.5.	Manage Lessons	Every user can search and watch lessons based on the lesson’s title. 
Moderator can upload a new lesson.
Moderator can update or delete a lesson, if necessary.
Administrator can upload a new lesson.
Administrator can update or delete a lesson, if necessary.	All Users, Moderator,  Administrator
2.6.	Rate Lesson	Every user can give feedback by commenting on every lesson.	All Users

3.	Main Views (Frontend)
View name	Brief Descriptions	URI
3.1.	Home	Lessons Search Bar, Find Users, My Profile, Logout	/home
3.2.	Lessons	All Lessons containing queryParam keyword	/home?search={queryParam}
3.3.	Lesson	Concrete Lesson (title, description, reviews and attachments)	/lesson/{lessonId}
3.4.	Lesson Reviews	Lessons Search Bar, Find Users, My Profile, Logout	/
3.5.	User Registration	User Registration View	/register
3.6.	Login	Login View	/login
3.7.	User Data 	Personal Details	/user/{username}
3.8.	Users	All Users	/users
3.9.	A Lesson Resource	Resource attached to a Lesson	/home/{lessonId}/view-video (or view-profile-picture)

4.	API Resources (Backend)
View name	Brief Descriptions	URI
4.1.	Users	GET User Data for all users. Available only for Administrators.	/users
4.2.	User	GET, PUT, DELETE User Data for User with specified userId, according to restrictions described in UCs.	/users/{username}
4.3.	Login	POST User Credentials (e-mail address and password) and receive a valid Security Token to use in subsequent API requests.	/login
4.4.	Logout	POST a logout request for ending the active session with OVLP, and invalidating the issued Security Token.	/logout
4.5.	Lessons	GET Lessons, and POST new Lesson (Id is auto-filled by OVLP and modified entity is returned as result from POST request), according to User's Role and identity security restrictions.	/home
4.6.	Lesson	GET, PUT, DELETE Lesson (including attached resources) for Lesson with specified lessonId.	/home/{lessonId}
4.7.	A Lesson Resource	POST, GET, DELETE (if you have authorities) a Resource.	/home/{lessonId}/view/{resourceId}
4.8.	Review Lesson	GET all reviews and POST review for Lesson with specified lessonId.	/home/{lessonId}

