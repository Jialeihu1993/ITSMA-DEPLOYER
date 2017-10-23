function initCapabilities(i18ndata){
    
    var initSelect = currentCap.initial_edition_selected;

    var capDescString  = getI18NValue(currentCap.description);
	var welcome_msg = getI18NValue(currentCap.welcome_msg);
	loadBundles(function(){
	    $("#section_main").append(
	    		"<div class='leftDiv'>"
			+	    "<div class='intro-title' align='left' style=''>"+SHOWFEATURES_CONTENT1+"</div>"
			+		"<iframe id='welcome-frame' class='welcome-frame' align='left' ></iframe>"
			+	"</div>"
			+	"<div class='rightDiv' >"
			+	"<div class='' align='left' style='margin: 70px 30px 0px 0px;'>"
            +    "<div id='headerDiv' style='height:110px' >"
            +       "<div title='"+htmlEncode(capDescString)+"' style='line-height: 35px;font-size: 28px;font-family: HPEMetricSemibold;border-bottom: solid 1px #CCCCCC;'>"+currentCap.suite+"</div>"
           		
            +       "<div id='radioDiv' style='display:block;'></div>" 
            +       "<div id='radioDesc' class='capDescDiv' ></div>" 
            +    "</div>"
            +	 "<div id='sizeContainer' style='display:block;height:110px;'>"
            +    	"<span id='sizeTitle' style='display:none;font-size:20px;font-family: HPEMetricSemibold;'>Install Size</span>"
            +	 	"<div id='sizeDiv' style='display:none;border-top: solid 1px #CCCCCC;'>"
            
            +	 	"</div>"
            +       "<div id='sizeDesc' class='capDescDiv' ></div>" 
            +	 "</div>"
           
            
            +    "<span style='font-size:20px;font-family: HPEMetricSemibold;'>"+SHOWFEATURES_CAPABILITIES+"</span>"
            +	 "<div id='bottom-fas' style='border-top: solid 1px #CCCCCC;padding-top: 10px;overflow-y: auto' >"
	        +	 	"<div id='setsDiv' style='text-align:left;width:50%;float:left;' >"
	            
	        +    	"</div>"
	        +	 	"<div id='middle-vtc' style='' >"
            
            +    	"</div>"
	        +	 	"<div id='featureDiv' style='text-align:left;width:49%;float:right;' >"
            
            +    	"</div>"
            +    "</div>"
            
            +   "</div>"
            +   "</div>"
   		 );    	
	})   
	
	//add welcome_msg
	$("#welcome-frame").contents().find("body").append(welcome_msg);
	
    
    //遍历加载edition
    currentCap.editions.forEach(function (e) {
    	var edtDescString  = getI18NValue(e.description);
        var edtNameString  = getI18NValue(e.name);
          
    	$("#radioDiv").append("<span class='radio-item'>"
    						+	"<input id='"+e.id+"' type='radio'  name='edition' class='radio-btn maskRadio' />"
    						+	"<label for='"+e.id+"' class='maskRadioImg' ></label>&nbsp;"
    						+	"<label for='"+e.id+"' class='radio-font maskRadioText' title='"+htmlEncode(edtDescString)+"'>"+htmlEncode(edtNameString)+"</label>"
    						+"</span>")
    	$(".radio-btn[id='"+e.id+"']").data("edition",e);
    	
    	if(!e.display ){
    		$(".radio-btn[id='"+e.id+"']").parent(".radio-item").hide();
    	}
    	
    	if(e.selected ){
    		initSelect = e.id;
    	}
    });
    
    //遍历加载install_size
    if(currentCap.install_size && currentCap.install_size!=null && currentCap.install_size.length>0){
    	$("#sizeTitle").show();
    	$("#sizeDiv").show();
    	
	    currentCap.install_size.forEach(function (e) {
	    	var sizeDescString  = getI18NValue(e.description);
	        var sizeNameString  = getI18NValue(e.name);
	          
	    	$("#sizeDiv").append("<span class='size-item'>"
	    						+	"<input id='"+e.id+"' type='radio'  name='install_size' class='size-btn maskRadio' />"
	    						+	"<label for='"+e.id+"' class='maskRadioImg' ></label>&nbsp;"
	    						+	"<label for='"+e.id+"' class='radio-font maskRadioText' title='"+htmlEncode(sizeDescString)+"'>"+htmlEncode(sizeNameString)+"</label>"
	    						+"</span>")
	    	$(".size-btn[id='"+e.id+"']").data("install_size",e);
	    	$(".size-item").css("display","none");
	    });
    }
    
    //resize bottom div
    $("#bottom-fas").height($("body").height()-410); 
    $("#welcome-frame").height($("body").height()-210); 
		
	//遍历加载feature_sets
    currentCap.feature_sets.forEach(function (e) {
      		var fsDescString  = getI18NValue(e.description);
      		var fsNameString  = getI18NValue(e.name);
	      	var suffixIconFlag = false;
	      	for(var i=0;i<e.has_features.length;i++ ){
	      		var hfopt = e.has_features[i].split(":");
	      		for(var j=0;j<currentCap.features.length;j++){
	      			if(hfopt[1] == currentCap.features[j].id){
	      				if(currentCap.features[j].display){
	      					suffixIconFlag = true;
	      					break;
	      				}
	      			}
	      		}
	      		if(suffixIconFlag){
	      			break;
	      		}
	      	}
	      		
	      	$("#setsDiv").append("<div class='fset-item'>"
			                +		"<input type='checkbox'  id='"+e.id+"' class='fset-checkbox maskCheckbox'  />"
			                +		"<label for='"+e.id+"' class='maskCheckboxImg'></label>&nbsp;"
			                +		"<span class='fset-font' title='"+htmlEncode(fsDescString)+"' >"+htmlEncode(fsNameString)+"</span>"
			                +		(suffixIconFlag ? "<button fsid='"+e.id+"' class='show-feature-icon' style='display:none'></button><span class='middle-line' style='' ></span>":"")
			                +		"<span class='fesDescDiv' >"+htmlEncode(fsDescString)+"</span>"
			                +	"</div>")
	      	
	      	$(".fset-checkbox[id='"+e.id+"']").data("fset",e);
	      	
	      	if(!e.display ){
	      		$(".fset-checkbox[id='"+e.id+"']").parent(".fset-item").hide();
	      	}
	      
	      	if(e.selected ){
	    		$(".fset-checkbox[id='"+e.id+"']")[0].checked=true;;
	    	}else{
	    		$(".fset-checkbox[id='"+e.id+"']")[0].checked=false;;
	    	}
	  });
	    
	  //遍历加载features
    currentCap.features.forEach(function (e) {
      	var featDescString  = getI18NValue(e.description);
	  	var featNameString  = getI18NValue(e.name);
	  	$("#featureDiv").append("<div class='feature-item'>"
				                +	"<input type='checkbox'  id='"+e.id+"' class='feature-checkbox maskCheckbox' />"
				                +	"<label for='"+e.id+"' class='maskCheckboxImg'></label>&nbsp;"
				                +	"<span class='feature-font' title='"+htmlEncode(featDescString)+"'>"+htmlEncode(featNameString)+"</span>"
				                +	"<span class='fesDescDiv' >"+htmlEncode(featDescString)+"</span>"
				                +"</div>")
	  	
	  	$(".feature-checkbox[id='"+e.id+"']").data("feature",e);
	  	if(!e.display ){
	  		$(".feature-checkbox[id='"+e.id+"']").parent(".feature-item").hide();
	  	}
	  	
	  	if(e.selected ){
    		$(".feature-checkbox[id='"+e.id+"']")[0].checked=true;;
    	}else{
    		$(".feature-checkbox[id='"+e.id+"']")[0].checked=false;;
    	}
    	
	  });
	    //install_size点击事件
		$(".size-btn").click(function(){
			$(".size-btn").each(function(){
				$(this).data("install_size").selected = false;
			});
			var sizeObj = $(this).data("install_size");
			sizeObj.selected = true;
			
			var sizeDescString  = getI18NValue(sizeObj.description);
			$("#sizeDesc").text(sizeDescString);
			$("#sizeDesc").attr("title",sizeDescString);
		});
		
	    //feature-checkbox点击事件
		$(".feature-checkbox").click(function(){
			var featureObj = $(this).data("feature");
			if($(this)[0].checked){
				featureObj.selected = true;
				checkRelatedFeature(featureObj);
			}else{
				featureObj.selected = false;
				cancelRelatedFeature(featureObj);
			}
		});
		
	    //fset-checkbox点击事件
		$(".fset-checkbox").click(function(){
			
			var fsetObj = $(this).data("fset");
			if($(this)[0].checked){
				$(this).siblings(".show-feature-icon").show();
	      		fsetObj.selected = true;
	      		
	      		fsetObj.has_features.forEach(function (hf) {	
	      			var hfopt = hf.split(":");
	      			var featureCheckbox = $(".feature-checkbox[id='"+hfopt[1]+"']");
				
	      			if(hfopt[0]=="always" || hfopt[0] == initSelect){
	      				//should show/hide feature
//	      				if(featureCheckbox.data("feature").display){
//	      					featureCheckbox.parent(".feature-item").show();
//	      				}else{
//	      					featureCheckbox.parent(".feature-item").hide();
//	      				}
	      				
	      				
	      				if(hfopt[2] == "selected"){
	      					featureCheckbox.attr("disabled","disabled");
	      					featureCheckbox[0].checked = true;
	      					featureCheckbox.data("feature").selected = true;
	      					featureCheckbox.data("feature").also_select.forEach(function (asf) {
	      						var afc = $(".feature-checkbox[id='"+asf+"']");
	      						afc.data("feature").selected = true;
	      						afc[0].checked = true;
	      					});
	      				}
	      				else if(hfopt[2] == "optional"){
	      					featureCheckbox.removeAttr("disabled");
	      					featureCheckbox[0].checked = false;
	      				}
	      				else{ }
	      			}
	      			else{ }
	      		});
			}else{
				fsetObj.selected = false;
				resetFset();
				if($(this).siblings(".middle-line").css("display")!="none"){
					$(".feature-item").hide();
					$(this).siblings(".show-feature-icon").hide();
					$(this).siblings(".middle-line").hide();
					$("#middle-vtc").hide();
					
				}else{
					$(this).siblings(".show-feature-icon").hide();
				}
				
				
				$(".fset-checkbox").each(function(){
					if($(this).data("fset").selected ){
						
						$(this).data("fset").has_features.forEach(function (hf) {
			      			var hfopt = hf.split(":");
			      			var featureCheckbox = $(".feature-checkbox[id='"+hfopt[1]+"']");
						
			      			if(hfopt[0]=="always" || hfopt[0] == initSelect){
//			      				should show/hide feature
//			      				if(featureCheckbox.data("feature").display){
//			      					featureCheckbox.parent(".feature-item").show();
//			      				}else{
//			      					featureCheckbox.parent(".feature-item").hide();
//			      				}
			      				
			      				if(hfopt[2] == "selected"){
			      					featureCheckbox.attr("disabled","disabled");
			      					featureCheckbox[0].checked = true;
			      					featureCheckbox.data("feature").selected = true;
			      					featureCheckbox.data("feature").also_select.forEach(function (asf) {
			      						var afc = $(".feature-checkbox[id='"+asf+"']");
			      						afc.data("feature").selected = true;
			      						afc[0].checked = true;
			      					});
			      				}
			      				else if(hfopt[2] == "optional"){
	      							featureCheckbox.removeAttr("disabled");
			      					featureCheckbox[0].checked = false;
			      				}
			      				else{ }
			      			}
			      			else{ }
			      		});
					}
				});
			}
			
			resizeMiddleVtc();
		});
	
	    //radio btn点击事件
		$(".radio-btn").click(function(){
			resetEdition();
			
			var clickRadio = $(this);
			
			var edition = $(this).data("edition");
//			currentEdition = edition;
			edition.selected = true;
			
			var edtDescString  = getI18NValue(edition.description);
			$("#radioDesc").text(edtDescString);
			$("#radioDesc").attr("title",edtDescString);
			//判断显示哪些allow_size
			if(edition.allow_size && edition.allow_size!=null){
				edition.allow_size.forEach(function (als) {
					var opts = als.split(":");
					
					var sizeRadio = $(".size-btn[id='"+opts[0]+"']");
					var sizeObj = sizeRadio.data("install_size");
					
					sizeRadio.parent(".size-item").css("display","inline-block");
					if(opts[1] == "selected"){
						sizeRadio[0].checked = true;
						sizeObj.selected = true;
						
						var sizeDescString  = getI18NValue(sizeObj.description);
						$("#sizeDesc").text(sizeDescString);
						$("#sizeDesc").attr("title",sizeDescString);
					}else{
						sizeRadio[0].checked = false;
						sizeObj.selected = false;
					}
				});
			}
			

			//判断显示哪些fset
	      	edition.has_feature_sets.forEach(function (hfs) {
	      		var opts = hfs.split(":");
	      		var fsetCheckbox = $(".fset-checkbox[id='"+opts[0]+"']");
	      		
	      		//should show/hide fset
	      		if(fsetCheckbox.data("fset").display){
	      			fsetCheckbox.parent(".fset-item").show();
	      		}else{
	      			fsetCheckbox.parent(".fset-item").hide();
	      		}
	      		
	      		if(opts[1] == "selected"){
	      			
	      			fsetCheckbox[0].checked = true;
	      			fsetCheckbox.siblings(".show-feature-icon").show();
	      			fsetCheckbox.attr("disabled","disabled");
					
					var fsetObj = fsetCheckbox.data("fset");
	      			fsetObj.selected = true;
	      			
		      		fsetObj.has_features.forEach(function (hf) {
		      			
		      			var hfopt = hf.split(":");
		      			var featureCheckbox = $(".feature-checkbox[id='"+hfopt[1]+"']");
					
		      			if(hfopt[0]=="always" || hfopt[0] == edition.id){
		      				//should show/hide feature
//		      				if(featureCheckbox.data("feature").display){
//		      					featureCheckbox.parent(".feature-item").show();
//		      				}else{
//		      					featureCheckbox.parent(".feature-item").hide();
//		      				}
		      				
		      				if(hfopt[2] == "selected"){
		      					featureCheckbox.attr("disabled","disabled");
		      					featureCheckbox[0].checked = true;
		      					featureCheckbox.data("feature").selected = true;
		      					featureCheckbox.data("feature").also_select.forEach(function (asf) {
		      						var afc = $(".feature-checkbox[id='"+asf+"']");
		      						afc.data("feature").selected = true;
		      						afc[0].checked = true;
		      					});
		      					
		      				}
		      				else if(hfopt[2] == "optional"){
	      						featureCheckbox.removeAttr("disabled");
		      					featureCheckbox[0].checked = false;
		      				}
		      				else{ }
		      			}
		      			else{ }
		      		});
	      		}
	      		else if(opts[1] == "optional"){
	      			fsetCheckbox[0].checked = false;
				  	fsetCheckbox.removeAttr("disabled");
				  	fsetCheckbox.siblings(".show-feature-icon").hide();
	      		}
	      		else{ }
			});
			resizeMiddleVtc();
		});  
		
		$(".show-feature-icon").click(function(){
			var fsid = $(this).attr("fsid");
			var fsetCheckbox = $(".fset-checkbox[id='"+fsid+"']");
			var fsetObj = fsetCheckbox.data("fset");
			
			if($(this).siblings(".middle-line").css("display")=="none"){
				$(".middle-line").hide();
				$("#middle-vtc").css("display","inline-block");
				
				$(this).siblings(".middle-line").show();
				$("#featureDiv").children(".feature-item").hide();
				fsetObj.has_features.forEach(function (hf) {
		      			
	      			var hfopt = hf.split(":");
	      			var featureCheckbox = $(".feature-checkbox[id='"+hfopt[1]+"']");
//					var featureObj = featureCheckbox.data("feature");
	      			
      				//should show/hide feature
      				if(featureCheckbox.data("feature").display){
      					featureCheckbox.parent(".feature-item").show();
      				}else{
      					featureCheckbox.parent(".feature-item").hide();
      				}
	      		});
	      		$("#featureDiv").show();
	      		resizeMiddleVtc();
			}else{
				$(".middle-line").hide();
				$("#middle-vtc").hide();
				$("#featureDiv").children(".feature-item").hide();
				$("#featureDiv").hide();
			}
			
		});
		
		resetEdition();
		//选择初始edition
		if($(".radio-btn[id='"+initSelect +"']")[0]){
			$(".radio-btn[id='"+initSelect +"']")[0].click();
		}
//		$(".radio-btn[id='"+initSelect +"']")[0].click();
		
		if(!currentCap.allow_custom_selection){
			$(".radio-btn").attr("disabled","disabled");
		}
		
//		resizeComponent();
		
		window.onresize = function(){
			$("#bottom-fas").height($("body").height()-410);
			$("#welcome-frame").height($("body").height()-210); 
			resizeComponent();
		}
		function getI18NValue(keyWithSymbol){
			var i18nKey = "";
			if(keyWithSymbol){
				var num1 = keyWithSymbol.indexOf("<<");
				var num2 = keyWithSymbol.lastIndexOf(">>");
				if(num1>-1 && num2>-1 && num2-num1>=2){
					i18nKey = keyWithSymbol.substring(num1+2,num2);
				}else{
					i18nKey = keyWithSymbol
				}
			}
			return (i18ndata && i18ndata[i18nKey])? i18ndata[i18nKey] : i18nKey;
		}
		function resizeMiddleVtc(){
			var maxHeight = $("#setsDiv").height() > $("#featureDiv").height() ? $("#setsDiv").height() : $("#featureDiv").height();
			$("#middle-vtc").height(maxHeight);
		}
}

//reset
function resetEdition() {		
	$(".radio-btn").each(function(){
		$(this).data("edition").selected = false;
	});
	
	$(".size-btn").each(function(){
		$(this).data("install_size").selected = false;
	});
	
	$(".fset-checkbox").each(function(){
		$(this).data("fset").selected = false;
	});
	$(".feature-checkbox").each(function(){
		$(this).data("feature").selected = false;
	});
	
	$("#middle-vtc").css("display","none");
	$(".middle-line").css("display","none");
	//feature_sets页面全部隐藏
	$(".fset-item").hide();
	//feature页面全部隐藏
	$(".feature-item").hide();
	//size selection hide
	$(".size-item").css("display","none");
	
	$(".fset-checkbox").each(function(){
		$(this).attr("disabled","disabled");
		$(this)[0].checked = false;
	});
	$(".feature-checkbox").each(function(){
		$(this).attr("disabled","disabled");
		$(this)[0].checked = false;
	});
}
//reset
function resetFset() {
	$(".feature-checkbox").each(function(){
		$(this).data("feature").selected = false;
	});
	
	//feature页面全部隐藏
//	$(".feature-item").hide();
	
	$(".feature-checkbox").each(function(){
		$(this).attr("disabled","disabled");
		$(this)[0].checked = false;
	});
}
function checkRelatedFeature(featureObj){
	featureObj.also_select.forEach(function (asf) {
		var afc = $(".feature-checkbox[id='"+asf+"']");
		var afObj = afc.data("feature");
		
		afObj.selected = true;
		afc[0].checked = true;
		checkRelatedFeature(afObj);
	});
}

function cancelRelatedFeature(featureObj){
	currentCap.features.forEach(function (feature) {
      	feature.also_select.forEach(function (asf) {
      		if(asf == featureObj.id){
      			var uncheckedFeature = $(".feature-checkbox[id='"+feature.id+"']");
      			uncheckedFeature[0].checked = false;
      			feature.selected = false;
      			cancelRelatedFeature(feature);
      		}
		});
    });
}
function htmlEncode(str) {
    var s = "";
    if (str.length == 0) return "";
    s = str.replace(/&/g, "&amp;");
    s = s.replace(/</g, "&lt;");
    s = s.replace(/>/g, "&gt;");
    s = s.replace(/ /g, "&nbsp;");
    s = s.replace(/\'/g, "&#39;");
    s = s.replace(/\"/g, "&quot;");
    s = s.replace(/\n/g, "<br>");
    return s;
}


function htmlDecode(str) {
    var s = "";
    if (str.length == 0) return "";
    s = str.replace(/&amp;/g, "&");
    s = s.replace(/&lt;/g, "<");
    s = s.replace(/&gt;/g, ">");
    s = s.replace(/&nbsp;/g, " ");
    s = s.replace(/&#39;/g, "\'");
    s = s.replace(/&quot;/g, "\"");
    s = s.replace(/<br>/g, "\n");
    return s;
}


