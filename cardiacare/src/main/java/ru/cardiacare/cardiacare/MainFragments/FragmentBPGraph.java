package ru.cardiacare.cardiacare.MainFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

// График давления по данным из дневника давления
// Используется, как часть ViewPager
// Документация по графикам: http://www.android-graphview.org/

public class FragmentBPGraph extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, null);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.setTitle(getResources().getText(R.string.pressure_graph).toString());
        graph.setTitleColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        graph.setTitleTextSize(54);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(7);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(10);
        graph.getViewport().setMaxY(300);
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getSystolicData());
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(getDiastolicData());
        series2.setColor(Color.GREEN);
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
        for (int i = 0; i < results.length; i++) {
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
        for (int i = 0; i < results.length; i++) {
            DataPoint v = new DataPoint(i + 1, results[i]);
            values[i] = v;
        }
        return values;
    }
}