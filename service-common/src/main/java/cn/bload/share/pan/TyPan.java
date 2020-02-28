package cn.bload.share.pan;

import java.util.ArrayList;
import java.util.List;

import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.model.PanTree;
import cn.bload.share.utils.StringUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/27 下午 9:47
 * @describe 类描述: 天翼云盘
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TyPan extends AbstractPan{

    public TyPan(String url, String password) {
        super(url, password);
    }

    @Override
    public boolean init() {
        HttpRequest request = HttpUtil.createGet(this.url);
        HttpResponse response = request.execute();
        String body = response.body();
        System.out.println(body);
        //shareId verifyCode
        String shareId;
        String verifyCode;
        try {
            shareId = StringUtil.getSubString(body,"_shareId = '","'");
            verifyCode = StringUtil.getSubString(body,"_verifyCode = '","'");
        }catch (Exception e){
            return false;
        }

        System.out.println(shareId);
        System.out.println(verifyCode);
        if (shareId == ""){
            return false;
        }
        params.put("shareId",shareId);
        params.put("verifyCode",verifyCode);
        return true;
    }

    @Override
    public List<PanTree> getTree() {
        return doTree("");
    }

    private List<PanTree> doTree(String fileid){
        String shareId = getParamsStr("shareId");
        String verifyCode = getParamsStr("verifyCode");
        String url = "https://cloud.189.cn/v2/listShareDir.action?fileId="
                + fileid
                + "&shareId="
                + shareId
                + "&accessCode="
                + this.password
                + "&verifyCode="
                + verifyCode
                + "&orderBy=1&order=ASC&pageNum=1&pageSize=9999999";

        HttpRequest request = HttpUtil.createGet(url);
        HttpResponse response = request.execute();
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        JSONObject errorVO = jsonObject.getJSONObject("errorVO");
        if (errorVO != null){
            throw new MyRuntimeException(errorVO.getStr("errorMsg"));
        }
        JSONArray datas = jsonObject.getJSONArray("data");
        List<PanTree> panTrees = new ArrayList<>();
        for (Object data : datas) {
            JSONObject object = (JSONObject)data;
            PanTree panTree = new PanTree();
            panTree.setName(object.getStr("fileName"));
            panTree.setSize(object.getStr("fileSize"));
            panTree.setCtimeTimeStamp(object.getStr("createTime"));
            panTree.setMtimeTimeStamp(object.getStr("lastOpTime"));

            if (object.getBool("isFolder")){
                panTree.setChildrens(doTree(object.getStr("fileId")));
            }
            panTrees.add(panTree);
        }
        return panTrees;
    }

    public static boolean checkPanUrl(String url) {
        return url.indexOf("cloud.189.cn") != -1;
    }

    public static void main(String[] args){
        AbstractPan pan = new TyPan("https://cloud.189.cn/t/6BfAfiB7ZvYr", "bzr8");
        boolean init = pan.init();
        List<PanTree> tree = pan.getTree();
        System.out.println(tree);
    }
}
