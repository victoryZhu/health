 <%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>  

<script type="text/javascript"> 
$(function(){  
	$("#menu_accordion");
	$("#menu_accordion").height(parseInt($("#menu_accordion").parent().height())); 
	$("#menu_accordion").accordion({ 
    	/*autoheight:false,*/
		fillSpace:true,     //collapsible:true,
		heightStyle:"content", //"auto",//"content",
		active:0
	});
});
 
function clickMenu(url){
	$.ajax({
		url: url,
		type: "GET",
		dataType: "html",
        //success:function(data, textStatus){  
        	 //alert(textStatus); 
        	 //alert(data); 
        	 //$("body>.ui-layout-center").html(data);	 
        //} 	        
		complete : function (req, err) {
			var data=req.responseText;
			//if(authorization(data)){
			if(data.substring(0,10)=='{"success"'){
				data=$.parseJSON(data);
				if(!authorization(data)) return;
			}
			//将返回的HTML加入center中
			$("body>.ui-layout-center").html(data); 
		}
	});
	return false;
}	
</script>

<div id="menu_accordion" class="menu_accordion" >	
	${menuString} 
</div> 			