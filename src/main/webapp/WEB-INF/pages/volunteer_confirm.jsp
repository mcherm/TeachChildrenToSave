<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Teach Children To Save - volunteer confirmation</title>
    <%@include file="include/commonHead.jsp"%>
</head>
<body>
<%@include file="include/header.jsp"%>
<h2>Submitted Student Information</h2>
   <div>
    <div>
        <div>FirstName</div>
        <div>${firstName}</div>
    </div>
    <div>
        <div>LastName</div>
        <div>${lastName}</div>
    </div>
    <div>
        <div>Email Address</div>
        <div>${emailAddress}</div>
    </div>
</div>  
</body>
</html>