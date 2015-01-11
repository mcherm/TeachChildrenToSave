<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
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

		        <div id="actions">
		            
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
		        </div>

		        <div id="volunteers">
		            <h2>Volunteers</h2>
		            
		            <table id="normalVolunteersTable">
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
                                        </tr>
                                    </table>
                                </th>
                                <th scope="col"><span class="ada-read">Column of Suspend buttons</span></th>
		                    </tr>
		                </thead>
		                <tbody>
                            <c:if test="${empty normalVolunteers}">
                                <td colspan="5" class="emptyTableMessage">There are currently no volunteers from your bank.</td>
                            </c:if>
                            <c:forEach var="volunteer" items="${normalVolunteers}">
                                <tr>
                                    <td><c:out value="${volunteer.firstName}"/></td>
                                    <td><c:out value="${volunteer.lastName}"/></td>
                                    <td><c:out value="${volunteer.email}"/></td>
                                    <td>
                                        <div class="volunteerClasses" id="volunteerClassesFor<c:out value="${volunteer.userId}"/>">
                                            <!-- populated by javascript -->
                                            <em>loading...</em>
                                        </div>
                                    </td>
                                    <td>
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
                        <h2>Unapproved Volunteers</h2>
                        <table id="suspendedVolunteersTable">
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
                                        <td><c:out value="${volunteer.firstName}"/></td>
                                        <td><c:out value="${volunteer.lastName}"/></td>
                                        <td><c:out value="${volunteer.email}"/></td>
                                        <td>
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
