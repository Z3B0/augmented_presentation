package se.chalmers.ocuclass.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import se.chalmers.ocuclass.R;


/**
 * Created by admin on 2015-08-27.
 */
public abstract class RecyclerViewFragment<T> extends RxFragment {


    private static final String TAG = RecyclerViewFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private Button btnRetry;
    private View.OnClickListener retryButtonClickListener;
    private TextView txtEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean loadingData = false;
    private int loadCount = 0;

    public abstract String getErrorString(Throwable ex);

    public int getErrorDrawableResource(Throwable ex) {
        return 0;
    }

    public abstract String getEmptyString();

    public int getEmptyDrawableResource() {
        return 0;
    }


    public abstract Observable<T> mainObservable();

    public View.OnClickListener getRetryButtonClickListener() {
        return null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    protected abstract void setup(View container, RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.lst_recycler);
        btnRetry = (Button) root.findViewById(R.id.btn_retry);
        txtEmpty = (TextView) root.findViewById(R.id.txt_empty);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.cnt_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onDataRefresh();
            }
        });

        txtEmpty.setText(getEmptyString());
        txtEmpty.setCompoundDrawablesWithIntrinsicBounds(0, getEmptyDrawableResource(), 0, 0);

        retryButtonClickListener = getRetryButtonClickListener();

        setup(root, recyclerView, swipeRefreshLayout);


        if(refreshOnScroll()) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!loadingData) {
                        swipeRefreshLayout.setEnabled(scrollRefreshEnabled());
                    }
                }
            });
        }

        if(recyclerView.getAdapter()==null){
            throw new IllegalStateException("RecyclerView Adapter is null, add it in setup");
        }


        return root;
    }

    protected boolean refreshOnScroll() {
        return true;
    }


    protected void onDataRefresh() {
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
        }
    }

    protected boolean scrollRefreshEnabled() {
        //Log.d(TAG, "Calling refresh enabled");
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition() == 0;
        }
        return true;
    }

    public final void loadData() {

        swipeRefreshLayout.setEnabled(false);

        if(loadCount > 0||showFirstLoadingIndicator()){
            postRefreshing(true);
        }

        loadCount++;
        loadingData = true;

        onDataLoad();
        mainObservable().concatMap(new Func1<T, Observable<T>>() {
            @Override
            public Observable<T> call(T t) {
                onCompleteBackground(t);
                return Observable.just(t);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<T>bindToLifecycle()).subscribe(new Action1<T>() {
            @Override
            public void call(T t) {

                onComplete(t);
                toggleList(recyclerView.getAdapter().getItemCount()==0?false:true);
                loadingData = false;
                if(refreshOnScroll()) {
                    swipeRefreshLayout.setEnabled(true);
                }
                postRefreshing(false);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                loadingData = false;
                if(refreshOnScroll()) {
                    swipeRefreshLayout.setEnabled(true);
                }
                postRefreshing(false);
                throwable.printStackTrace();
                if (onError(throwable)) {

                    toggleList(false);
                    txtEmpty.setText(getErrorString(throwable));
                }
            }
        });
    }

    protected boolean showFirstLoadingIndicator() {
        return true;
    }

    private void postRefreshing(final boolean refreshing){
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(refreshing);
            }
        });
    }

    protected void onDataLoad() {

    }

    protected abstract void onComplete(T t);

    protected void toggleList(boolean show) {

        recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
        txtEmpty.setVisibility(show ? View.GONE : View.VISIBLE);
        btnRetry.setVisibility(!show && retryButtonClickListener != null ? View.VISIBLE : View.GONE);
    }

    protected void onCompleteBackground(T t) {

    }


    protected boolean onError(Throwable ex) {
        Toast.makeText(getContext(), getErrorString(ex), Toast.LENGTH_LONG);
        return true;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {

        swipeRefreshLayout.setEnabled(refreshEnabled);
    }
}
