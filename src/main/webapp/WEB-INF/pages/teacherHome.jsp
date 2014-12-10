<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- Home page for Teachers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Teacher Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="teacherHome">
        <%@include file="include/header.jsp"%>
        <h1>Your Home Page</h1>
        <div id="actions">
            <h2>Actions</h2>
            <ul>
                <li><a href="createEvent.htm">Create new class</a></li>
                <li><a href="editPersonalData.htm">Edit my Data</a></li>
            </ul>
        </div>
        <div id="events">
            <h2>My Classes</h2>
            <table id="eventTable" class="displayTable">
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Time</th>
                        <th>Grade</th>
                        <th>Students</th>
                        <th>Volunteer</th>
                        <th>Bank</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="event" items="${events}">
                        <tr>
                            <td><c:out value="${event.eventDate}"/></td>
                            <td><c:out value="${event.eventTime}"/></td>
                            <td><c:out value="${event.grade}"/></td>
                            <td><c:out value="${event.numberStudents}"/></td>
                            <td>
                                <c:out value="${event.linkedVolunteer.firstName}" default="no volunteer"/>
                                <c:out value="${event.linkedVolunteer.lastName}" default=""/>
                            </td>
                            <td><c:out value="${event.linkedVolunteer.linkedBank.bankName}" default=""/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <%@include file="include/footer.jsp"%>
    </body>
</html>
