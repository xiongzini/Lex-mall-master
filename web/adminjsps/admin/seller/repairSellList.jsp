<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: T020
  Date: 2020/3/31
  Time: 10:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <head>
        <title>运营中商家</title>

        <style type="text/css">
            table {
                font-family: 宋体;
                font-size: 14pt;
                width: 70%;
                text-align: center;
            }

            a:link {
                font-size: 16px;
                color: #6699cc;
                text-decoration: none;
            }

            a:visited {
                font-size: 16px;
                color: #00aeff;
                text-decoration: none;
            }

            a:hover {
                font-size: 16px;
                color: slateblue;
                text-decoration: none;
            }
        </style>
    </head>
<body>
<a href="javascript:history.go(-1);">&lt;&lt;返回</a>
<h2 style="text-align: center;">停运中商家</h2>
<table align="center" border="1" cellpadding="0" cellspacing="0">
    <tr id="th">
        <th colspan="4">商家信息</th>
        <th>操作</th>
    </tr>
    <tr>
        <th>门店名</th>
        <th>门店地址</th>
        <th>商家email</th>
        <th>商家电话</th>
        <th>-</th>
    </tr>
    <c:forEach items="${sellerList}" var="seller">
        <tr>
            <td>${seller.s_storeName}</td>
            <td class="address">${seller.s_addr}</td>
            <td>${seller.s_email}</td>
            <td>${seller.s_phone}</td>
            <td>
                <a href="<c:url value="/admin/AdminServlet?method=repairSeller&s_id=${seller.s_id}"/>"
                   onclick="return confirm('确认恢复${seller.s_storeName}门店吗？')">恢复门店</a>
            </td>
        </tr>
    </c:forEach>

</table>
</body>
</html>
