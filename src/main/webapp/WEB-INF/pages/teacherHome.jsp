<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- Home page for Teachers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Teacher Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="teacherHome" class="teacherHome">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">   

		        <h1>
		        	Your Home Page
		        </h1>


				<div class="actions">
					<h2>Actions</h2>

						<ul class="noUl">
							<li class="mb1">
								<button onclick="js.loadURL('createEvent.htm');" class="editOrRegister">
									Create new class
								</button>
							</li>
							<li class="mb1">
								<button onclick="js.loadURL('editPersonalData.htm');" class="editOrRegister">
									Edit my Account
								</button>
							</li>
						</ul>
					</div>


					<div class="documents">
						<strong>Important documents</strong>
			        	<%
			        	String helpfulHints = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/Helpful Hints 2015.pub";
			        	String letterToTeacher = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/eacher/2015 Letter to teachers.pub";
			        	String volunteerGuideline = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/Volunteer Guidelines 2015.pub";
			        	%>

						<ul>
							<li>
								<a href="<%=helpfulHints%>">Helpful Hints 2015</a>
							</li>

							<li>
								<a href="<%=letterToTeacher%>">Letters to Teachers</a>
							</li>

							<li>
								<a href="<%=helpfulHints%>">Volunteers Guidelines</a>
							</li>
						</ul>

		       	 	</div>

		        <div id="events">
		            <h2>My Classes</h2>
		            <table id="eventTable" class="responsive">
		                <thead>
		                    <tr>
		                        <th scope="col">Date</th>
		                        <th scope="col">Time</th>
		                        <th scope="col" class="center">Grade</th>
		                        <th scope="col" class="center">Students</th>
		                        <th scope="col">Volunteer</th>
		                        <th scope="col">Bank</th>
                                <th scope="col"><span class="ada-read">Column of Details buttons</span></th>
                                <th scope="col"><span class="ada-read">Column of Delete buttons</span></th>
		                    </tr>
		                </thead>
		                <tbody>
                            <c:if test="${empty events}">
                                <td colspan="8" class="emptyTableMessage">You have not requested a volunteer for any classes yet.</td>
                            </c:if>
		                    <c:forEach var="event" items="${events}">
		                        <tr>
		                            <td data-title="Date"><c:out value="${event.eventDate.pretty}"/></td>
		                            <td data-title="Time"><c:out value="${event.eventTime}"/></td>
		                            <td class="center" data-title="Grade"><c:out value="${event.grade}"/></td>
		                            <td class="center" data-title="Students"><c:out value="${event.numberStudents}"/></td>
		                            <td data-title="Volunteer">
		                                <c:out value="${event.linkedVolunteer.firstName}" default="no volunteer"/>
		                                <c:out value="${event.linkedVolunteer.lastName}" default=""/>
		                            </td>
		                            <td data-title="Bank"><c:out value="${event.linkedVolunteer.linkedBank.bankName}" default=""/></td>
                                    <td class="action">
                                        <form action="eventDetails.htm" method="POST">
                                            <input type="hidden" name="eventId" value="<c:out value="${event.eventId}"/>"/>
                                            <input type="hidden" name="doneURL" value="teacherHome.htm"/>
                                            <button class="editOrRegister details" type="submit">Details</button>
                                        </form>
                                    </td>
                                    <td class="action">
										<button onclick="js.loadURL('teacherCancel.htm?eventId=<c:out value="${event.eventId}"/>');" class="editOrRegister delete">
											Delete
										</button>
										<%--<a href="teacherCancel.htm?eventId=<c:out value="${event.eventId}"/>">cancel</a>--%>
									</td>
		                        </tr>
		                    </c:forEach>
		                </tbody>
		            </table>
		        </div>

			</main>

		</div><%-- mainCnt --%>			        
		       
        <%@include file="include/footer.jsp"%>

    </body>
</html>