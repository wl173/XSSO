package xxx.xxx.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.net.ClientSocket;
import org.hy.common.net.ClientSocketCluster;
import org.hy.common.net.data.Communication;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import xxx.xxx.ISSODAO;
import xxx.xxx.User;





/**
 * 单点登陆DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-04
 * @version     v1.0
 */
@Xjava
public class SSODAO implements ISSODAO
{
    
    /**
     * 用户首次登陆时，集群同步单点登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_SessionID  会话ID
     * @param i_User       登陆的用户信息
     */
    public void loginClusterUser(String i_SessionID ,User i_User)
    {
        List<ClientSocket> v_Servers     = this.getSSOServers();
        Communication      v_SessionData = new Communication();
        User               v_Clone       = i_User;
        String             v_USID        = "";
        
        if ( i_SessionID.startsWith($USID) )
        {
            v_USID = i_SessionID;
        }
        else
        {
            v_USID = $USID + i_SessionID;
        }
        
        v_SessionData.setDataXID(          v_USID);
        v_SessionData.setData(             v_Clone);
        v_SessionData.setDataExpireTimeLen(this.getSSOSessionTimeOut());
        
        XJava.putObject(v_SessionData.getDataXID() ,v_SessionData ,v_SessionData.getDataExpireTimeLen());
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.sendObjects(v_Servers 
                                           ,this.getClusterTimeout() 
                                           ,v_SessionData.getDataXID()
                                           ,v_SessionData
                                           ,v_SessionData.getDataExpireTimeLen()
                                           ,true);
        }
    }
    
    
    
    /**
     * 用户退出时，集群移除单点登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_SessionID  会话ID
     */
    public void logoutClusterUser(String i_SessionID)
    {
        List<ClientSocket> v_Servers = this.getSSOServers();
        
        XJava.remove(i_SessionID);
        
        if ( !Help.isNull(v_Servers) )
        {
            CommunicationRequest v_RequestData = new CommunicationRequest();
            
            v_RequestData.setEventType("logout");
            
            if ( i_SessionID.startsWith($USID) )
            {
                v_RequestData.setDataXID(i_SessionID);
            }
            else
            {
                v_RequestData.setDataXID($USID + i_SessionID);
            }
            
            ClientSocketCluster.sends(v_Servers ,this.getClusterTimeout() ,v_RequestData ,false);
        }
    }
    
    
    
    /**
     * 保持集群会话活力及有效性
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_SessionID  会话ID
     * @param i_User       登陆的用户信息
     */
    public void aliveClusterUser(String i_SessionID ,Object i_User)
    {
        List<ClientSocket>   v_Servers     = this.getSSOServers();
        CommunicationRequest v_RequestData = new CommunicationRequest();
        Communication        v_SessionData = new Communication();
        String               v_USID        = "";
        
        if ( i_SessionID.startsWith($USID) )
        {
            v_USID = i_SessionID;
        }
        else
        {
            v_USID = $USID + i_SessionID;
        }
        
        v_SessionData.setDataXID(          v_USID);
        v_SessionData.setData(             i_User);
        v_SessionData.setDataExpireTimeLen(this.getSSOSessionTimeOut());
        
        v_RequestData.setEventType(        "alive");
        v_RequestData.setDataXID(          v_SessionData.getDataXID());
        v_RequestData.setData(             v_SessionData);
        v_RequestData.setDataExpireTimeLen(v_SessionData.getDataExpireTimeLen());
        
        XJava.putObject(v_SessionData.getDataXID() ,v_SessionData ,v_SessionData.getDataExpireTimeLen());
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.sends(v_Servers ,this.getClusterTimeout() ,v_RequestData ,false);
        }
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
    public void syncSSOSessions()
    {
        List<ClientSocket>          v_Servers      = this.getSSOServers();
        CommunicationResponse       v_ResponseData = null;
        List<CommunicationResponse> v_Datas        = null;
        
        System.out.println("-- 同步单点登陆服务的会话数据... ...");
        
        for (ClientSocket v_Server : v_Servers)
        {
            v_ResponseData = v_Server.getObjects($USID);
            
            if ( v_ResponseData != null && v_ResponseData.getResult() == 0 )
            {
                v_Datas = (List<CommunicationResponse>)v_ResponseData.getData();
                
                if ( !Help.isNull(v_Datas) ) {break;}
            }
        }
        
        int v_Count = 0;
        
        if ( !Help.isNull(v_Datas) )
        {
            for (CommunicationResponse v_Data : v_Datas)
            {
                if ( v_Data.getDataExpireTimeLen() > 0 )
                {
                    XJava.putObject(v_Data.getDataXID() ,v_Data.getData() ,v_Data.getDataExpireTimeLen());
                    v_Count++;
                }
            }
        }
        
        System.out.println("-- 同步单点登陆服务的会话数据... ...完成. 共同步 " + v_Count + " 份。");
    }
    
    
    
    /**
     * 同步某一个具体的USID的单点登陆服务器的会话数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-09-03
     * @version     v1.0
     *
     * @param i_USID   会话ID
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object syncSSOSession(String i_USID)
    {
        List<ClientSocket>          v_Servers      = this.getSSOServers();
        CommunicationResponse       v_ResponseData = null;
        List<CommunicationResponse> v_Datas        = null;
        
        for (ClientSocket v_Server : v_Servers)
        {
            v_ResponseData = v_Server.getObjects(i_USID);
            
            if ( v_ResponseData != null && v_ResponseData.getResult() == 0 )
            {
                v_Datas = (List<CommunicationResponse>)v_ResponseData.getData();
                
                if ( !Help.isNull(v_Datas) ) {break;}
            }
        }
        
        if ( !Help.isNull(v_Datas) )
        {
            for (CommunicationResponse v_Data : v_Datas)
            {
                if ( v_Data.getDataExpireTimeLen() > 0 )
                {
                    XJava.putObject(v_Data.getDataXID() ,v_Data.getData() ,v_Data.getDataExpireTimeLen());
                }
            }
        }
        
        return XJava.getObject(i_USID);
    }
    
    
    
    /**
     * 集群并发通讯的超时时长(单位：毫秒)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @return
     */
    private long getClusterTimeout()
    {
        return Long.parseLong(XJava.getParam("ClusterTimeout").getValue());
    }
    
    
    
    /**
     * 单点登陆的会话超时时长(单位：秒)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @return
     */
    public long getSSOSessionTimeOut()
    {
        return Long.parseLong(XJava.getParam("SSOSessionTimeOut").getValue());
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
    private List<ClientSocket> getSSOServers()
    {
        String []          v_ClusterServers = StringHelp.replaceAll(XJava.getParam("SSOServers").getValue() ,new String[]{" " ,"\t" ,"\r" ,"\n"} ,new String[]{""}).split(",");
        List<ClientSocket> v_Clusters       = new ArrayList<ClientSocket>();
        
        for (String v_Server : v_ClusterServers)
        {
            String [] v_HostPort = (v_Server.trim() + ":1721").split(":");
            
            v_Clusters.add(new ClientSocket(v_HostPort[0] ,Integer.parseInt(v_HostPort[1])));
        }
        
        return v_Clusters;
    }
    
}
