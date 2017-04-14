<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/zTreeStyle/zTreeStyle.css" />" /> 
<script type="text/javascript" src="<c:url value="/resources/js/jquery.ztree.all-3.5.js" />"></script>

<script> 
var zTreeObj;
var setting = {		   
		   data: {
			   simpleData: {
				   enable: true,
				   idKey: "id",
				   pIdKey: "pId",
				   rootPId: 0
			   }
		   }		   
	};

var nodes = ${nodes};

$(document).ready(function () {   
  //树载入 
  zTreeObj = $.fn.zTree.init($("#treeDemo"), setting, nodes);
  //展开第一个节点，执行有问题！  
  /* var nodes = zTreeObj.getNodes();
  if (nodes.length>0) {
	  zTreeObj.expandNode(nodes[0], true, true, true);
  } */   
  
  //var zTreeObj  = $.fn.zTree.getZTreeObj("treeDemo");  
  //展开全部节点  
  //zTreeObj.expandAll(true);
}); 
</script> 

<div style="overflow-y:scroll;height:100%;">
	<h3>系统模块功能</h3>
	<ul id="treeDemo" class="ztree"></ul>	
</div> 	