package com.adsyst.light_project_mobile.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.local_storage.provider.DbHandler;
import com.adsyst.light_project_mobile.service.local_notification.adapter.CardListAdapter;
import com.adsyst.light_project_mobile.service.local_notification.helper.RecyclerItemTouchHelper;
import com.adsyst.light_project_mobile.service.local_notification.helper.RecyclerItemTouchHelperListener;
import com.adsyst.light_project_mobile.service.local_notification.model.Item;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements RecyclerItemTouchHelperListener {


    // Array of strings...
    ListView simpleList;
    String  Item1[] = {"Télécollecte", "Carte smopaye", "Manage_Recharge", "Retrait", "Retrait", "Manage_Recharge"};
    String  SubItem[] = {"Une Télécollecte sur un montant de 10 000 fcfa a été éffectué.",
            "Votre carte s'expire le 29/09/19 veuillez passer mettre à jour.",
            "Votre recharge a échoué carte invalid. Veuillez vous rapprocher.",
            "Votre retrait aupres de votre opérateur orange a reussit.",
            "Votre retrait aupres de votre opérateur orange a reussit.",
            "Votre retrait aupres de votre opérateur orange a reussit."};
    int flags[] = {R.drawable.ic_baseline_close_24, R.drawable.ic_baseline_check_circle_24, R.drawable.ic_baseline_close_24, R.drawable.ic_baseline_check_circle_24, R.drawable.ic_baseline_close_24, R.drawable.ic_baseline_check_circle_24};
    String  ItemDate[] = {"01/08/2019", "02/08/2019", "03/08/2019", "04/08/2019", "05/08/2019", "06/08/2019"};

    private FloatingActionButton btnSuppNotif;
    private LinearLayout notificationsVides;


    //https://web-service-api-smp.ws-smopaye-cm.mon.world/index.php
    String urladress = "http://bertin-mounok.com/soccer.php";
    BufferedInputStream is;
    String line = null;
    String result = null;



    //AJOUT DU RECYCLER AVEC SUPPRESSION DES NOTIFICATIONS MANUELLE
    /*************************************/
    private final String URL_API = "https://api.androidhive.info/json/menu.json";
    private RecyclerView recyclerView;
    private List<Item> list;
    private ArrayList<Item> list2;
    private CardListAdapter adapter;
    private CoordinatorLayout rootLayout;
    private EditText inputSearch;
    private RelativeLayout notif_search;
    private SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        //permet de rendre une activity en plein écran
        //getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);


        btnSuppNotif = (FloatingActionButton) view.findViewById(R.id.btnSuppNotif);
        //simpleList = (ListView) view.findViewById(R.id.ListViewNotification);
        notificationsVides = (LinearLayout) view.findViewById(R.id.notificationsVides);
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        notif_search = (RelativeLayout) view.findViewById(R.id.notif_search);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);



        //recupération des informations de la BD pendant l'authentificatiion sous forme de SESSION
        //avec les données quittant de Activity -> Fragment
        /*assert getArguments() != null;
        String retour = getArguments().getString("result_BD");
        assert retour != null;
        String[] parts = retour.split("-");*/

        //numTel = getArguments().getString("telephone");


        //BASE DE DONNEES DISTANTE
        /*******************************DEBUT******************************/
        /*simpleList = (ListView) view.findViewById(R.id.ListViewNotification);
        //cela fontionne bien. faut juste enlever les commentaires en dessous
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        //collectData();
        CustomAdapterNotification customAdapter = new CustomAdapterNotification(getActivity(), Item,SubItem, flags, ItemDate);
        simpleList.setAdapter(customAdapter);*/
        /********************************FIN*******************************/



        //BASE DE DONNEES LOCALE
        /*******************************DEBUT******************************/
        /*DbHandler db = new DbHandler(getActivity());
        ArrayList<HashMap<String, String>> userList = db.GetUsers();

        if(userList.isEmpty()){
            notificationsVides.setVisibility(View.VISIBLE);
            simpleList.setVisibility(View.GONE);

        } else {
            notificationsVides.setVisibility(View.GONE);
            simpleList.setVisibility(View.VISIBLE);
            ListAdapter adapter = new SimpleAdapter(getActivity(), userList, R.layout.listitem_notification, new String[]{"title","description", "image_notification", "dateEnreg"}, new int[]{R.id.item, R.id.subitem, R.id.image, R.id.itemDate});
            simpleList.setAdapter(adapter);
        }*/
        /********************************FIN*******************************/





        //SUPRESSION DES NOTIFICATIONS APRES GLISSEMENT
        /*******************************DEBUT******************************/
        DbHandler db = new DbHandler(getActivity());
        list2 = db.listAllNotification();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        rootLayout = (CoordinatorLayout) view.findViewById(R.id.rootlayout);

        if(list2.isEmpty()){
            notificationsVides.setVisibility(View.VISIBLE);
            notif_search.setVisibility(View.GONE);

        } else {
            adapter = new CardListAdapter(getContext(), list2, list2);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(adapter);
            ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0,    ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT|ItemTouchHelper.UP|ItemTouchHelper.DOWN, this);
            new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        }

        /********************************FIN*******************************/



        btnSuppNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list2.isEmpty()){
                    notificationsVides.setVisibility(View.VISIBLE);
                    notif_search.setVisibility(View.GONE);
                } else {
                    db.DeleteAllNotifications();

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);

                    Toast.makeText(getContext(), getString(R.string.notifVide), Toast.LENGTH_SHORT).show();
                    notificationsVides.setVisibility(View.VISIBLE);
                    notif_search.setVisibility(View.GONE);
                }

            }
        });

        //module de recherche
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //module d'actualisation
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(list2.isEmpty()){
                    notificationsVides.setVisibility(View.VISIBLE);
                    notif_search.setVisibility(View.GONE);

                } else {
                    adapter = new CardListAdapter(getContext(), list2, list2);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        //mise à jour des notifications vues
        if (getArguments() != null) {
            String notif = getArguments().getString("update_notif", "");
            db.UpdateNumNotification(notif);
        }

        return view;
    }



    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(viewHolder instanceof CardListAdapter.MyViewHolder){
            String name = list2.get(viewHolder.getAdapterPosition()).getName();
            final int deteteIndex = viewHolder.getAdapterPosition();
            final String id_bd = list2.get(viewHolder.getAdapterPosition()).getId();
            final Item deletedItem = list2.get(viewHolder.getAdapterPosition());

            adapter.removeItem(deteteIndex, Integer.parseInt(id_bd), getActivity());

            Snackbar snackbar = Snackbar.make(rootLayout, name + " " + getString(R.string.encoursSuppression), Snackbar.LENGTH_LONG);
            snackbar.setAction(getString(R.string.supprimer), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deletedItem, deteteIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    public static Fragment newInstance(String s) {
        NotificationsFragment myFragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString("update_notif", s);
        myFragment.setArguments(args);
        return myFragment;
    }
    /********************************FIN*******************************/


}