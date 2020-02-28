package cn.bload.share.pan;

import java.util.List;

import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.model.PanTree;
import cn.bload.share.pan.git.AbstractGit;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/28 下午 4:18
 * @describe 类描述:
 */
public class GitPan extends AbstractPan {
    private AbstractGit git;

    public GitPan(String url, String password) {
        super(url, password);
        git = AbstractGit.getGit(url);
        if (git == null){
            throw new MyRuntimeException("无法识别此类型的git地址");
        }
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public List<PanTree> getTree() {
        return git.getTree(url);
    }


    public static boolean checkPanUrl(String url) {
        return AbstractGit.getGit(url) != null;
    }
}
