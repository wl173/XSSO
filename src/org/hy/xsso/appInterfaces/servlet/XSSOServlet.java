package org.hy.xsso.appInterfaces.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hy.common.Help;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.xml.XJava;
import org.hy.xsso.common.AppCluster;
import org.hy.xsso.common.Log;





/**
 * 单点登陆验证
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-03
 * @version     v1.0
 */
public class XSSOServlet extends HttpServlet
{
    private static final long   serialVersionUID = 2336614778115702389L;

    /** 登陆的Session会话ID标识，标识着是否登陆成功 */
    public  static final String $SessionID = "$XSSO$";
    
    
    
    public XSSOServlet()
    {
        super();
    }
    
    
    
    public void doGet(HttpServletRequest i_Request, HttpServletResponse i_Response) throws ServletException, IOException 
    {
        CommunicationRequest v_SessionData = (CommunicationRequest)i_Request.getSession().getAttribute($SessionID);
        
        // 单点登陆：请求各个单点是否有过登录动作，并通过jsonp跨域回调给最终用户当前访问的单点。
        String v_SSOCallBack = i_Request.getParameter("SSOCallBack");
        String v_USID        = i_Request.getParameter("USID");
        String v_SessionUSID = "";
        
        if ( !Help.isNull(v_SSOCallBack) )
        {
            if ( null != v_SessionData )
            {
                v_SessionUSID = v_SessionData.getDataXID();
                v_SessionData = (CommunicationRequest)XJava.getObject(v_SessionUSID);
                if ( v_SessionData != null )
                {
                    i_Response.getWriter().println(v_SSOCallBack + "('" + v_SessionUSID + "');");
                    Log.log(":USID 全局会话有效，返回票据。" ,v_SessionUSID);
                }
                else
                {
                    // 单点已退出 或 会话已超时过期
                    i_Request.getSession().removeAttribute($SessionID);
                    i_Request.getSession().invalidate();
                    Log.log(":USID 票据已失效，全局会话将销毁。" ,v_SessionUSID);
                }
            }
            
            return;
        }
        
        // 验证登录
        if ( null == v_SessionData )
        {
            if ( Help.isNull(v_USID) )
            {
                // Nothing.
            }
            else if ( this.createSessionByUSID(i_Request ,v_USID) )
            {
                Log.log(":USID 使用票据创建全局会话。" ,v_USID);
            }
            else
            {
                Log.log(":USID 票据已失效 或 已过期 或 为非法票据。" ,v_USID);
            }
        }
        else
        {
            v_SessionUSID = v_SessionData.getDataXID();
            v_SessionData = (CommunicationRequest)XJava.getObject(v_SessionUSID);
            
            if ( v_SessionData == null )
            {
                if ( !Help.isNull(v_USID) && this.createSessionByUSID(i_Request ,v_USID) )
                {
                    // 如果票据USID是有效的，有可能是退出后，又重新登陆的
                    Log.log(":USID 使用票据创建全局会话。旧票据 " + v_SessionUSID + " 已销毁。" ,v_USID);
                }
                else
                {
                    // 单点已退出 或 会话已超时过期
                    i_Request.getSession().removeAttribute($SessionID);
                    i_Request.getSession().invalidate();
                    Log.log(":USID 票据已失效 或 已过期。" ,v_SessionUSID);
                }
            }
            else
            {
                // 保持集群会话活力及有效性
                AppCluster.aliveCluster(v_SessionUSID ,v_SessionData ,AppCluster.getSSOSessionTimeOut());
                Log.log(":USID A保持集群会话活力。" ,v_SessionUSID);
            }
        }
    }
    
    
    
    /**
     * 按票据USID创建单点会话
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_Request
     * @param i_USID
     * 
     * @return  创建成功返回 true
     */
    private boolean createSessionByUSID(HttpServletRequest i_Request ,String i_USID)
    {
        CommunicationRequest v_SessionData = (CommunicationRequest)XJava.getObject(i_USID);
        
        if ( v_SessionData == null )
        {
            // 单点已退出 或 会话已超时过期
            return false;
        }
        else
        {
            // 跨域单点登陆成功
            i_Request.getSession().setMaxInactiveInterval(AppCluster.getSSOSessionTimeOut());
            i_Request.getSession().setAttribute($SessionID ,v_SessionData);
            
            AppCluster.loginCluster(v_SessionData.getDataXID() ,v_SessionData);
            
            return true;
        }
    }
    
    
    
    public void doPost(HttpServletRequest i_Request, HttpServletResponse i_Response) throws ServletException, IOException 
    {
        this.doGet(i_Request ,i_Response);
    }
    
}
