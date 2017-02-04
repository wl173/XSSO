package xx.xx.xx;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.net.ClientSocket;
import org.hy.common.net.ClientSocketCluster;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;





/**
 * 单点登陆DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-04
 * @version     v1.0
 */
@Xjava
public class SSODAO
{
    
    public final  static String $USID = "USID";
    
    
    
    /**
     * 用户首次登陆时，集群同步单点登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_SessionID  会话ID
     * @param i_User       登陆的用户信息(需实现 java.io.Serializable 接口)
     */
    public void loginClusterUser(String i_SessionID ,Object i_User)
    {
        List<ClientSocket> v_Servers     = this.getSSOServers();
        Return<Object>     v_RequestData = new Return<Object>();
        String             v_USID        = "";
        
        if ( i_SessionID.startsWith($USID) )
        {
            v_USID = i_SessionID;
        }
        else
        {
            v_USID = $USID + i_SessionID;
        }
        
        v_RequestData.paramObj(i_User);
        v_RequestData.paramStr(v_USID);
        
        XJava.putObject(v_USID ,v_RequestData ,this.getSSOSessionTimeOut());
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.sendObjects(v_Servers 
                                           ,this.getClusterTimeout() 
                                           ,v_USID
                                           ,v_RequestData
                                           ,this.getSSOSessionTimeOut()
                                           ,false);
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
     * @param i_User       登陆的用户信息(需实现 java.io.Serializable 接口)
     */
    public void aliveClusterUser(String i_SessionID ,Object i_User)
    {
        List<ClientSocket>   v_Servers     = this.getSSOServers();
        CommunicationRequest v_RequestData = new CommunicationRequest();
        String               v_USID        = "";
        
        v_RequestData.setEventType("alive");
        v_RequestData.setData(i_User);
        v_RequestData.setDataExpireTimeLen(this.getSSOSessionTimeOut());
        
        if ( i_SessionID.startsWith($USID) )
        {
            v_USID = i_SessionID;
        }
        else
        {
            v_USID = $USID + i_SessionID;
        }
        
        v_RequestData.setDataXID(v_USID);
        XJava.putObject(         v_USID ,(new Return<Object>()).paramObj(i_User).paramStr(v_USID) ,this.getSSOSessionTimeOut());
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.sends(v_Servers ,this.getClusterTimeout() ,v_RequestData ,false);
        }
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
    private long getSSOSessionTimeOut()
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
        String []          v_ClusterServers = XJava.getParam("SSOServers").getValue().split(",");
        List<ClientSocket> v_Clusters       = new ArrayList<ClientSocket>();
        
        for (String v_Server : v_ClusterServers)
        {
            String [] v_HostPort = (v_Server.trim() + ":1721").split(":");
            
            v_Clusters.add(new ClientSocket(v_HostPort[0] ,Integer.parseInt(v_HostPort[1])));
        }
        
        return v_Clusters;
    }
    
}
