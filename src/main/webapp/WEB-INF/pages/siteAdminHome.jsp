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
    <div class="qa-notes">This page is a stub with fixed data</div>
    <div id="actions">
        <h2>Actions</h2>
        <ul>
            <li><a href="adminEditBanks.htm">Add/Remove/Edit Banks</a></li>
            <li><a href="adminEditSchools.htm">Add/Remove/Edit Schools</a></li>
            <li><a href="adminEditTeachers.htm">Remove/Edit Teachers</a></li>
            <li><a href="adminEditVolunteers.htm">Remove/Edit Volunteers</a></li>
            <li><a href="adminListTeachers.htm">List Teachers</a></li>
            <li><a href="adminListVolunteers.htm">List Volunteers</a></li>
            <li><a href="adminListClasses.htm">List Classes</a></li>
            <li><a href="adminSendEmailAnnounce.htm">Send Email Announcement</a></li>
            <li><a href="editData.htm">Edit my Data</a></li>
        </ul>
    </div>
</body>
</html>
