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
    <div class="qa-notes">This page is a stub with fixed data</div>
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
                <tr>
                    <td>03/12</td>
                    <td>9:00</td>
                    <td>Henderson Elementary</td>
                    <td>Mr. Toady</td>
                    <td>3</td>
                    <td>16</td>
                </tr>
                <tr>
                    <td>03/12</td>
                    <td>2:00</td>
                    <td>St. Jude's</td>
                    <td>Mrs. Tennnison</td>
                    <td>3</td>
                    <td>19</td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
