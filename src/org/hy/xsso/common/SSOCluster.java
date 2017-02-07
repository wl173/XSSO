package org.hy.xsso.common;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.net.ClientSocket;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.XJava;




/**
 * 单点登陆服务的集群操作 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-07
 * @version     v1.0
 */
public class SSOCluster extends Cluster
{
    
    private SSOCluster()
    {
        
    }
    
    
    
    /**
     * 当应用服务故障后重启时，同步单点登陆服务器的会话数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-07
     * @version     v1.0
     *
     */
    @SuppressWarnings("unchecked")
    public static void syncSSOSessions()
    {
        List<ClientSocket>          v_Servers      = getSSOServersNoMy();
        CommunicationResponse       v_ResponseData = null;
        List<CommunicationResponse> v_Datas        = null;
        
        System.out.println("-- 同步其它单点登陆服务的会话数据... ...");
        
        for (ClientSocket v_Server : v_Servers)
        {
            v_ResponseData = v_Server.getObjects("");
            
            if ( v_ResponseData != null && v_ResponseData.getResult() == 0 )
            {
                v_Datas = (List<CommunicationResponse>)v_ResponseData.getData();
                
                if ( !Help.isNull(v_Datas) ) {break;}
            }
        }
        
        if ( !Help.isNull(v_Datas) )
        {
            int v_Count = 0;
            for (CommunicationResponse v_Data : v_Datas)
            {
                if ( v_Data.getDataExpireTimeLen() > 0 )
                {
                    XJava.putObject(v_Data.getDataXID() ,v_Data.getData() ,v_Data.getDataExpireTimeLen());
                    v_Count++;
                }
            }
            
            System.out.println("-- 同步其它单点登陆服务的会话数据... ...完成. 共同步 " + v_Count + " 份。");
        }
        else
        {
            System.out.println("-- 同步其它单点登陆服务的会话数据... ...完成.");
        }
    }
    
    
    
    /**
     * 获取除我之外的其它服务的集群配置信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *
     * @return
     */
    public static List<ClientSocket> getSSOServersNoMy()
    {
        String             v_My             = Help.NVL(Help.NVL(XJava.getParam("WhoAmI") ,new Param()).getValue());
        String []          v_ClusterServers = Help.NVL(Help.NVL(XJava.getParam("AppServers") ,new Param()).getValue()).split(",");
        List<ClientSocket> v_Clusters       = new ArrayList<ClientSocket>();
        
        if ( !Help.isNull(v_ClusterServers) )
        {
            for (String v_Server : v_ClusterServers)
            {
                if ( !Help.isNull(v_Server) )
                {
                    String [] v_HostPort = (StringHelp.replaceAll(v_Server.trim() ,new String[]{" " ,"\t" ,"\r" ,"\n"} ,new String[]{""}) + ":1721").split(":");
                    
                    if ( !v_My.equalsIgnoreCase(v_HostPort[0]) )
                    {
                        v_Clusters.add(new ClientSocket(v_HostPort[0] ,Integer.parseInt(v_HostPort[1])));
                    }
                }
            }
        }
        
        return v_Clusters;
    }
    
    
    
    /**
     * 获取集群配置信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-17
     * @version     v1.0
     *
     * @return
     */
    public static List<ClientSocket> getSSOServers()
    {
        String []          v_ClusterServers = Help.NVL(Help.NVL(XJava.getParam("AppServers") ,new Param()).getValue()).split(",");
        List<ClientSocket> v_Clusters       = new ArrayList<ClientSocket>();
        
        if ( !Help.isNull(v_ClusterServers) )
        {
            for (String v_Server : v_ClusterServers)
            {
                if ( !Help.isNull(v_Server) )
                {
                    String [] v_HostPort = (StringHelp.replaceAll(v_Server.trim() ,new String[]{" " ,"\t" ,"\r" ,"\n"} ,new String[]{""}) + ":1721").split(":");
                    
                    v_Clusters.add(new ClientSocket(v_HostPort[0] ,Integer.parseInt(v_HostPort[1])));
                }
            }
        }
        
        return v_Clusters;
    }
    
}
