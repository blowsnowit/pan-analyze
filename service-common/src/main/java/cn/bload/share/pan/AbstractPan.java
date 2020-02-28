package cn.bload.share.pan;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bload.share.model.PanTree;
import cn.bload.share.model.WorkMessage;
import lombok.Data;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/27 下午 9:42
 * @describe 类描述:
 */
@Data
public abstract class AbstractPan{

    protected String url;

    protected String password;

    protected List<String> cookies;

    protected Map<String,Object> params = new HashMap<>();


    public AbstractPan(String url, String password){
        this.url = url;
        this.password = password;
    }


    public List<HttpCookie> getHttpCookies(){
        List<HttpCookie> list = new ArrayList<>();
        for (String cookie : cookies) {
            String[] arr = cookie.split("=");
            if (arr.length == 2){
                list.add(new HttpCookie(arr[0],arr[1]));
            }
        }
        return list;
    }

    public void setHttpCookies(List<HttpCookie> cookies){
        List<String> list = new ArrayList<>();
        for (HttpCookie cookie : cookies) {
            list.add(cookie.toString());
        }
        this.cookies = list;
    }

    //用来验证链接是否正常等
    abstract public boolean init();


    //获取目录树
    abstract public List<PanTree> getTree();

    //判断是否允许解析
    public static boolean checkPanUrl(String url) {
        return false;
    }

    public String getParamsStr(String key){
        if (!params.containsKey(key)) return null;
        return params.get(key).toString();
    }

    public WorkMessage getWorkMessage(String key){
        WorkMessage workMessage = new WorkMessage();
        workMessage.setKey(key);
        workMessage.setUrl(url);
        workMessage.setPassword(password);
        workMessage.setCookies(this.getCookies());
        workMessage.setParams(params);
        return workMessage;
    }


    public static AbstractPan getPan(WorkMessage workMessage){
        AbstractPan pan = PanFactory.getPan(workMessage.getUrl(), workMessage.getPassword());
        pan.setParams(workMessage.getParams());
        pan.setCookies(workMessage.getCookies());
        return pan;
    }
}
