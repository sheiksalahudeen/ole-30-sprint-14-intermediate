/**
 * Created by angelind on 12/23/14.
 */

jq(window).load(function(){

});

jq("select[id='fieldType_add_control']").live("change",function(){

    //alert( "fieldtype" );
    var value= jq("select[id='fieldType_add_control']").attr('value');
    if(value == "boolean"){
        //alert( "boolean" );
        jq('#fieldValue_add_control').attr('maxlength','1');
    }
    else{
        jq('#fieldValue_add_control').attr('maxlength','100');
    }
});


jq("#alertInterval_add_control").live('keydown',function(event) {
    if ( event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 ||
        (event.keyCode == 65 && event.ctrlKey === true) ||
        (event.keyCode >= 35 && event.keyCode <= 39)) {
        return;
    }
    else {
        if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 )) {
            event.preventDefault();
        }
    }
});
