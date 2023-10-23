package com.adsyst.light_project_mobile.ui.historical;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.web_service.model.ResultsHistoric;

import java.util.ArrayList;
import java.util.List;

public class AdapterHistorical extends RecyclerView.Adapter<AdapterHistorical.ViewHolder> implements Filterable {

    private static final String TAG = "AdapterHistorical";
    private Context c;
    private  List<ResultsHistoric> resultsHistoricList;
    private  List<ResultsHistoric> resultsHistoricListFilter;

    public AdapterHistorical(Context c, List<ResultsHistoric> resultsHistoricList, List<ResultsHistoric> resultsHistoricListFilter) {
        this.c = c;
        this.resultsHistoricList = resultsHistoricList;
        this.resultsHistoricListFilter = resultsHistoricListFilter;
    }

    @NonNull
    @Override
    public AdapterHistorical.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(c);
        View view = layoutInflater.inflate(R.layout.layout_historique, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHistorical.ViewHolder holder, int position) {
        holder.ShowHistorical(resultsHistoricListFilter.get(position));
    }

    @Override
    public int getItemCount() {
        return resultsHistoricListFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String key = constraint.toString();
                if(key.isEmpty()){
                    Log.w(TAG, "onResponse: 0" + resultsHistoricListFilter.size());
                    resultsHistoricListFilter = resultsHistoricList;
                } else {
                    Log.w(TAG, "onResponse: 1" + resultsHistoricListFilter.size());
                    List<ResultsHistoric> firstFilter = new ArrayList<>();
                    for(ResultsHistoric row : resultsHistoricList){
                     if(row.getNom_ben().toLowerCase().contains(key.toLowerCase())){
                            firstFilter.add(row);
                        }
                    }
                    resultsHistoricListFilter = firstFilter;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = resultsHistoricListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //resultsHistoricListFilter.clear();
                resultsHistoricListFilter = (ArrayList<ResultsHistoric>) results.values;
                notifyDataSetChanged();
            }
        };
    }



    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date_transaction;
        private TextView title_donataire_valeur;
        private TextView title_beneficiaire_valeur;
        private TextView title_frais_valeur;
        private TextView title_montant_valeur;
        private TextView idTransaction;
        private TextView tel_exp;
        private TextView tel_destinataire;

         public ViewHolder(@NonNull View itemView) {
             super(itemView);
             date_transaction = (TextView) itemView.findViewById(R.id.date_transaction);
             title_donataire_valeur = (TextView) itemView.findViewById(R.id.title_donataire_valeur);
             title_beneficiaire_valeur = (TextView) itemView.findViewById(R.id.title_beneficiaire_valeur);
             title_frais_valeur = (TextView) itemView.findViewById(R.id.title_frais_valeur);
             title_montant_valeur = (TextView) itemView.findViewById(R.id.title_montant_valeur);
             idTransaction = (TextView) itemView.findViewById(R.id.idTransaction);
             tel_exp = (TextView) itemView.findViewById(R.id.tel_exp);
             tel_destinataire = (TextView) itemView.findViewById(R.id.tel_destinataire);
         }

         @SuppressLint("SetTextI18n")
         void ShowHistorical(ResultsHistoric historic){
             if(historic.getDate_trans() != null)
             date_transaction.setText("Date: " + historic.getDate_trans());
             if(historic.getNom_client() != null)
             title_donataire_valeur.setText(historic.getNom_client());
             if(historic.getNom_ben() != null)
             title_beneficiaire_valeur.setText(historic.getNom_ben());
             if(historic.getFrais_trans() != null)
             title_frais_valeur.setText(historic.getFrais_trans() + " Fcfa");
             if(historic.getMontant_trans() != null)
             title_montant_valeur.setText(historic.getMontant_trans() + " Fcfa");
             if(historic.getNum_trans() != null)
             idTransaction.setText("ID transaction: " + historic.getNum_trans());
             if(historic.getTel_client() != null)
             tel_exp.setText("Tel " + historic.getTel_client());
             if(historic.getTel_ben() != null)
             tel_destinataire.setText("Tel " + historic.getTel_ben());
         }

     }




}
