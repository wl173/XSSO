function ssoLogin(i_SSOServer ,i_USID)
{
	$.ajax(  
    {
        type     : 'get',  
        url      : 'http://' + i_SSOServer + '/XSSO/sso?USID=' + i_USID,  
        dataType : 'jsonp',  
        jsonp    : "jsoncallback",  
        success  : function(data) {},  
        error    : function() {}  
    });
}