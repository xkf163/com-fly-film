

//格式化日期
function fnRenderPackage(b,type,rowObj,oSettings){
	alert(b+"type:"+type);  
	console.log(JSON.stringify(oSettings));
	return b; 
}

function fnRenderDate(value,type,rowObj,oSetting){ 
	console.log(this);
	console.log("---------------------------------------------"); 
	console.log(value+"--->"+type+"--->"+JSON.stringify(rowObj)+"--->"+JSON.stringify(oSetting));
	return value+"hello world";	
}

function fnRenderSubNames ( d , e , f ) {
	return "<strong style='padding-bottom: 10px;line-height: 25px;'>"+f.nameChn+"</strong><BR><small style='color: #333'>"+f.nameEng +"</small><BR>"+f.year;
}

function fnRenderNames ( d , e , f ) {
	return "<strong style='padding-bottom: 10px;line-height: 25px;'>"+f.name+"</strong><BR><small style='color: #333'>"+f.diskNo + ":" +f.fullPath+"</small><BR>"+f.mediaSizeGB+"Gigabyte";
}
function fnRenderLogo(value) {
	var src = '/assets/images/avatars/nologo.png';
	if (value) {
		src = 'data:image/jpeg;base64,'+value;
	}
	return "<a id='thumbnail' data-magnify='gallery' data-caption='logo' href='"+src+"'><img width='50px'  src="+src+"></a>";
}

function fnRenderNameExtend(value,e,f) {
	return "<strong style='padding-bottom: 10px;line-height: 25px;'>"+value +"</strong><BR><small class='text-info'>"+f.person.job+"</small><BR><small>"+f.person.birthPlace+"</small>";
}
function fnRenderFaceLogo(value) {
	if (value) {
		return "<img width='50px' src= 'data:image/jpeg;base64,"+value+"' >";
	} else
		return "<img width='50px' src= '/assets/images/avatars/noface.png' >";
}

//Film类
function fnRenderFilmLogo(value) {
	if (value) {
		return "<img width='50px' src= 'data:image/jpeg;base64,"+value+"' >";
	} else
		return "<img width='50px' src= '/assets/images/avatars/nologo.png' >";
}

function fnRenderReleaseDate(value) {
	if (value) {
		if (value.indexOf(",")){
			return value.replace(/,/g,"<br>");
		}
	}
	return value;
}

function fnRenderFilmName ( d , e , f ) {
	return "<strong style='padding-bottom: 10px;line-height: 25px;'>"+f.subject+"</strong><BR><small style='color: #333'>"+f.genre+"</small>";
}

function fnRenderPersonNameExtend(value,e,f) {
	return "<strong style='padding-bottom: 10px;line-height: 25px;'>"+value +"</strong><BR><small class='text-info'>"+f.job+"</small><BR><small>"+f.birthPlace+"</small>";
}

