package com.adsyst.light_project_mobile.ui.starting;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.local_storage.provider.DbHandler;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.ui.Balance;
import com.adsyst.light_project_mobile.ui.Partenaires;
import com.adsyst.light_project_mobile.ui.Settings;
import com.adsyst.light_project_mobile.ui.fragments.HomeFragmentCallBox;
import com.adsyst.light_project_mobile.ui.fragments.HomeFragmentUser;
import com.adsyst.light_project_mobile.ui.fragments.LocationMarchandFragment;
import com.adsyst.light_project_mobile.ui.fragments.NotificationsFragment;
import com.adsyst.light_project_mobile.ui.fragments.StatistiquesFragment;
import com.adsyst.light_project_mobile.ui.historical.HomeHistorical;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerHeaderItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DrawerView drawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private Toolbar toolbar;
    private Context context;
    private PrefManager prf;
    private String firstName = "";
    private String lastName = "";
    private DbHandler db;
    @BindView(R.id.l_item_notifications)
    BubbleToggleView l_item_notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initItem();
        initDrawerLayout();
        initBottomView();
    }

    private void tutorial() {
        toolbar.inflateMenu(R.menu.menu);
        //toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp));
        // We load a drawable and create a location to show a tap target here
        // We need the display to get the width and height at this point in time
        final Display display = getWindowManager().getDefaultDisplay();
        // Load our little droid guy
        final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_baseline_close_24);
        // Tell our droid buddy where we want him to appear
        final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
        // Using deprecated methods makes you look way cool
        droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);

        final SpannableString sassyDesc = new SpannableString("It allows you to go back, sometimes");
        sassyDesc.setSpan(new StyleSpan(Typeface.ITALIC), sassyDesc.length() - "sometimes".length(), sassyDesc.length(), 0);


        final TapTargetSequence seq = new TapTargetSequence(this)
                .targets(
                        TapTarget.forBounds(droidTarget, "Oh look!", "You can point to any part of the screen. You also can't cancel this one!")
                                .cancelable(false)
                                .icon(droid)
                                .id(1),
                        TapTarget.forToolbarNavigationIcon(toolbar, "This is the back button", sassyDesc).id(2),
                        TapTarget.forToolbarOverflow(toolbar, "This will show more options", "But they're not useful :(").id(3),
                        /*TapTarget.forToolbarMenuItem(toolbar, R.id.search1, "This is a search icon", "As you can see, it has gotten pretty dark around here...")
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.secondary_color)
                                .targetCircleColor(android.R.color.black)
                                .transparentTarget(true)
                                .textColor(android.R.color.black)
                                .id(4),*/
                        TapTarget.forView(findViewById(R.id.l_item_home), "Accueil", "Page d'accueil de l'application")
                                .dimColor(android.R.color.darker_gray)
                                .outerCircleColor(R.color.primary_color_2)
                                .targetCircleColor(R.color.secondary_color_2)
                                .cancelable(false)
                                .drawShadow(true)
                                .titleTextDimen(R.dimen.title_text_size)
                                .textColor(android.R.color.white)
                                .id(5),
                        TapTarget.forView(findViewById(R.id.l_item_transactions), "Transactions", "Page d'accueil de l'application").id(6),
                        TapTarget.forView(findViewById(R.id.l_item_point_marchands), "Points Marchands", "Liste des points marchands sur la carte").id(7),
                        TapTarget.forView(findViewById(R.id.l_item_notifications), "Notifications", "Liste des notifications").id(8)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });


        seq.start();

    }

    private void initItem() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.accueil));
        toolbar.setSubtitle(getString(R.string.subTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;
        ButterKnife.bind(this);
        prf = new PrefManager(context);
        db = new DbHandler(getApplicationContext());
        if (prf.getBoolean("tutorial")) {
            prf.setBoolean("tutorial",false);
            tutorial();
        }

        firstName = prf.getString("key_prenom_client").toUpperCase();
        lastName = prf.getString("key_nom_client").toUpperCase();
    }

    private void initDrawerLayout() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer = (DrawerView) findViewById(R.id.drawer);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.secondary_color));
        drawerLayout.addDrawerListener(drawerToggle);
        drawerLayout.closeDrawer(drawer);


        drawer.setDrawerTheme(
                new DrawerTheme(this)
                        //.setBackgroundColorRes(R.color.primary_color)
                        //.setTextColorPrimaryRes(R.color.secondary_color)
                        //.setTextColorSecondaryRes(R.color.secondary_color)
                        //.setTextColorPrimaryInverseRes(R.color.primary_color)
                        //.setTextColorSecondaryInverseRes(R.color.secondary_color)
                        .setHighlightColorRes(R.color.primary_color)
        );


        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, R.drawable.solde_bleu))
                .setTextPrimary(getString(R.string.solde))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        Intent intent = new Intent(context, Balance.class);
                        startActivity(intent);
                    }
                })
        );


        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, R.drawable.list_partenaire_bleu))
                .setTextPrimary(getString(R.string.partenaires))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        Intent intent = new Intent(context, Partenaires.class);
                        startActivity(intent);
                    }
                })
        );


        drawer.addItem(new DrawerItem()
                .setImage(ContextCompat.getDrawable(this, R.drawable.historique_bleu))
                .setTextPrimary(getString(R.string.historique))
        );

        drawer.addItem(new DrawerItem()
                .setImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.impression_bleu), DrawerItem.SMALL_AVATAR)
                .setTextPrimary(getString(R.string.impression))
        );

        drawer.selectItem(1);
        drawer.addItem(new DrawerHeaderItem().setTitle(getString(R.string.configuration)));
        drawer.addItem(new DrawerItem()
                .setRoundedImage((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.parametre_bleu), DrawerItem.SMALL_AVATAR)
                .setTextPrimary(getString(R.string.configuration))
                .setTextSecondary(getString(R.string.setting), DrawerItem.THREE_LINE)
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        Intent intent = new Intent(MainActivity.this, Settings.class);
                        startActivity(intent);
                    }
                })
        );


        drawer.addDivider();
        drawer.addItem(new DrawerItem()
                .setImage((Drawable) ContextCompat.getDrawable(this, R.drawable.ic_baseline_exit_to_app_24))
                .setTextPrimary(getString(R.string.deconnexion))
                .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                    @Override
                    public void onClick(DrawerItem drawerItem, long l, int i) {
                        Deconnexion();
                    }
                })
        );

        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem item, long id, int position) {
                drawer.selectItem(position);
                switch (position){
                    case 2:
                        Intent intent = new Intent(MainActivity.this, HomeHistorical.class);
                        startActivity(intent);
                        break;
                    case 3:
                        Toast.makeText(context, getString(R.string.impression), Toast.LENGTH_SHORT).show();
                        break;
                }
                //Toast.makeText(MainActivity.this,"N°" + position, Toast.LENGTH_SHORT).show();
            }
        });

        drawer.addProfile(new DrawerProfile()
                .setId(1)
                .setAvatar(ContextCompat.getDrawable(this, R.drawable.ic_baseline_account_circle_24))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.logo_ampoule))
                .setName(lastName)
                .setDescription(firstName)
                .setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
                    @Override
                    public void onClick(DrawerProfile drawerProfile, long id) {
                        Toast.makeText(MainActivity.this, getString(R.string.profil) + id, Toast.LENGTH_SHORT).show();
                    }
                })
        );

        /*drawer.addProfile(new DrawerProfile()
                .setId(2)
                .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.cat_2))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.cat_wide_1))
                .setName(getString(R.string.lorem_ipsum_short))
        );

        drawer.addProfile(new DrawerProfile()
                .setId(3)
                .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.cat_1))
                .setBackground(ContextCompat.getDrawable(this, R.drawable.cat_wide_2))
                .setName(getString(R.string.lorem_ipsum_short))
                .setDescription(getString(R.string.lorem_ipsum_medium))
        );*/


        drawer.setOnProfileClickListener(new DrawerProfile.OnProfileClickListener() {
            @Override
            public void onClick(DrawerProfile profile, long id) {
                Toast.makeText(MainActivity.this, "Clicked profile *" + id, Toast.LENGTH_SHORT).show();
            }
        });
        drawer.setOnProfileSwitchListener(new DrawerProfile.OnProfileSwitchListener() {
            @Override
            public void onSwitch(DrawerProfile oldProfile, long oldId, DrawerProfile newProfile, long newId) {
                Toast.makeText(MainActivity.this, "Switched from profile *" + oldId + " to profile *" + newId, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initBottomView() {

        // init pager view

        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(100);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //check role user
        if (prf.getString("key_id_role").equals("22")){
            adapter.addFragment(new HomeFragmentUser());
        }else {
            adapter.addFragment(new HomeFragmentCallBox());
        }
        adapter.addFragment(new StatistiquesFragment());
        adapter.addFragment(new LocationMarchandFragment());
        adapter.addFragment(new NotificationsFragment().newInstance("1"));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        final BubbleNavigationConstraintView bubbleNavigationLinearView = findViewById(R.id.top_navigation_constraint);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationLinearView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });
    }


    @Override
    public void onBackPressed() {
            Deconnexion();
    }

    private void Deconnexion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(getString(R.string.confirmExit));
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_exit_to_app_24);
        alertDialogBuilder.setMessage(getString(R.string.confirmExitMsg));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.oui), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
                dialog.dismiss();
                finish();
                finishAffinity();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.non), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.apropos:
                prf.setBoolean("tutorial",true);
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


     private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }



    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page login
     *
     * @param newBase
     * @since 2020
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }



    @Override
    protected void onResume() {
        super.onResume();
        l_item_notifications.setBadgeText(String.valueOf(db.GetNumNotifications()));
    }
}