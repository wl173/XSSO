package org.hy.xsso.total;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.net.data.Communication;
import org.hy.common.xml.SerializableDef;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;





/**
 * 统计在线用户数
 *
 * @author      ZhengWei(HY)
 * @createDate  2019-08-17
 * @version     v1.0
 */
@Xjava
public class UserTotal extends SerializableDef
{

    private static final long serialVersionUID = -686344448173546041L;
    
    
    
    /**
     * 获取在线用户数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-08-17
     * @version     v1.0
     *
     * @param i_IDPrefix  用户ID前缀。如：USID
     * @return
     */
    public Integer totalOnLineUserCount(String i_IDPrefix)
    {
        Map<String ,Object> v_Users = XJava.getObjects(i_IDPrefix);
        
        if ( Help.isNull(v_Users) )
        {
            return 0;
        }
        else
        {
            return v_Users.size();
        }
    }
    
    
    
    /**
     * 获取活跃用户数
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-08-17
     * @version     v1.0
     *
     * @param i_IDPrefix  用户ID前缀。如：USID
     * @param i_TimeLen   多长时间内为活跃。单位：毫秒
     * @return 
     */
    public Integer totalOnActivity(String i_IDPrefix ,Long i_TimeLen)
    {
        Map<String ,Object> v_Users = XJava.getObjects(i_IDPrefix);
        
        if ( Help.isNull(v_Users) )
        {
            return 0;
        }
        else
        {
            Date v_Now   = new Date();
            int  v_Count = 0;
            for (Object v_User : v_Users.values())
            {
                Communication v_CData = (Communication)v_User;
                
                if ( v_Now.differ(v_CData.getTime()) <= i_TimeLen )
                {
                    v_Count++;
                }
            }
            
            return v_Count;
        }
    }
    
}
