<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Teach Children to Save - Volunteer Home</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body id="volunteerHome">
    <%@include file="include/header.jsp"%>
    <h1>Your Home Page</h1>
    <div id="actions">
        <h2>Actions</h2>
        <ul>
            <li><a href="registerForEvent.htm">Register for an Event</a></li>
            <li><a href="editData.htm">Edit my Data</a></li>
        </ul>
    </div>
    <div id="events">
        <h2>My Classes</h2>
        <table id="eventTable" class="displayTable">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Time</th>
                    <th>School</th>
                    <th>Teacher</th>
                    <th>Grade</th>
                    <th>Students</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="event" items="${events}">
                    <tr>
                        <td><c:out value="${event.eventDate}"/></td>
                        <td><c:out value="${event.eventTime}"/></td>
                        <td><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
                        <td><c:out value="${event.linkedTeacher.firstName}"/> <c:out value="${event.linkedTeacher.lastName}"/></td>
                        <td><c:out value="${event.grade}"/></td>
                        <td><c:out value="${event.numberStudents}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>
