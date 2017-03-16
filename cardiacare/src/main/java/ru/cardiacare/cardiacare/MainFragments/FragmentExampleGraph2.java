package ru.cardiacare.cardiacare.MainFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import ru.cardiacare.cardiacare.R;

// Пример графика
// Используется, как часть ViewPager
// Документация по графикам: http://www.android-graphview.org/

public class FragmentExampleGraph2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, null);
        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.setTitle("График не давления");
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(7);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);
        graph.getGridLabelRenderer().setNumHorizontalLabels(7);

        BarGraphSeries<DataPoint> series3 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(1, 1),
                new DataPoint(2, 2),
                new DataPoint(3, 3),
                new DataPoint(4, 4),
                new DataPoint(5, 5),
                new DataPoint(6, 6),
                new DataPoint(7, 7),
        });
        graph.addSeries(series3);

        series3.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });
        return view;
    }

    public FragmentExampleGraph2() {
    }

    public static FragmentExampleGraph2 newInstance() {
        FragmentExampleGraph2 f = new FragmentExampleGraph2();
        return f;
    }
}