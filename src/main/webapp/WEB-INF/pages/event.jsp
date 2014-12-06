<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Teach Children To Save - Event registration</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body>
<%@include file="include/header.jsp"%>
<h2>Event Information</h2>
<form:form method="POST" action="addEvent.htm" modelAttribute="event">
   <div>
    <div class="row_div">
    	<div class="row_div_left">
	        <div class="caption_div"><form:label path="schoolID">School ID</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input path="schoolID" /></div>
       </div>
        <div class="row_div_right">
        	<div class="caption_div"><form:label path="teacherUserID">Teacher User ID</form:label></div>
        	<div class="field_div"><form:input path="teacherUserID" /></div>
        </div>
    </div>
    
    <div class="row_div">
    	<div class="row_div_left">
        	<div class="caption_div"><form:label path="volunteerUserID">Volunteer User ID</form:label></div>
        	<div class="field_div row_div_field_left_joint"><form:input path="volunteerUserID" /></div>
        </div>
    
	    <div class="row_div_right">
	        <div class="caption_div"><form:label path="grade">Grade</form:label></div>
	        <div class="field_div"><form:input path="grade" /></div>
	    </div>
    </div>
    
    <div class="row_div">
	    <div class="row_div_left">
	        <div class="caption_div"><form:label path="subject">Subject</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input path="subject" /></div>
	    </div>
	    
	    <div class="row_div_right">
	        <div class="caption_div"><form:label path="numStudents">Number of Students</form:label></div>
	        <div class="field_div"><form:input path="numStudents" /></div>
	    </div>
    </div>
    
    <div class="row_div">
	    <div class="row_div_left">
	        <div class="caption_div"><form:label path="eventDate">Event Date</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input type= "date" path="eventDate" /></div>
	    </div>
	    
	    <div class="row_div_right">
	        <div  class="caption_div"><form:label path="eventTime">Event Time</form:label></div>
	        <div class="field_div"><form:input path="eventTime" /></div>
	    </div>
	 </div>
    <div class="row_div">
    	<div class="row_div_left">
	        <div class="caption_div"><form:label path="eventNotes">Event Notes</form:label></div>
	        <div class="field_div row_div_field_left_joint"><form:input path="eventNotes" /></div>
	    </div>
	     
	     <div class="row_div_right">  
	        <div class="caption_div"><form:label path="volunteerAssigned">Volunteer Assigned</form:label></div>
	        <div class="field_div"><form:input type="checkbox" path="volunteerAssigned" /></div>
	     </div>
    </div>
    
    <div class="row_div">
        <div >
            <input type="submit" value="Submit"/>
        </div>
    </div>
</div>  
</form:form>
</body>
</html>