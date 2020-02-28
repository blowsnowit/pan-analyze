package cn.bload.share.pan;


import com.fasterxml.jackson.annotation.JsonIgnore;

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

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/2/19 11:46
 * @describe 类描述:
 */
public class BaiduPan extends AbstractPan{
    private final Integer APPID = 250528;
    private String surl;

    public BaiduPan(String url, String password) {
        super(url, password);
    }


    @Override
    public boolean init(){
        HttpRequest request = HttpUtil.createGet(this.url);
        HttpResponse response = request.execute();

        this.setHttpCookies(new ArrayList<>(response.getCookies()));
        HttpCookie cookie = response.getCookie("BDCLND");
        if (cookie == null){
            if (response.header("Location").indexOf("https://pan.baidu.com/share/init") == -1){
                return false;
            }
            cookie = this.verify();
            cookies.add(cookie.toString());
        }
        return true;
    }

    @Override
    public List<PanTree> getTree() {
        return this.getSonTree("");
    }

    public static boolean checkPanUrl(String url) {
        return url.indexOf("pan.baidu.com") != -1;
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


    //获取子目录列表
    @JsonIgnore
    private List<PanTree> getSonTree(String path) {
        List<PanTree> tree = new ArrayList<>();
        String url = null;
        try {
            url = "https://pan.baidu.com/share/list?&web=5&app_id="
                    + APPID
                    + "&channel=chunlei&clienttype=5&desc=1&showempty=0&page=1&num=20&order=time&shorturl="
                    + getShortUrl()
                    + ("".equals(path) ? "&root=1" : "&dir=" + URLEncoder.encode(path,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpUtil.createGet(url);

        request.cookie(this.getHttpCookies().toArray(new HttpCookie[cookies.size()]));

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

    @JsonIgnore
    private String getShortUrl(){
        return this.url.substring(this.url.lastIndexOf("/") + 2);
    }
}



