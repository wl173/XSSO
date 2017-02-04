package xx.xx.xx;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Return;

import xx.xx.xx.User;
import xx.xx.xx.SSODAO;





public class LoginAction 
{
    
    public final static String $SessionID = "LoginUser";
    
    

    public User loadSession()
    {
        return this.loadSession("");
    }
    
    
    
    /**
     * 检查session是否失效
     * 
     * @param i_USID
     * @return
     */
    public User loadSession(String i_USID)
    {
        // 应用服务的相关代码
        // ...
        // ...
        // ...
        // 其后执行下面的代码
        
        
        ISSODAO      v_SSODAO    = (ISSODAO) XJava.getObject("SSODAO");
        User         v_LoginUser = (User)getSession().getAttribute($SessionID);
        Return<User> v_SSOUser   = null;
        
        if ( v_LoginUser != null )
        {   
            v_SSOUser = (Return<User>)XJava.getObject(v_LoginUser.getSessionID());
            if ( v_SSOUser != null )
            {
                // 保持集群会话活力及有效性
                v_SSODAO.aliveClusterUser(v_LoginUser.getSessionID() ,v_SSOUser.paramObj);
                return v_LoginUser;
            }
            else
            {
                // 单点已退出
                getSession().removeAttribute($SessionID);
                getSession().invalidate();
                
                return null;
            }
        }
        
        
        // 跨域单点登陆
        if ( !Help.isNull(i_USID) )
        {
            v_SSOUser = (Return<User>)XJava.getObject(i_USID);
            
            if ( v_SSOUser != null )
            {
                v_LoginUser = v_SSOUser.paramObj;
                        
                if ( v_LoginUser != null )
                {
                    System.out.println(Date.getNowTime().getFullMilli() + "  跨域单点登陆：" + v_LoginUser.getUserName());
                    getSession().setSessionAttribute($SessionID ,v_LoginUser);
                    
                    // 保持集群会话活力及有效性
                    v_SSODAO.aliveClusterUser(v_LoginUser.getSessionID() ,v_SSOUser.paramObj);
                    return v_LoginUser;
                }
            }
        }
        
        return null;
    }
    
    

    /**
     * 用户登陆
     * 
     * @param i_User
     * @return
     */
    public User login(User i_User) 
    {
        // 应用服务的登陆验证代码
        // ...
        // ...
        // ...
        // 验证成功后，执行下面的代码

        
        i_User.setSessionID(ISSODAO.$USID + getSession().getId());
        getSession().setSessionAttribute($SessionID, i_User);
        
        // 单点登陆
        ISSODAO  v_SSODAO = (ISSODAO) XJava.getObject("SSODAO");
        v_SSODAO.loginClusterUser(i_User.getSessionID() ,i_User);
        
        return i_User;
    }
    
    

    /**
     * 用户注销
     * 
     * @param i_User
     * @return
     */
    public User logoutUser(User i_User)
    {
        // 应用服务的相关代码
        // ...
        // ...
        // ...
        // 其后执行下面的代码
        
        
        
        User v_LoginUser = (User)getSession().getSessionAttribute($SessionID);
        if ( v_LoginUser != null )
        {
            getSession().removeAttribute($SessionID);
            getSession().invalidate();
                            
            // 单点退出
            ISSODAO v_SSODAO = (ISSODAO) XJava.getObject("SSODAO");
            v_SSODAO.logoutClusterUser(v_LoginUser.getSessionID());
        }
        
        return i_User;
    }
    
}