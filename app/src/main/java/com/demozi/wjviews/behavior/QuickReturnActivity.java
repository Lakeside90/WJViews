package com.demozi.wjviews.behavior;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demozi.wjviews.R;

public class QuickReturnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_return);

        RecyclerView view = (RecyclerView) findViewById(R.id.recycler_view);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(new HomeAdapter());
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
    {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            holder.section_label.setText("HHAHH" + position);
        }

        @Override
        public int getItemCount()
        {
            return 40;
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            TextView section_label;

            public MyViewHolder(View view)
            {
                super(view);
                section_label = (TextView) view.findViewById(R.id.section_label);
            }
        }
    }

}
