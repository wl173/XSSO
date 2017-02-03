package org.hy.xsso.appInterfaces.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.xml.XJava;
import org.hy.xsso.common.Cluster;





/**
 * TODO(请详细描述类型的作用。描述后请删除todo标签) 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-03
 * @version     v1.0
 */
public class XSSOServlet extends HttpServlet
{
    private static final long   serialVersionUID = 2336614778115702389L;

    /** 登陆的Session会话ID标识，标识着是否登陆成功 */
    public  static final String $SessionID       = "$XSSO$";
    
    /** 单点登陆的统计用户会话ID */
    public  static final String $USID            = "USID";
    
    
    
    public XSSOServlet()
    {
        super();
    }
    
    
    
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest i_Request, HttpServletResponse i_Response) throws ServletException, IOException 
    {
        i_Response.setContentType("text/html;charset=UTF-8");
        
        Return<Object> v_SessionData = (Return<Object>)i_Request.getSession().getAttribute($SessionID);
        
        // 单点登陆：请求各个单点是否有过登录动作，并通过jsonp跨域回调给最终用户当前访问的单点。
        String v_SSOCallBack = i_Request.getParameter("SSOCallBack");
        String v_USID        = i_Request.getParameter("USID");
        
        if ( !Help.isNull(v_SSOCallBack) )
        {
            if ( null != v_SessionData )
            {
                v_SessionData = (Return<Object>)XJava.getObject(v_SessionData.paramStr);
                if ( v_SessionData != null )
                {
                    i_Response.getWriter().println(v_SSOCallBack + "('" + v_SessionData.paramStr + "');");
                }
            }
            
            return;
        }
        
        // 验证登录
        if ( null == v_SessionData )
        {
            // 单点登陆
            if ( !Help.isNull(v_USID) )
            {
                v_SessionData = (Return<Object>)XJava.getObject(v_USID);
                
                if ( v_SessionData == null )
                {
                    // 单点已退出 或 会话已超时过期
                    return;
                }
                else
                {
                    // 跨域单点登陆成功
                    i_Request.getSession().setMaxInactiveInterval(Cluster.getSSOSessionTimeOut());
                    i_Request.getSession().setAttribute($SessionID ,v_SessionData);
                    
                    // 保持集群会话活力及有效性
                    Cluster.loginCluster(v_SessionData.paramStr ,v_SessionData);
                }
            }
        }
        else
        {
            v_SessionData = (Return<Object>)XJava.getObject(v_SessionData.paramStr);
            
            if ( v_SessionData == null )
            {
                // 单点已退出 或 会话已超时过期
                i_Request.getSession().removeAttribute($SessionID);
                return;
            }
            else
            {
                // 保持集群会话活力及有效性
                Cluster.loginCluster(v_SessionData.paramStr ,v_SessionData);
            }
        }
    }
    
    
    
    public void doPost(HttpServletRequest i_Request, HttpServletResponse i_Response) throws ServletException, IOException 
    {
        this.doGet(i_Request ,i_Response);
    }
    
}
