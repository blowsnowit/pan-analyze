package cn.bload.share.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/2/19 13:01
 * @describe 类描述:
 */
@Data
public class PanTree implements Serializable {
    private String name;
    private String size;
    private String md5;

    //创建时间
    private String ctime;
    //修改时间
    private String mtime;


    private List<PanTree> childrens;

    public PanTree() {
    }

    public PanTree(JSONObject o) {
        this.setName(o.get("server_filename").toString());
        this.setSize(o.get("size").toString());
        if (o.get("md5") != null){
            this.setMd5(o.get("md5").toString());
        }
        this.setCtime(o.get("server_ctime").toString());
        this.setMtime(o.get("server_mtime").toString());
    }

    private String doParseTimeStamp(String time){
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int timeStamp = (int)(date.getTime() / 1000);
        return "" + timeStamp;
    }
    public void setCtimeTimeStamp(String time){
        this.setCtime(doParseTimeStamp(time));
    }


    public void setMtimeTimeStamp(String time){
        this.setMtime(doParseTimeStamp(time));
    }
}
