<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<html>
<head>
    <title>Teach Children To Save - volunteer confirmation</title>
</head>
<body>
<%@include file="include/header.jsp" %>
<h2>Submitted Student Information</h2>
   <div>
    <div>
        <div>Name</div>
        <div>${firstName} &nbsp; ${lastName}</div>
    </div>
    <div>
        <div>Email</div>
        <div>${emailAddress}</div>
    </div>
    <div>
        <div>Work Address</div>
        <div>${addressLine1}</div>
        <div>${addressLine2}</div>
        <div>${state}</div>
        <div>${city} - ${zipcode}</div>
    </div>
    <div>
        <div>Work Phone</div>
        <div>${workPhoneNumber}</div>
    </div>
    <div>
        <div>Mobile Phone</div>
        <div>${mobilePhoneNumber}</div>
    </div>
    <div>
        <div>Employer </div>
        <div>${employerInfo}</div>
    </div>
</div>  
</body>
</html>