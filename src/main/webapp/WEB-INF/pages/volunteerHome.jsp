<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Volunteer Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="volunteerHome" class="volunteerHome">

        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

	
		        <h1>Your Home Page</h1>

		        <div id="actions">
		        
		            <h2>Actions</h2>
		         
		            <ul class="noUl">
		                <li class="mb1">
		                	<button onclick="js.loadURL('eventRegistration.htm');" class="editOrRegister">
		                		Register for a class
		                	</button>
		                </li>
		                
		                <li class="mb1">
		                	<button onclick="js.loadURL('editPersonalData.htm');" class="editOrRegister">
		                		Edit my account
		                	</button>
		                </li>
		            </ul>
		        </div>
	
		        <div id="events">
	
		            <h2>My Classes</h2>
	
		            <table id="eventTable" class="displayTable">
		                <thead>
		                    <tr>
		                        <th scope="col">Date</th>
		                        <th scope="col">Time</th>
		                        <th scope="col">School</th>
		                        <th scope="col">Teacher</th>
		                        <th scope="col" class="center">Grade</th>
		                        <th scope="col" class="center">Students</th>
                                <th scope="col">
									<span class="ada-read">Column of Delete buttons</span>
								</th>
		                    </tr>
		                </thead>
		                <tbody>
		                    <c:forEach var="event" items="${events}">
		                        <tr>
		                            <td><c:out value="${event.eventDate}"/></td>
		                            <td><c:out value="${event.eventTime}"/></td>
		                            <td><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
		                            <td><c:out value="${event.linkedTeacher.firstName}"/> <c:out value="${event.linkedTeacher.lastName}"/></td>
		                            <td class="center"><c:out value="${event.grade}"/></td>
		                            <td class="center"><c:out value="${event.numberStudents}"/></td>
                                    <td>
										<button onclick="js.loadURL('volunteerWithdraw.htm?eventId=<c:out value="${event.eventId}"/>');" class="editOrRegister delete">
											Delete
										</button>
										<%--<a href="volunteerWithdraw.htm?eventId=<c:out value="${event.eventId}"/>">withdraw</a>--%>
									</td>
		                        </tr>
		                    </c:forEach>
		                </tbody>
		            </table>
		        </div><%-- #events --%>
		  </main> 

        </div><%-- .mainCnt --%>		  
		  
        <%@include file="include/footer.jsp"%>
    </body>
</html>