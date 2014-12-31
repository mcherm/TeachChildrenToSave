<%-- Home page for Volunteers --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Teach Children to Save - Bank Administrator Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="bankAdminHome" class="bankAdminHome">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <div class="decor"></div>

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
		                	<button onclick="js.loadURL('registerForEvent.htm');" class="editOrRegister">
		                		Register volunteers for classes
		                	</button>
	                	</li>
		                <li class="mb1">
		                	<button onclick="js.loadURL('manageVolunteers.htm');" class="editOrRegister">
		                		Manage my bank's volunteers
		                	</button>
	                	</li>
		                <li class="mb1">
		                	<button onclick="js.loadURL('editPersonalData.htm');" class="editOrRegister">
			                	Edit my Account
		                	</button>
	                	</li>
		            </ul>
		        </div>

		        <div id="volunteers">
		            <h2>Unapproved Volunteers</h2>
		            <table id="unapprovedVolunteersTable">
		                <thead>
		                    <tr>
		                        <th>First Name</th>
		                        <th>Last Name</th>
		                        <th>Email</th>
		                        <th>Action</th>
		                    </tr>
		                </thead>
		                <tbody>
		                    <c:forEach var="volunteer" items="${volunteers}">
		                        <c:if test="${!volunteer.approved}">
		                            <tr>
		                                <td><c:out value="${volunteer.firstName}"/></td>
		                                <td><c:out value="${volunteer.lastName}"/></td>
		                                <td><c:out value="${volunteer.email}"/></td>
		                                <td>
											<button onclick="js.loadURL('approveVolunteer.htm?volunteerId=<c:out value="${volunteer.userId}"/>');" class="editOrRegister delete">
												Decline
											</button>
											<%--<a href="approveVolunteer.htm?volunteerId=<c:out value="${volunteer.userId}"/>">Approve</a>--%>
										</td>
		                            </tr>
		                        </c:if>
		                    </c:forEach>
		                </tbody>
		            </table>
		
		            <h2>Approved Volunteers</h2>
		            
		            <table id="approvedVolunteersTable">
		                <thead>
		                    <tr>
		                        <th>First Name</th>
		                        <th>Last Name</th>
		                        <th>Email</th>
		                        <th>Classes Registered</th>
		                    </tr>
		                </thead>
		                <tbody>
		                <c:forEach var="volunteer" items="${volunteers}">
		                    <c:if test="${volunteer.approved}">
		                        <tr>
		                            <td><c:out value="${volunteer.firstName}"/></td>
		                            <td><c:out value="${volunteer.lastName}"/></td>
		                            <td><c:out value="${volunteer.email}"/></td>
		                            <td><div class="qa-notes">not implemented yet</div></td>
		                        </tr>
		                    </c:if>
		                </c:forEach>
		                </tbody>
		            </table>
		        </div>
		        
			</main>

		</div><%-- mainCnt --%>			        
		        
        <%@include file="include/footer.jsp"%>
    </body>
</html>