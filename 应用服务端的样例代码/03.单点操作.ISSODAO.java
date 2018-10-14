package xxx.xxx.dao;

import xxx.xxx.User;





/**
 * 单点登陆DAO接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-04
 * @version     v1.0
 */
public interface ISSODAO
{
    public final  static String               $USID           = "USID";
    
    
    
    /**
     * 集群同步单点登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_SessionID  会话ID
     * @param i_User       登陆的用户信息
     */
    public void loginClusterUser(String i_SessionID ,User i_User);
    
    
    
    /**
     * 用户首次登陆时，集群移除单点登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_SessionID  会话ID
     */
    public void logoutClusterUser(String i_SessionID);
    
    
    
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
    public void aliveClusterUser(String i_SessionID ,Object i_User);
    
    
    
    /**
     * 当应用服务故障后重启时，同步单点登陆服务器的会话数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-07
     * @version     v1.0
     *
     */
    public void syncSSOSessions();
    
    
    
    /**
     * 单点登陆的会话超时时长(单位：秒)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @return
     */
    public long getSSOSessionTimeOut();
    
}
