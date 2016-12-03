<%-- Home page for Site Admins --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en-US">
    <head>
        <title>Teach Children to Save - Site Administrator Home</title>
        <%@include file="include/commonHead.jsp"%>
    </head>
    <body id="siteAdminHome" class="siteAdminHome">
    
        <a href="#main" class="ada-read">Skip to main content</a>

        <%@include file="include/header.jsp" %>

        <div class="mainCnt">

        <%@include file="include/navigation.jsp" %>

            <main id="main">


		        <h1>
		        	Admin Home Page
		        </h1>
	        
	            <h2>Actions</h2>
	         
	            <ul class="noUl">
	                <li class="mb1">
	                	<button onclick="js.loadURL('viewEditBanks.htm');" class="editOrRegister">
	                		Add/Remove/Edit Banks
	                	</button>
                	</li>
                	
	                <li class="mb1">
	                	<button onclick="js.loadURL('schools.htm');" class="editOrRegister">
	                		Add/Remove/Edit Schools
	                	</button>
	                </li>

	                <li class="mb1">
	                	<button onclick="js.loadURL('teachers.htm');" class="editOrRegister">
	                		Remove/Edit Teachers
	                	</button>
	                </li>
	                
	                <li class="mb1">
	                	<button onclick="js.loadURL('volunteers.htm');" class="editOrRegister">
	                		Remove/Edit/SignUp <br> Volunteers and Bank Admin
	                	</button>
	                </li>

                    <li class="mb1">
                        <button onclick="js.loadURL('viewEditEvents.htm');" class="editOrRegister">
                            List/Create Classes
                        </button>
                    </li>

                    <li class="mb1">
                        <button onclick="js.loadURL('listAllowedDates.htm');" class="editOrRegister">
                            Add/Remove Event Dates
                        </button>
                    </li>

                    <li class="mb1">
                        <button onclick="js.loadURL('listAllowedTimes.htm');" class="editOrRegister">
                            Add/Remove Event Times
                        </button>
                    </li>

					<li class="mb1">
						<button onclick="js.loadURL('viewSiteSettings.htm');" class="editOrRegister">
						    View/Edit Site Settings
						</button>
					</li>

                    <li class="mb1">
                        <button onclick="js.loadURL('adminViewStatistics.htm');" class="editOrRegister">
                            View Program Statistics
                        </button>
                    </li>

                    <li class="mb1">
	                	<button onclick="js.loadURL('emailAnnouncement.htm');" class="editOrRegister">
	                		Send Email Announcement
	                	</button>
	                </li>
	                
	                <li class="mb1">
	                	<button onclick="js.loadURL('editPersonalData.htm');" class="editOrRegister">
	                		Edit my Account
	                	</button>
	                </li>
	            </ul>

		
			</main>

		</div><%-- mainCnt --%>	

        <%@include file="include/footer.jsp"%>

    </body>
</html>