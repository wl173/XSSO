package org.hy.xsso.common;

import org.hy.common.Date;
import org.hy.common.StringHelp;
import org.hy.common.xml.XJava;





/**
 * 可在服务运行时，动态控制日志的输出与否 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-06
 * @version     v1.0
 */
public class Log
{
    
    private Log()
    {
        
    }
    
    
    
    public static void log(String i_Info ,String i_USID)
    {
        if ( Boolean.parseBoolean(XJava.getParam("IsShowLog").getValue()) )
        {
            System.out.println(Date.getNowTime().getFullMilli() + ": " + StringHelp.replaceAll(i_Info ,":USID" ,i_USID));
        }
    }
    
}
