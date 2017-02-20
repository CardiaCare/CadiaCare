package ru.cardiacare.cardiacare.MainFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import ru.cardiacare.cardiacare.R;

// Пример графика
// Используется, как часть ViewPager
// Документация по графикам: http://www.android-graphview.org/

public class FragmentExampleGraph1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, null);

        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.setTitle("График давления");
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(7);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(1, 7),
                new DataPoint(2, 4),
                new DataPoint(3, 6),
                new DataPoint(4, 3),
                new DataPoint(5, 7),
                new DataPoint(6, 4),
                new DataPoint(7, 6)
        });
        graph.addSeries(series);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(1, 4),
                new DataPoint(2, 6),
                new DataPoint(3, 3),
                new DataPoint(4, 7),
                new DataPoint(5, 4),
                new DataPoint(6, 6),
                new DataPoint(7, 5)
        });
        series2.setColor(Color.GREEN);
        graph.addSeries(series2);
        return view;
    }

    public FragmentExampleGraph1() {}

    public static FragmentExampleGraph1 newInstance() {
        FragmentExampleGraph1 f = new FragmentExampleGraph1();
        return f;
    }
}