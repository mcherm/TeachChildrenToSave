<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Teach Children To Save - Event Confirm</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body>
<%@include file="include/header.jsp"%>
<h2>Submitted Event Information</h2>
   <div>
    <div>
        <div>eventID</div>
        <div>${eventID}</div>
    </div>
    <div>
        <div>SchooldID</div>
        <div>${schooldID}</div>
    </div>
    <div>
        <div>Teacher User ID</div>
        <div>${teacherUserID}</div>
    </div>
    <div>
        <div>Event Notes</div>
        <div>${eventNotes}</div>
    </div>
</div>  
</body>
</html>