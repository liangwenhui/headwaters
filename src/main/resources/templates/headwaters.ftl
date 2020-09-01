<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>ID监控页面</title>
    <link href="/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<table class="table table-hover">
    <thead>
    <tr>
        <th>名称key</th>
        <th>映射id</th>
        <th>步长step</th>
        <th>动态步长autoStep</th>
        <th>当前id池</th>
        <th>备用id池是否就绪</th>
        <th>备用id池异步更新中</th>
        <th>初始化状态</th>
        <th>剩余id数量</th>
        <th>当前值</th>
        <th>最大值</th>
        <th>当前值(内部)</th>
        <th>最大值(内部)</th>

    </tr>
    </thead>
    <tbody>
    <#if data?exists>
        <#list data?keys as key>
        <tr>

            <td>${key}</td>
            <td>${data[key].id}</td>
            <td>${data[key].step}</td>
            <td>${data[key].autoStep}</td>
            <td>${data[key].currentBucketIndex}</td>
            <td>${data[key].nextReady?string('true','false')}</td>
            <td>${data[key].backupThreadRunning?string('true','false')}</td>
            <td>${data[key].initStatus?string('true','false')}</td>
            <td>${data[key].idle}</td>
            <td>${data[key].currentValue}</td>
            <td>${data[key].max}</td>
            <td>${data[key].currentInsideValue}</td>
            <td>${data[key].inside}</td>
        </tr>
        <tr>
        </tr>

        </#list>
    </#if>
    <tbody>
</table>
</body>
</html>