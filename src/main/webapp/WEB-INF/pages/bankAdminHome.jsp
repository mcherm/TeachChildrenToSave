<%-- Home page for  Bank Admin --%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
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
		                	<button onclick="js.loadURL('editVolunteerPersonalData.htm');" class="editOrRegister">
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
                    <ul class="noUl">
                        <li class="mb1">
                            <button onclick="js.loadURL('eventRegistration.htm');" class="editOrRegister">
                                My Own Volunteering
                            </button>
                        </li>
                    </ul>

                </div>

                <c:if test="${showDocuments}">
                    <c:if test="${not empty volunteerDocs}">
                        <div class="documents">
                            <strong>Volunteer documents</strong>
                            <ul>
                                <c:forEach var="docName" items="${volunteerDocs}">
                                    <li><a href="${s3Util.makeS3URL(docName)}" target="_blank">${docName}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                    <c:if test="${not empty bankAdminDocs}">
                        <div class="documents">
                            <strong>BankAdmin documents</strong>
                            <ul>
                                <c:forEach var="docName" items="${bankAdminDocs}">
                                    <li><a href="${s3Util.makeS3URL(docName)}" target="_blank">${docName}</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </c:if>
                </c:if>


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
                                            <th>School</th>
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
                                            <button type="submit" class="editOrRegister smallButton">Approve</button>
                                        </form>
                                    </td>
                                    <td class="action">
                                        <form method="POST" action="suspendVolunteer.htm">
                                            <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                            <button onclick="return confirm('This will remove the volunteer from any classes she has signed up for.  Are you sure you want to suspend volunteer?' );"
                                                    type="submit" class="editOrRegister delete smallButton">Suspend</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                    <h2>Approved Volunteers</h2>
                    <a download="bankvolunteers.xls" href="excel/bankVolunteers.htm" class="downloadExcel">Export to Excel</a>


                    <table id="normalVolunteersTable" class="responsive">
		                <thead>
		                    <tr>
		                        <th>First Name</th>
		                        <th>Last Name</th>
		                        <th>Email</th>
                                <c:if test="${not empty bank.bankSpecificDataLabel}">
                                    <th><c:out value="${bank.bankSpecificDataLabel}"/></th>
                                </c:if>
		                        <th>
                                    <div>Classes Registered</div>
                                    <table>
                                        <tr>
                                            <th>Date</th>
                                            <th>Time</th>
                                            <th>School</th>
                                            <th>Grade</th>
                                            <th>Students</th>
                                            <th>Teacher</th>
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
                                    <c:if test="${not empty bank.bankSpecificDataLabel}">
                                        <td data-title="<c:out value="${bank.bankSpecificDataLabel}"/>"><c:out value="${volunteer.bankSpecificData}"/></td>
                                    </c:if>
                                    <td data-title="Classes Registered">
                                        <div class="volunteerClasses" id="volunteerClassesFor<c:out value="${volunteer.userId}"/>">
                                            <!-- populated by javascript -->
                                            <em>loading...</em>
                                        </div>
                                    </td>
                                    <td class="action">
                                        <form method="POST" action="unApproveVolunteer.htm">
                                            <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                            <button type="submit" class="editOrRegister smallButton">Undo Approve</button>
                                        </form>
                                    </td>
                                    <td class="action">
                                        <form method="POST" action="suspendVolunteer.htm">
                                            <input type="hidden" name="volunteerId" value="<c:out value="${volunteer.userId}"/>"/>
                                            <button onclick="return confirm('This will remove the volunteer from any classes she has signed up for.  Are you sure you want to suspend volunteer?' );"
                                                    type="submit" class="editOrRegister delete smallButton">Suspend</button>
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
                                                <button type="submit" class="editOrRegister smallButton">Reinstate</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>

                <p>&nbsp;</p>

                <div class="withBorder">
                    <h2>Bank-Specific Field</h2>
                    <p>
                        If you want to collect extra information from volunteers from your bank, then fill in a label
                        here. Each volunteer from your bank will be presented with an extra text field to fill out
                        and you will be able to see their answers. For instance, if you needed to know the department
                        number for all of your bank's volunteers, you could write "Bank of Delaware Department Number"
                        below and each volunteer would be able to fill out that field. If there is no special
                        information that you need to collect from you bank's volunteers then simply leave this
                        field empty.
                    </p>
                    <div>
                        <form:form method="POST" action="setBankSpecificFieldLabel.htm" modelAttribute="formData">
                            <form:hidden path="bankId"/>
                            <div class="formElementCnt">
                                <label>
                                    <div class="inputCnt">
                                        <div class="info">
                                            Bank-Specific Field Label
                                        </div>
                                        <form:input path="bankSpecificFieldLabel"/>
                                    </div>
                                </label>
                            </div>
                            <button type="submit" class="editOrRegister">Update Label</button>
                        </form:form>
                    </div>
                </div>

			</main>

		</div><%-- mainCnt --%>

        <%@include file="include/footer.jsp"%>
    </body>
</html>
