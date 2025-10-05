<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Volunteer Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="volunteerHome" class="volunteerHome">

        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">

		        <h1>Your Home Page</h1>

				<c:if test="${not volunteerSignupsOpen}">
					<div class="importantMessage">
						Signups for classes will be available soon.
					</div>
				</c:if>

		        <div class="actions">
		        
		            <h2>Actions</h2>
		         
		            <ul class="noUl">
						<c:if test="${volunteerSignupsOpen}">
							<li class="mb1">
									<c:choose>
										<c:when test="${!(sessionData.volunteer.getApprovalStatus() == ApprovalStatus.Suspended)}">
											<button onclick="js.loadURL('eventRegistration.htm');" class="editOrRegister">
												Register for a class
											</button>
										</c:when>
										<c:otherwise>
											<div>The administrator for your bank has suspended your account. You
											can not volunteer for classes at this time.</div>
										</c:otherwise>
									</c:choose>
							</li>
						</c:if>
		                <li class="mb1">
		                	<button onclick="js.loadURL('editVolunteerPersonalData.htm');" class="editOrRegister">
		                		Edit my account
		                	</button>
		                </li>
		            </ul>
		        </div>

				<c:if test="${showDocuments}">
					<div class="documents">
						<strong>Important documents</strong>
						<ul>
							<c:forEach var="docName" items="${volunteerDocs}">
								<li><a href="${s3Util.makeS3URL(docName)}" target="_blank">${docName}</a></li>
							</c:forEach>
						</ul>
					</div>
				</c:if>

		        <div id="events">
	
		            <h2>My Classes</h2>

		            <table id="eventTable" class="displayTable responsive">

		                <thead>
		                    <tr>
		                        <th scope="col">Date</th>
		                        <th scope="col">Time</th>
		                        <th scope="col">School</th>
		                        <th scope="col">Teacher</th>
		                        <c:if test="${showGradeColumn}">
		                            <th scope="col" class="center">Grade</th>
		                        </c:if>
								<c:if test="${showDeliveryMethodColumn}">
								    <th scope="col">Delivery Method</th>
								</c:if>
		                        <th scope="col" class="center">Students</th>
                                <c:if test="${bank.minLMIForCRA != null}">
                                    <th scope="col">CRA</th>
                                </c:if>
                                <th scope="col"><span class="ada-read">Column of Details buttons</span></th>
                                <th scope="col"><span class="ada-read">Column of Delete buttons</span></th>
		                    </tr>
		                </thead>
		                <tbody>
                            <c:if test="${empty events}">
                                <td colspan="8" class="emptyTableMessage">You have not signed up for any classes yet.</td>
                            </c:if>
		                    <c:forEach var="event" items="${events}">
		                        <tr>

		                            <td data-title="Date"><c:out value="${event.eventDate.pretty}"/></td>
		                            <td data-title="Time"><c:out value="${event.eventTime}"/></td>
		                            <td data-title="School"><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>
		                            <td data-title="Teacher"><c:out value="${event.linkedTeacher.firstName}"/> <c:out value="${event.linkedTeacher.lastName}"/></td>
		                            <c:if test="${showGradeColumn}">
		                                <td data-title="Grade" class="center"><c:out value="${event.grade}"/></td>
		                            </c:if>
									<c:if test="${showDeliveryMethodColumn}">
									    <td data-title="Delivery Method"><c:out value="${event.deliveryMethod}"/></td>
									</c:if>
		                            <td data-title="Students" class="center"><c:out value="${event.numberStudents}"/></td>
                                    <c:if test="${bank.minLMIForCRA != null}">
                                        <td data-title="CRA">
                                            <c:choose>
                                                <c:when test="${event.linkedTeacher.linkedSchool.lmiEligible >= bank.minLMIForCRA}">CRA eligible</c:when>
                                                <c:otherwise>Not eligible</c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:if>
                                    <td class="action">
<%--=======--%>
		                            <%--<td data-title="Date"><c:out value="${event.eventDate.pretty}"/></td>--%>
		                            <%--<td data-title="Time"><c:out value="${event.eventTime}"/></td>--%>
		                            <%--<td data-title="School"><c:out value="${event.linkedTeacher.linkedSchool.name}"/></td>--%>
		                            <%--<td data-title="Teacher"><c:out value="${event.linkedTeacher.firstName}"/> <c:out value="${event.linkedTeacher.lastName}"/></td>--%>
		                            <%--<td class="center" data-title="Grade"><c:out value="${event.grade}"/></td>--%>
		                            <%--<td class="center" data-title="Students"><c:out value="${event.numberStudents}"/></td>--%>
                                    <%--<td class="action">--%>
<%-->>>>>>> Stashed changes--%>
                                        <form action="eventDetails.htm" method="POST">
                                            <input type="hidden" name="eventId" value="<c:out value="${event.eventId}"/>"/>
                                            <input type="hidden" name="doneURL" value="volunteerHome.htm"/>
                                            <button class="editOrRegister details" type="submit">Details</button>
                                        </form>
                                    </td>
                                    <td class="action">
										<button onclick="js.loadURL('volunteerWithdraw.htm?eventId=<c:out value="${event.eventId}"/>');" class="editOrRegister delete">
											Delete
										</button>
									</td>
		                        </tr>
								<tr>
									<c:if test ="${not empty event.notes}">
										<td colspan="10" class =  "italic">Class Note:  <c:out value="${event.notes}"/></td>
									</c:if>
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