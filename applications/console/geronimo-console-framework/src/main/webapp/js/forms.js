function textElementsNotEmpty(formName, elementNameArray){
    var obj;
    for(i in elementNameArray){
        var elem = elementNameArray[i];
        obj = eval("document.forms['" + formName + "'].elements['"+ elem +"']");
        if(isEmptyString(obj.value)){
            alert(elem + " must not be empty.");
            obj.focus(); 
            return false;             
        }
    }
    return true;
}
function isEmptyString(value){
    return value.length < 1;
}
function checkIntegral(formName, elementName){
    var obj = eval("document.forms['" + formName + "'].elements['"+ elementName +"']");
    if(isIntegral(obj.value)) return true;
    else{
        alert(elementName + " must be an integer.");
        obj.focus();
        return false;
    }
}

function isIntegral(value){
    if(value.length < 1) return false;
    var ints = "1234567890";
    for(i = 0; i < value.length; i++){
        if(ints.indexOf(value.charAt(i)) < 0) return false;
    }
    return true;
}
    