package se.chalmers.ocuclass.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.adapters.PresentationListAdapter;
import se.chalmers.ocuclass.model.Presentation;
import se.chalmers.ocuclass.model.Slide;
import se.chalmers.ocuclass.model.User;
import se.chalmers.ocuclass.net.RestClient;
import se.chalmers.ocuclass.ui.MainActivity;
import se.chalmers.ocuclass.ui.NfcDetectActivity;


/**
 * Created by richard on 01/10/15.
 */
public class MainFragment extends RecyclerViewFragment<List<Presentation>> implements AdapterView.OnItemClickListener {

    private static final String EXTRA_USER = "extra_user";


    private PresentationListAdapter adapter;
    private User user;

    @Override
    public String getErrorString(Throwable ex) {
        return "Error";
    }

    @Override
    public String getEmptyString() {
        return getString(R.string.no_presentations);
    }

    @Override
    public Observable<List<Presentation>> mainObservable() {


        /*PresentationListResponse response = new PresentationListResponse();

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



        response.setPresentations(presentationList);*/


        return RestClient.service().presentationList();
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

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NfcDetectActivity.startActivity(getActivity(),user,adapter.getItems().get(position));
            }
        });


    }

    @Override
    protected void onComplete(List<Presentation> presentationList) {

        Presentation dummy1 = new Presentation(presentationList.get(0));
        dummy1.setName("Neurology for Dummies");

        Presentation dummy2 = new Presentation(presentationList.get(0));
        dummy2.setName("Sustained excitability elevations");


        presentationList.add(dummy1);
        presentationList.add(dummy2);

        for(Presentation presentation : presentationList){
            presentation.setDescription("Bacon ipsum dolor amet pig turducken ground round, capicola swine fatback rump corned beef short ribs pork loin filet mignon flank leberkas shoulder venison. Beef ribs sirloin turkey brisket. Strip steak short loin ham hock bresaola pork belly, venison meatball ribeye turducken. Meatloaf rump turducken, boudin capicola salami pig frankfurter pork loin shoulder swine beef pork alcatra.");
        }

        adapter.setItems(presentationList);

    }

    public static MainFragment newInstance(User user) {
        MainFragment fragment = new MainFragment();

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER, user);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.user = (User) getArguments().getSerializable(EXTRA_USER);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
