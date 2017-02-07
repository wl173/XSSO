package org.hy.xsso.common;

import org.hy.common.Help;
import org.hy.common.app.Param;
import org.hy.common.xml.XJava;





/**
 * 集群操作 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-07
 * @version     v1.0
 */
public class Cluster
{
    
    /**
     * 集群并发通讯的超时时长。默认为：30000毫秒
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-25
     * @version     v1.0
     *
     * @return
     */
    public static long getClusterTimeout()
    {
        return Long.parseLong(Help.NVL(Help.NVL(XJava.getParam("ClusterTimeout") ,new Param()).getValue() ,"30000"));
    }
    
    
    
    /**
     * 单点登陆的会话超时时长。默认为：3600秒
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @return
     */
    public static int getSSOSessionTimeOut()
    {
        return Integer.parseInt(Help.NVL(Help.NVL(XJava.getParam("SSOSessionTimeOut") ,new Param()).getValue() ,"3600"));
    }
    
}
