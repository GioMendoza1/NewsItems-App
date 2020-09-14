# NewsItemsApp

## About The Project
The NewsItemsApp is a web application that scrapes articles from specified sections of Time.com and renders it into Bulma CSS cards. The app consists of a VueJS front-end and a Java Servlet back-end. Both are run on seperate Tomcat servers and communicate through HTTP get requests. Upon the first get request, each article's information is converted into a NewsItem object and added to a list. The list is sorted and converted into a JSON so that it can be sent and retrieved by the front end. The list is cached for a quick response in future requests.

### Features
* Clicking on a news item will open the corresponding Time.com article in a new tab
* By default, each news item is sorted based on the time it is published (most recent article first, second most recent article after, etc.)
* News items can be filtered by category (Tech, Business, etc.) through a drop-down list
* News items can be filtered by title or excerpt through an input field

### Built With
* [HTML/CSS](https://html.spec.whatwg.org/multipage/)
* [VueJS](https://vuejs.org/)
* [Bulma](https://bulma.io/)
* [Java Servlet](https://docs.oracle.com/cd/E17802_01/products/products/servlet/2.5/docs/servlet-2_5-mr2/javax/servlet/package-summary.html)
* [Magnolia CMS](https://www.magnolia-cms.com/)

### File Structure
* HTML/VueJS code is found [here](https://github.com/GioMendoza1/NewsItemsApp/blob/master/hello-magnolia/templates/components/newsItems.ftl)
* CSS is found [here](https://github.com/GioMendoza1/NewsItemsApp/tree/master/hello-magnolia/webresources/css)
* Java code is found [here](https://github.com/GioMendoza1/NewsItemsApp/tree/master/JavaServlet/src/main/java)
* Images of the design are found [here](https://github.com/GioMendoza1/NewsItemsApp/tree/master/images)

<!-- CONTACT -->
## Contact
Giovannie Mendoza - giovanniemendoza1@gmail.com

Project Link: [https://github.com/GioMendoza1/NewsItemsApp](https://github.com/GioMendoza1/NewsItemsApp)

<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [Tomcat](https://tomcat.apache.org/)
* [Maven](https://maven.apache.org/)
* [Jsoup](https://jsoup.org/)
* [Gson](https://github.com/google/gson)
* [Axios](https://www.npmjs.com/package/axios)
* [Docker](https://www.docker.com/)
