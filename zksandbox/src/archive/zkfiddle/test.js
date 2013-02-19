function checkMail(mails){
    var array = mails.split(",");
    var result="\n";
    for(var i=0;i<array.length;i++){
        var mail = array[i];
        var er = new RegExp(/^[A-Za-z0-9_\-\.]+@[A-Za-z0-9_\-\.]{2,}\.[A-Za-z0-9]{2,}(\.[A-Za-z0-9])?/);
        if(typeof(mail) == "string"){
            if(er.test(mail)){
                result+=array[i]+": valid mail\n";
            }else{
                result+=array[i]+": invalid mail\n";
            }
        }else if(typeof(mail) == "object"){
            if(er.test(mail.value)){
                result+=array[i]+": valid mail\n";
            }
        }else{
            result+=array[i]+": invalid mail\n";
        }
    }
    zk.Widget.$(jq('$lbl')[0]).setValue(result);
}