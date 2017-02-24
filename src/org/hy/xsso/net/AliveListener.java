package org.hy.xsso.net;

import org.hy.common.Date;
import org.hy.common.ExpireMap;
import org.hy.common.Help;
import org.hy.common.net.CommunicationListener;
import org.hy.common.net.data.CommunicationRequest;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.xml.XJava;
import org.hy.xsso.common.AppCluster;
import org.hy.xsso.common.Log;





/**
 * 保持会话活力及有效性的通讯监听事件 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-04
 * @version     v1.0
 */
public class AliveListener implements CommunicationListener
{
    
    private static ExpireMap<String ,Date> $CacheTimes;
    
    
    
    public AliveListener()
    {
        if ( $CacheTimes == null )
        {
            $CacheTimes = new ExpireMap<String ,Date>();
        }
    }
    
    
    
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
        return "alive";
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
        
        XJava.putObject(i_RequestData.getDataXID() ,i_RequestData.getData() ,i_RequestData.getDataExpireTimeLen());
        
        int     v_Interval = Integer.parseInt(XJava.getParam("AliveIntervalTime").getValue());
        Date    v_Time     = $CacheTimes.get(i_RequestData.getDataXID());
        boolean v_IsPush   = (v_Time == null);
        
        if ( v_IsPush )
        {
            Log.log(":USID L保持集群会话活力。" ,i_RequestData.getDataXID());
            
            if ( v_Interval > 0 )
            {
                $CacheTimes.put(i_RequestData.getDataXID() ,new Date() ,v_Interval);
            }
            
            AppCluster.aliveCluster(i_RequestData.getDataXID() ,i_RequestData.getData() ,i_RequestData.getDataExpireTimeLen());
        }
        
        v_ResponseData.setDataXID(i_RequestData.getDataXID());
        return v_ResponseData;
    }
    
}
