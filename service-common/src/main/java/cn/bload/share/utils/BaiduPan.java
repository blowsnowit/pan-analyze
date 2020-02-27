package cn.bload.share.utils;


import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.model.PanTree;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/2/19 11:46
 * @describe 类描述:
 */
public class BaiduPan {


    private final Integer APPID = 250528;
    private String url;
    private String surl;
    private String password;
    @Getter
    @Setter
    private List<HttpCookie> cookies;
    @Setter
    private List<PanTree> trees;

    public BaiduPan(String url,String password){
        this.url = url;
        this.password = password;
    }

    public void setCookies(List<HttpCookie> cookies){
        this.cookies = cookies;
    }

    public void setCookies(String cookies){
        List<HttpCookie> list = new ArrayList<>();
        String[] arr = cookies.split(";");
        for (String cookie : arr) {
            String[] arr2 = cookie.split("=");
            if (arr2.length == 2){
                list.add(new HttpCookie(arr2[0],arr2[1]));
            }
        }
        this.cookies = list;
    }

    public String getCookiesStr(){
        StringBuffer stringBuffer = new StringBuffer();
        for (HttpCookie cookie : cookies) {
            stringBuffer.append(cookie.toString()+";");
        }
        return stringBuffer.toString();
    }

    public boolean init(){
        HttpRequest request = HttpUtil.createGet(this.url);
        HttpResponse response = request.execute();
        this.cookies = new ArrayList<>(response.getCookies());
        HttpCookie cookie = response.getCookie("BDCLND");
        if (cookie == null){
            System.out.println(response);
            if (response.header("Location").indexOf("https://pan.baidu.com/share/init") == -1){
                return false;
            }
            cookie = this.verify();
            cookies.add(cookie);
        }

        System.out.println(this.getShortUrl());
        return true;
    }

    //获取分享状态
    public void checkStatus(){
        HttpRequest request = HttpUtil.createGet("https://pan.baidu.com/share/linkstatus?web=5&app_id="
                + APPID
                + "&channel=chunlei&clienttype=5&shorturl="
                + this.getShortUrl()
        );
        request.cookie(cookies.toArray(new HttpCookie[cookies.size()]));
        HttpResponse response = request.execute();
        //status 0 正常，6取消分享
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        System.out.println(jsonObject);
        Integer status = jsonObject.getInt("status");
        if (status != 0){
            if (status == 6){
                throw new MyRuntimeException("已取消分享");
            }
            if (status == -1){
                throw new MyRuntimeException("分享已过期");
            }
            throw new MyRuntimeException("该链接已失效");
        }
    }

    //获取目录树
    public List<PanTree> getTree(){
        //缓存本次目录树
        if (trees != null){
            return trees;
        }
        try {
            trees = this.getSonTree("");
            return trees;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String doTreeStr(List<PanTree> panTrees,int level){
        String meet = "┣━━";
        String str = "";
        for (PanTree panTree : panTrees) {
            for (int i = 0; i < level - 1; i++) {
                str += "\t";
            }
            str += meet + panTree.getName() + "\n";
            if (panTree.getChildrens() != null){
                str += doTreeStr(panTree.getChildrens(),level + 1);
            }
        }
        return str;
    }

    //获取解析树文本
    public String getTreeStr(){
        List<PanTree> tree = getTree();
        String treeStr = doTreeStr(tree,1);
        return treeStr;
    }


    //获取子目录列表
    private List<PanTree> getSonTree(String path) throws UnsupportedEncodingException {
        List<PanTree> tree = new ArrayList<>();
        String url = "https://pan.baidu.com/share/list?&web=5&app_id="
                + APPID
                + "&channel=chunlei&clienttype=5&desc=1&showempty=0&page=1&num=20&order=time&shorturl="
                + getShortUrl()
                + ("".equals(path) ? "&root=1" : "&dir=" + URLEncoder.encode(path,"UTF-8"));
        HttpRequest request = HttpUtil.createGet(url);
        request.cookie(cookies.toArray(new HttpCookie[cookies.size()]));
        HttpResponse response = request.execute();
        JSONObject object = JSONUtil.parseObj(response.body());
        JSONArray list = object.getJSONArray("list");
        for (int i = 0; i < list.size(); i++) {
            JSONObject o = (JSONObject) list.get(i);

            PanTree panTree = new PanTree(o);

            String isdir = o.get("isdir").toString();
            if ("1".equals(isdir)){
                panTree.setChildrens(this.getSonTree(path + "/" + panTree.getName()));
            }
            tree.add(panTree);
        }
        return tree;
    }

    //验证密码
    public HttpCookie verify(){
        String url = "https://pan.baidu.com/share/verify?surl="
                + this.getShortUrl()
                + "&web=5&app_id="
                + APPID
                + "&channel=chunlei&clienttype=5";
        HttpRequest request = HttpUtil.createPost(url);
        request.form("pwd",this.password);
        request.form("vcode","");
        request.form("vcode_str","");
        request.header("Referer","https://pan.baidu.com/wap/init?surl=" + this.getShortUrl());
//        request.cookie(cookies.toArray(new HttpCookie[cookies.size()]));
        HttpResponse response = request.execute();
        // 2 请求头错误
        // -62需要验证码
        // -12密码错误
        System.out.println(response);
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        Integer errno = jsonObject.getInt("errno");
        if (errno == -12){
            throw new MyRuntimeException("密码错误");
        }
        HttpCookie cookie = response.getCookie("BDCLND");
        if (cookie == null){
            throw new MyRuntimeException("其他错误");
        }
        return cookie;
    }

    private String getShortUrl(){
        return this.url.substring(this.url.lastIndexOf("/") + 2);
    }
}



