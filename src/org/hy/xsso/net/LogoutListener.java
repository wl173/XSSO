package org.hy.xsso.net;

import org.hy.common.Help;
import org.hy.common.net.CommunicationListener;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.XJava;
import org.hy.xsso.common.Cluster;





/**
 * 单点退出通讯监听事件 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-04
 * @version     v1.0
 */
public class LogoutListener implements CommunicationListener
{
    
    /**
     *  数据通讯的事件类型。即通知哪一个事件监听者来处理数据通讯（对应 ServerSocket.listeners 的分区标识）
     *  
     *  事件类型区分大小写
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @return
     */
    public String getEventType()
    {
        return "logout";
    }
    
    
    
    /**
     * 数据通讯事件的执行动作
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_RequestData
     * @return
     */
    public CommunicationResponse communication(CommunicationRequest i_RequestData)
    {
        CommunicationResponse v_ResponseData = new CommunicationResponse();
        
        if ( Help.isNull(i_RequestData.getDataXID()) )
        {
            v_ResponseData.setResult(1);
            return v_ResponseData;
        }
        
        v_ResponseData.setDataXID(i_RequestData.getDataXID());
        XJava.remove(i_RequestData.getDataXID());
        Cluster.logoutCluster(i_RequestData.getDataXID());
        
        return v_ResponseData;
    }
    
}
