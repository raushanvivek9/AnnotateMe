# AnnotateMe
This Android-based application enables Annotators to label the dataset by two major techniques, Simple image classification, and Bounding box. The Simple classification works with one class having one label, whereas, in the Bounding box, we can bind multiple objects in an image within a single frame. After creating the project, Annotators or users can share and work on the same project with others using the functionalities of a private repository. Annotators can also work on projects by downloading datasets from the public repository. They can also export the project files with various file formats.
<br>
AnnotateMe is a mobile-based Data Annotation and Image Labeling Tool for ML and DL model. This mobile-based annotation tool is a software application designed for mobile devices that allows users to annotate and label on various types of image contents. With this tool, users can easily add comments, highlights, drawings, and other annotations to images, documents, and videos directly from their mobile devices. One of the key advantages of a mobile-based annotation tool is its convenience and portability. This tool offers several advantages. It provides flexibility and convenience as users can annotate on the go, enabling collaboration and quick decision-making even when they are not at their desks. This allows seamless integration with cloud storage services, facilitating easy storage, sharing, and access to annotated files across devices.<br>
<br>
AnnoatateMe facilitates real-time collaboration by allowing users to make annotations and share them instantly with team members or stakeholders. This immediate feedback loop enhances communication, accelerates decision-making, and streamlines workflows. Team members can provide comments, suggestions, or revisions in real-time, leading to faster iterations and improved project outcomes. Annotation projects are automatically synced, ensuring that users have access to the most up-to-date versions on any Android device (minimum Android version 6). This feature enhances productivity, as users can switch between devices without worrying about data loss or file compatibility issues.<br>
<br>
This tool typically offers intuitive and user-friendly interfaces optimized for mobile devices, making the labeling and annotation process efficient and accessible to users with varying levels of technical expertise. These interfaces make it easy for users to navigate and utilize the annotation features effectively, even for those with limited technical expertise. The streamlined and accessible interface ensures a smooth and efficient annotation process, saving time and effort. Overall, a mobile-based annotation tool enhances productivity, collaboration, and accessibility, making it a valuable asset for individuals and teams working in diverse industries.<br>
<br>

Objective <br>
•	The objective of the project is to ease the data labeling process by using a mobile-based application. <br>
•	We also aim to increase the labeling by making it possible to label more samples through the participation of normal users i.e. other than trained annotators. <br>
•	After creating the project, Annotators or users can share and work on the same project with others using the functionalities of a private repository.<br>
•	The proposed project will also enable the collection of more samples as any user with a smartphone can participate and share labeled images on request or voluntarily<br>
<br><br>
PROJECT MODULES<br>
1)	Authentication<br>
a.	Sign in<br>
b.	Sign up<br>
c.	Sign out<br>
d.	Forget password<br>
2)	User Management<br>
a.	Profile<br>
b.	Help<br>
c.	About us<br>
3)	Project Management<br>
a.	Create Project<br>
b.	Share project<br>
•	Public<br>
•	Private<br>
4)	Annotation Adopted<br>
a.	Simple Classification<br>
b.	Bounding Box<br>
c.	Semi-Automated Model<br>
5)	Dataset Export<br>
a.	.txt<br>
b.	.json<br>
c.	.csv<br>
d.	.xls<br>
6)	Email Verification & Notification<br>
&nbsp;a.	OTP Verifications<br>
&nbsp;b.	Shared project notification<br>
&nbsp;c.	Push Notification<br>

2.5.1  Module 1: Authentication<br>
a.	Sign up: Allows new users to create an account with the system. This sub-module typically includes fields to collect user information such as name, email address, and password.<br>
b.	Sign in: Allows users to log in to the system using their username/email and password.<br>
c.	Sign out: Allows users to log out of the system, terminating their session.<br>
d.	Forget password: Allows users who have forgotten their password to reset it. This typically involves sending a password reset link to the user's email address.<br>

2.5.2  Module 2: User Management<br>
a.	Profile: Allows users to view and edit their account information such as name, email address, and password.<br>
b.	Help: Provides information on how to use the system, typically through a knowledge base or frequently asked questions (FAQ) section.<br>
c.	About us: Provides information about the system and its creators.<br>

2.5.3  Module 3: Project Management<br>
a.	Create project: Allows users to create new annotation projects. This sub-module typically includes fields to enter project information such as name, description, and type of annotations required.<br>
b.	Delete project:  Allows users to delete created annotation projects.<br>

2.5.4  Module 4: Annotation Adopted<br>
a.	Simple classification: Allows users to classify objects in images or text into pre-defined categories.<br>
b.	Bounding box: Allows users to draw rectangular boxes around objects in images, indicating their location.<br>
c.	Semi-automated: Allows users to leverage machine learning algorithms to suggest annotations, which can then be reviewed and corrected by human annotators.<br>

2.5.5	 Module 5: Dataset Export<br>
a.	.txt: Allows users to export the annotated data in plain text format.<br>
b.	 .csv: Allows users to export the annotated data in comma-separated values (CSV) format.<br>
c.	.json: Allows users to export the annotated data in JavaScript Object Notation (JSON) format.<br>
d.	.xls: Allows users to export the annotated data in Microsoft Excel format.<br>

2.5.6  Module 6: Shared Project Management<br>
a.	Public: Allows users to share their annotation projects publicly, enabling others to view and contribute to them.<br>
b.	Private: Allows users to share their annotation projects only with specific users or groups, restricting access to them.<br>

2.5.7  Module 7: Email Verification<br>
a.	OTP Verification: Allows users to verify their email addresses by entering a one-time password (OTP) sent to their email by using Java Mail API.<br>
b.	Shared Project Notification: Sends an email notification to users when they are invited to contribute to a shared project by using Java Mail API.. <br>
c.	Push Notification: Annotators and Users will get push notification on their Android devices in case of any project shared in the public and private repository in the AnnotateMe application by using Retrofit API.<br>

#Application Screenshots<br>
<br>
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/a72aefa0-c39d-4211-986b-691a9fb6e089)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/d1de4c05-6dba-4b99-be33-2094d4c7ac4a)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/f3fa3c14-6681-4d9e-8847-70ad72a2ad06)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/74905b51-53f6-42b7-9f4d-02953db16add)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/847d9ba2-d74b-43eb-b9e2-af495c4d5b3e)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/caa5021b-6499-4c85-b79c-8d91b96cc501)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/b280dcef-77c8-4cab-ab0d-5982de928cfe)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/8dbc1a82-b3bd-46e6-b63d-88bcc84895ae)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/e77c2e25-6ce7-4834-8f81-a84787c30a97)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/69f6bd10-cf4f-49a9-8cd7-1d07123caf1b)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/4254d5c3-9807-42e4-b3bc-e4c4278f95ed)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/1a4e7a1d-7e6a-4706-ad5a-25aed3c0a808)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/076df1b9-76c8-4c42-a124-7fd3b20dc1a3)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/716d51c9-1d0a-4e60-8129-dc59a6ab67c1)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/241990eb-98fe-41b1-991d-7de041ffc815)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/0993c9ef-c4a4-4ae5-8377-f5e61ad3ae8f)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/80c49f31-3bc8-4b5e-97ef-25b7a2280d7c)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/6c11f282-ced9-45d7-aa63-12ac9d6f721e)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/89d35305-8f78-443f-b5c0-f5c028a29a5c)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/d72b15fe-9cc9-4305-8102-5d454a0362ed)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/2202a736-589c-4eb7-b523-3a9b125219a8)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/57607f8a-f34f-4d7b-bc09-1c8d4847d921)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/7be53b59-6bcf-4c3c-8d24-820a4a297a61)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/0a0ad58a-e53f-4b70-a54e-6ee997931645)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/6292ef10-ffc0-4a9d-92b2-5cacd3a5538d)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/e2d5895e-e3d4-42d0-abde-6e797622e2be)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/605f3f54-772c-45eb-aa61-f408178ff429)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/0bc3b171-6762-41c6-a525-468a4b205afc)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/a371c051-ceab-4e08-ac02-2f925c40b6fe)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/6335d4b5-56c8-4a1d-8b68-46124477dcbd)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/2b898831-b475-40b7-b7c7-e6d58b7cd882)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/3b71d5f3-6063-4bcb-b5db-6781f9c36c92)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/6663d25d-8029-45fc-94a0-e115a54c9062)
<br>
<br>
<h3>Firebase Structure</h3><br>
<br>
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/61d9c436-f37d-491a-80f3-844f5311180f)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/dc6d59fa-edfd-463f-b6ba-b9df763815a1)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/f83333f8-7d3c-4fe4-9b94-53983700b1d5)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/57b4b9bf-97f0-4b3c-959d-05b9d24df86c)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/dfccef25-c84c-411c-9ae6-1889838ea18e)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/1c985ac8-fbf0-4e09-b199-7189249dba50)
![image](https://github.com/raushanvivek9/AnnotateMe/assets/49828254/fa03c644-ea5a-4d3e-aa6a-e4089ffc1748)





















































