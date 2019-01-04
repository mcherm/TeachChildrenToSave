<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en-US">
    <head>

        <title>Teach Children To Save - Delete School </title>
        <%@include file="include/commonHead.jsp"%>

    </head>
    <body class="schools">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

            <%@include file="include/navigation.jsp" %>

            <main id="main">

                <h1> Confirm Delete of ${school.name} </h1>

                <%@include file="include/errors.jsp"%>
                <c:if test="${not empty deleteSchoolFormData.teachers}">


                    <h2> For each teacher listed below belonging to ${school.name} you must choose to delete them or reassign them to a new school.  When you delete the school the teachers will be deleted or reassigned as indicated in the table.</h2>
                    <br>
                    <a download="teachers.xls" href="excel/deletedSchoolTeachers/<c:out value="${school.schoolId}"/>.htm" class="downloadExcel">Export to Excel</a>
                </c:if>
                <form:form method="POST" action="deleteSchool.htm" modelAttribute="deleteSchoolFormData">
                    <form:hidden path="schoolIdToBeDeleted"></form:hidden>
                        <c:if test="${not empty deleteSchoolFormData.teachers}">
                            <table id="teachersToBeDeletedTable" class="responsive">
                                <thead>
                                    <tr>
                                        <th scope="col">User ID</th>
                                        <th scope="col">Email</th>
                                        <th scope="col">First Name</th>
                                        <th scope="col">Last Name</th>
                                        <th scope="col">User Type</th>
                                        <th scope="col">Phone Number</th>
                                        <th scope ="col">Delete or Reassign school</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="teacher" items="${deleteSchoolFormData.teachers}" varStatus="status">
                                        <tr>
                                            <td data-title="User ID"><c:out value="${teacher.userId}"/></td>
                                            <td data-title="Email"><c:out value="${teacher.email}"/></td>
                                            <td data-title="First Name"><c:out value="${teacher.firstName}"/></td>
                                            <td data-title="Last Name"><c:out value="${teacher.lastName}"/></td>
                                            <td data-title="User Type"><c:out value="${teacher.userType.getDisplayName()}"/></td>
                                            <td data-title="Phone Number"><c:out value="${teacher.phoneNumber}"/></td>
                                         <td>
                                                <form:select path="teachers[${status.index}].schoolId">
                                                    <form:option value="deleteme" label="- DeleteThisTeacher -" />
                                                    <form:options items="${schools}" itemValue="schoolId" itemLabel="name" />
                                                </form:select>
                                                <form:hidden path="teachers[${status.index}].userId"></form:hidden>
                                            </td>

                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    <br>

                    <input class ="editOrRegister delete" type="submit" value="Delete School" >


                </form:form>
                <br>
                <button onclick="js.loadURL('schools.htm')" class="editOrRegister cancel">
                    Cancel
                </button>
            </main>

        </div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>

    </body>
</html>