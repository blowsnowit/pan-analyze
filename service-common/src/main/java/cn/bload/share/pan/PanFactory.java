package cn.bload.share.pan;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/28 下午 1:54
 * @describe 类描述:
 */
public class PanFactory {

    public static AbstractPan getPan(String url,String password){
        if (BaiduPan.checkPanUrl(url)){
            return new BaiduPan(url,password);
        }
        if (TyPan.checkPanUrl(url)){
            return new TyPan(url,password);
        }
        if (GitPan.checkPanUrl(url)){
            return new GitPan(url,password);
        }
        return null;
    }
}
