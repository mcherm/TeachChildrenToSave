<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Bank Administrator Home</title>
        <%@include file="include/commonHead.jsp"%>
        <script src="<c:url value="/tcts/js/jquery-1.11.1.min.js" />"></script>
        <script type="text/javascript">
            $(document).ready(function() {
                $('div.volunteerClasses').each( function() {
                    var fields = {volunteerId: $(this).attr('id').substring(19)};
                    $(this).load('bankAdminHomeDetail.htm', fields);
                });
            });
        </script>
    </head>
    <body id="bankAdminHome" class="bankAdminHome">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">   
		
		        <h1>Your Home Page</h1>

		        <div id="actions" class="actions">
		            
		            <h2>
		            	Actions
	            	</h2>
		            
		            <ul class="noUl">
		                <li class="mb1">
		                	<button onclick="js.loadURL('editPersonalData.htm');" class="editOrRegister">
			                	Edit my Account
		                	</button>
	                	</li>
		            </ul>

                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('editBank.htm?bankId=<c:out value="${bank.bankId}"/>');" class="editOrRegister">
                                Edit Bank Settings
                            </button>
                        </li>
                    </ul>
		        </div>

                <div class="documents">
                    <strong>Important documents</strong>
                    <%
                        String helpfulHints = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/Helpful%20Hints%202015.pdf";
                        String volunteerGuideline = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/Volunteer%20Guidelines%202015.pdf";
                        String certificateOfRecognition = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/2015%20TCTSD%20Certificate.pdf";
                        String lessonHandout = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/TCTSD%2015%20Lesson_Handout_Answer%20Key.pdf";
                        String faq = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/volunteer/TCTSD%20FAQs.pdf";
                    %>

                    <ul>
                        <li><a href="<%=helpfulHints%>" target="_blank">Helpful Hints 2015</a></li>
                        <li><a href="<%=volunteerGuideline%>" target="_blank">Volunteers Guidelines</a></li>
                        <li><a href="<%=certificateOfRecognition%>" target="_blank">Certificate of Recognition</a></li>
                        <li><a href="<%=lessonHandout%>" target="_blank">Lesson Handout</a></li>
                        <li><a href="<%=faq%>" target="_blank">FAQs</a></li>
                    </ul>
                </div>


                <div id="volunteers">

                <c:if test="${not empty newVolunteers}">
                    <h2>New Volunteers</h2>
                    <div>When you've verfied a new user is valid, please approve the user.  This will place it on the approved list.  New users and approved users can sign up for classes normally.  However if you suspend a user that user will be unsigned up from any classes they had previously signed up for and be prevented from signing up for any classes in the future unless they are reinstated by you.</div>
                    <table id="newVolunteersTable" class="responsive">
                        <thead>
                            <tr>
                                <th>First Name</th>
                                <th>Last Name</th>
                                <th>Email</th>
                                <th>
                                    <div>Classes Registered</div>
                                    <table>
                                        <tr>
                                            <th>Date</th>
                                            <th>Time</th>
                                            <th>Teacher</th>
                                            <th>Grade</th>
                                            <th>Students</th>
                                            <c:if test="${bank.minLMIForCRA != null}">
                                                <th scope="col">CRA</th>
                                            </c:if>
                                        </tr>
                                    </table>
                                </th>
                                <th scope="col">Approve<span class="ada-read">Column of Approve buttons</span></th>
                                <th scope="col">Suspend<span class="ada-read">Column of Suspend buttons</span></th>
                            </tr>
                        </thead>
                        <tbody>
                           <c:forEach var="volunteer" items="${newVolunteers}">
                                <tr>
                                    <td data-title="First Name"><c:out value="${volunteer.firstName}"/></td>
                                    <td data-title="Last Name"><c:out value="${volunteer.lastName}"/></td>
                                    <td data-title="Email"><c:out value="${volunteer.email}"/></td>
                                    <td data-title="Classes Registered">
                                        <div class="volunteerClasses" id="volunteerClassesFor<c:out value="${volunteer.userId}"/>">
                                            <!-- populated by javascript -->
                                            <em>loading...</em>
                                        </div>
                                    </td>
                                    <td class="action">
                                        <form method="POST" action="approveVolunteer.htm">
                                            <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                            <button type="submit" class="editOrRegister delete">Approve</button>
                                        </form>
                                    </td>
                                    <td class="action">
                                        <form method="POST" action="suspendVolunteer.htm">
                                            <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                            <button type="submit" class="editOrRegister delete">Suspend</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                    <h2>Approved Volunteers</h2>
		            
		            <table id="normalVolunteersTable" class="responsive">
		                <thead>
		                    <tr>
		                        <th>First Name</th>
		                        <th>Last Name</th>
		                        <th>Email</th>
		                        <th>
                                    <div>Classes Registered</div>
                                    <table>
                                        <tr>
                                            <th>Date</th>
                                            <th>Time</th>
                                            <th>Teacher</th>
                                            <th>Grade</th>
                                            <th>Students</th>
                                            <c:if test="${bank.minLMIForCRA != null}">
                                                <th scope="col">CRA</th>
                                            </c:if>
                                        </tr>
                                    </table>
                                </th>
                                <th scope="col">Undo Approval<span class="ada-read">Column of Undo Approval buttons</span></th>
                                <th scope="col">Suspend<span class="ada-read">Column of Suspend buttons</span></th>
		                    </tr>
		                </thead>
		                <tbody>
                            <c:if test="${empty normalVolunteers}">
                                <td colspan="5" class="emptyTableMessage">There are currently no volunteers from your bank.</td>
                            </c:if>
                            <c:forEach var="volunteer" items="${normalVolunteers}">
                                <tr>
                                    <td data-title="First Name"><c:out value="${volunteer.firstName}"/></td>
                                    <td data-title="Last Name"><c:out value="${volunteer.lastName}"/></td>
                                    <td data-title="Email"><c:out value="${volunteer.email}"/></td>
                                    <td data-title="Classes Registered">
                                        <div class="volunteerClasses" id="volunteerClassesFor<c:out value="${volunteer.userId}"/>">
                                            <!-- populated by javascript -->
                                            <em>loading...</em>
                                        </div>
                                    </td>
                                    <td class="action">
                                        <form method="POST" action="unApproveVolunteer.htm">
                                            <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                            <button type="submit" class="editOrRegister delete">Undo Approve</button>
                                        </form>
                                    </td>
                                    <td class="action">
                                        <form method="POST" action="suspendVolunteer.htm">
                                            <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                            <button type="submit" class="editOrRegister delete">Suspend</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
		                </tbody>
		            </table>

                    <c:if test="${not empty suspendedVolunteers}">
                        <h2>Suspended Volunteers</h2>
                        <table id="suspendedVolunteersTable" class="responsive">
                            <thead>
                                <tr>
                                    <th>First Name</th>
                                    <th>Last Name</th>
                                    <th>Email</th>
                                    <th scope="col"><span class="ada-read">Column of Reinstate buttons</span></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="volunteer" items="${suspendedVolunteers}">
                                    <tr>
                                        <td data-title="First Name"><c:out value="${volunteer.firstName}"/></td>
                                        <td data-title="Last Name"><c:out value="${volunteer.lastName}"/></td>
                                        <td data-title="Email"><c:out value="${volunteer.email}"/></td>
                                        <td class="action">
                                            <form method="POST" action="reinstateVolunteer.htm">
                                                <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                                <button type="submit" class="editOrRegister delete">Reinstate</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

			</main>

		</div><%-- mainCnt --%>			        
		        
        <%@include file="include/footer.jsp"%>
    </body>
</html>
