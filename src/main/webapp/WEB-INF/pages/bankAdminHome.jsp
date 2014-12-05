<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Teach Children to Save - Bank Administrator Home</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body id="bankAdminHome">
    <%@include file="include/header.jsp"%>
    <h1>Your Home Page</h1>
    <div class="qa-notes">This page is a stub with fixed data</div>
    <div id="actions">
        <h2>Actions</h2>
        <ul>
            <li><a href="registerForEvent.htm">Register volunteers for classes</a></li>
            <li><a href="manageVolunteers.htm">Manage my bank's volunteers</a></li>
            <li><a href="editData.htm">Edit my Data</a></li>
        </ul>
    </div>
    <div id="volunteers">
        <h2>Unapproved Volunteers</h2>
        <table id="unapprovedVolunteersTable" class="displayTable">
            <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Patricia</td>
                    <td>Anderson</td>
                    <td>patricia.anderson@capitalone.com</td>
                    <td><a href="approveVolunteer.htm?volunteerId=8847">Approve</a></td>
                </tr>
                <tr>
                    <td>Arnold</td>
                    <td>Jameson</td>
                    <td>arnold.jameson@capitalone.com</td>
                    <td><a href="approveVolunteer.htm?volunteerId=8843">Approve</a></td>
                </tr>
            </tbody>
        </table>

        <h2>Approved Volunteers</h2>
        <table id="unapprovedVolunteersTable" class="displayTable">
            <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Email</th>
                    <th>Classes Registered</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Jennifer</td>
                    <td>Betts</td>
                    <td>jennifer.betts@capitalone.com</td>
                    <td>0</td>
                </tr>
                <tr>
                    <td>Christine</td>
                    <td>Ponce</td>
                    <td>christine.ponce@capitalone.com</td>
                    <td>0</td>
                </tr>
                <tr>
                    <td>Herman</td>
                    <td>Ellis</td>
                    <td>herman.ellis@capitalone.com</td>
                    <td>2</td>
                </tr>
                <tr>
                    <td>Robert</td>
                    <td>Rodgers</td>
                    <td>bob.rodgers@capitalone.com</td>
                    <td>0</td>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
