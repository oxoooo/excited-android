package ooo.oxo.excited.fragment;



import com.trello.rxlifecycle.components.support.RxFragment;

import ooo.oxo.excited.fragment.callback.AppBarCallback;
import ooo.oxo.excited.fragment.callback.ContainerCallback;

/**
 * Created by seasonyuu on 2016/12/23.
 */

public class BaseFragment extends RxFragment {
    public AppBarCallback appBarCallback;
    public ContainerCallback containerCallback;

    public void setAppBarCallback(AppBarCallback appBarCallback) {
        this.appBarCallback = appBarCallback;
    }

    public void setContainerCallback(ContainerCallback containerCallback) {
        this.containerCallback = containerCallback;
    }
}
