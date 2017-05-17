package ru.cardiacare.cardiacare.MainFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

// График давления по данным из дневника давления
// Используется, как часть ViewPager
// Документация по графикам: http://www.android-graphview.org/

public class FragmentBPGraph extends Fragment {
    int max;
    int min;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, null);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getSystolicData());
        series.setColor(ResourcesCompat.getColor(getResources(),R.color.colorAccent, null));
        series.setDrawBackground(true);
        series.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.colorAccent, null));

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(getDiastolicData());
        series2.setColor(ResourcesCompat.getColor(getResources(),R.color.colorBackground, null));
        series2.setDrawBackground(true);
        series2.setBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.colorBackground, null));

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.setTitle(getResources().getText(R.string.pressure_graph).toString());
        graph.setTitleColor(ResourcesCompat.getColor(getResources(), R.color.colorBackground, null));
        graph.setTitleTextSize(22);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(7);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(min - 30);
        graph.getViewport().setMaxY(max + 10);

        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
        gridLabelRenderer.setVerticalLabelsColor(ResourcesCompat.getColor(getResources(),R.color.colorBackground, null));
        gridLabelRenderer.setHorizontalLabelsVisible(false);
        gridLabelRenderer.setGridColor(ResourcesCompat.getColor(getResources(),R.color.colorBackground, null));

        //graph.getGridLabelRenderer().setNumHorizontalLabels(7);


        graph.addSeries(series);
        graph.addSeries(series2);
        return view;
    }

    public FragmentBPGraph() {
    }

    public static FragmentBPGraph newInstance() {
        FragmentBPGraph f = new FragmentBPGraph();
        return f;
    }

    private DataPoint[] getSystolicData() {
        String[] items = MainActivity.storage.getSystolicBP().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

        int[] results = new int[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                results[i] = Integer.parseInt(items[i]);
            } catch (NumberFormatException nfe) {
            }
        }

        DataPoint[] values = new DataPoint[results.length];
        max = results[0];

        for (int i = 0; i < results.length; i++) {
            if (max < results[i])
                max = results[i];

            DataPoint v = new DataPoint(i + 1, results[i]);
            values[i] = v;
        }
        return values;
    }

    private DataPoint[] getDiastolicData() {
        String[] items = MainActivity.storage.getDiastolicBP().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

        int[] results = new int[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                results[i] = Integer.parseInt(items[i]);
            } catch (NumberFormatException nfe) {
            }
        }

        DataPoint[] values = new DataPoint[results.length];
        min = results[0];
        for (int i = 0; i < results.length; i++) {

            if (min > results[i])
                min = results[i];

            DataPoint v = new DataPoint(i + 1, results[i]);
            values[i] = v;
        }
        return values;
    }
}