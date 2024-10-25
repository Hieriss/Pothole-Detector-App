// PlaceAutoSuggestAdapter.java
package com.example.prj;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class PlaceAutoSuggestAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> resultList;
    private PlacesClient placesClient;

    public PlaceAutoSuggestAdapter(Context context, int resource) {
        super(context, resource);
        placesClient = Places.createClient(context);
    }

    @Override
    public int getCount() {
        return resultList != null ? resultList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    resultList = getAutocomplete(constraint);
                    results.values = resultList;
                    results.count = resultList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private List<String> getAutocomplete(CharSequence constraint) {
        final List<String> resultList = new ArrayList<>();
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setQuery(constraint.toString())
                .setSessionToken(token)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                resultList.add(prediction.getFullText(null).toString());
            }
        }).addOnFailureListener(Throwable::printStackTrace);

        return resultList;
    }
}