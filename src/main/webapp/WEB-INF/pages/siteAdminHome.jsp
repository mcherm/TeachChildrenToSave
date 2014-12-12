<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Site Administrator Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="siteAdminHome">
        <%@include file="include/header.jsp"%>
        <h1>Admin Home Page</h1>
        <div id="actions">
            <h2>Actions</h2>
            <ul>
                <li><a href="bank/banks.htm">Add/Remove/Edit Banks</a></li>
                <li><a href="school/schools.htm">Add/Remove/Edit Schools</a></li>
                <li><a href="teacher/teachers.htm">Remove/Edit Teachers</a></li>
                <li><a href="volunteer/volunteers.htm">Remove/Edit Volunteers</a></li>
                <li><a href="teacher/teachers.htm">List Teachers</a></li>
                <li><a href="volunteer/volunteers.htm">List Volunteers</a></li>
                <li><a href="class/classes.htm">List Classes</a></li>
                <li><a href="adminSendEmailAnnounce.htm">Send Email Announcement</a></li>
                <li><a href="adminEditAllowedDates.htm">Add/Remove Event Dates</a></li>
                <li><a href="adminEditAllowedTimes.htm">Add/Remove Event Times</a></li>
                <li><a href="editPersonalData.htm">Edit my Data</a></li>
            </ul>
        </div>
        <%@include file="include/footer.jsp"%>
    </body>
</html>
