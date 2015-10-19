package se.chalmers.ocuclass.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.adapters.PresentationListAdapter;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.Slide;
import se.chalmers.ocuclass.net.PresentationListResponse;


/**
 * Created by richard on 01/10/15.
 */
public class MainFragment extends RecyclerViewFragment<PresentationListResponse> implements AdapterView.OnItemClickListener {


    private PresentationListAdapter adapter;

    @Override
    public String getErrorString(Throwable ex) {
        return "Error";
    }

    @Override
    public String getEmptyString() {
        return getString(R.string.no_presentations);
    }

    @Override
    public Observable<PresentationListResponse> mainObservable() {


        PresentationListResponse response = new PresentationListResponse();

        List<Presentation> presentationList = new ArrayList<Presentation>();

        for(int i = 0; i < 10; i++){
            Presentation presentation = new Presentation();
            presentation.setDescription("Bacon ipsum dolor amet leberkas tri-tip boudin short loin, filet mignon ham hock kielbasa doner meatloaf. Ribeye pork chop ham hock doner meatloaf. Sirloin pancetta kevin strip steak shankle turkey beef ribs shank tenderloin pig pork chop fatback biltong spare ribs short ribs.");
            presentation.setDate("2015-01-23");
            presentation.setName("Math lecture " + (i + 1));
            List<Slide> slides  = new ArrayList<>();
            for(int ii = 0; ii < 10; ii++){
                Slide slide = new Slide();
                slide.setText("Slide example "+(ii+1));
                slides.add(slide);
            }
            presentation.setSlides(slides);
            presentationList.add(presentation);
        }



        response.setPresentations(presentationList);


        return Observable.just(response);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void setup(View container, RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout) {


        adapter = new PresentationListAdapter(this,getString(R.string.slides));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onComplete(PresentationListResponse presentationListResponse) {


        adapter.setItems(presentationListResponse.getPresentations());

    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
