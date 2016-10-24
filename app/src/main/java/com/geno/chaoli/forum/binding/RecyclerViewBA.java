package com.geno.chaoli.forum.binding;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.geno.chaoli.forum.BR;
import com.geno.chaoli.forum.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianhao on 16-9-19.
 */
public class RecyclerViewBA {
    private static final String TAG = "RVAdapter";

    @BindingAdapter("app:position")
    public static void setPosition(RecyclerView recyclerView, int position) {
        recyclerView.smoothScrollToPosition(position);
    }

    @BindingAdapter({"app:itemList", "app:selector", "app:handler"})
    @SuppressWarnings("unchecked")
    public static void setItems(RecyclerView recyclerView, ObservableList newItems, LayoutSelector layoutSelector, BaseViewModel viewModel) {
        if (recyclerView.getAdapter() == null) {
            MyAdapter adapter = new MyAdapter(layoutSelector, newItems);
            adapter.setHandler(viewModel);
            recyclerView.setAdapter(adapter);
        } else {
            MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
            List oldItems = adapter.getItemList();
            adapter.setItemList(newItems);
            if ((newItems.size() > 0 && newItems.get(0) instanceof DiffItem) || (oldItems.size() > 0 && oldItems.get(0) instanceof DiffItem)) {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(oldItems, newItems), true);
                diffResult.dispatchUpdatesTo(adapter);
            } else {
                if (oldItems.size() == 0) {
                    adapter.notifyDataSetChanged();
                    newItems.addOnListChangedCallback(adapter.onListChangedCallback);
                }
            }
        }
    }

    @BindingAdapter({"app:itemList", "app:itemRes", "app:handler"})
    @SuppressWarnings("unchecked")
    public static void setItems(RecyclerView recyclerView, ObservableList newItems, int itemRes, BaseViewModel viewModel) {
        if (recyclerView.getAdapter() == null) {
            MyAdapter adapter = new MyAdapter(new DefaultLayoutSelector(itemRes), newItems);
            adapter.setHandler(viewModel);
            recyclerView.setAdapter(adapter);
        } else {
            MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
            List oldItems = adapter.getItemList();
            adapter.setItemList(newItems);
            if ((newItems.size() > 0 && newItems.get(0) instanceof DiffItem) || (oldItems.size() > 0 && oldItems.get(0) instanceof DiffItem)) {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(oldItems, newItems), true);
                diffResult.dispatchUpdatesTo(adapter);
            } else {
                if (oldItems.size() == 0) {
                    adapter.notifyDataSetChanged();
                    newItems.addOnListChangedCallback(adapter.onListChangedCallback);
                }
            }
        }
    }

    @BindingAdapter({"app:itemList", "app:selector"})
    @SuppressWarnings("unchecked")
    public static void setItems(RecyclerView recyclerView, ObservableList newItems, LayoutSelector layoutSelector) {
        if (recyclerView.getAdapter() == null) {
            Log.d(TAG, "setItems: " + newItems.size());
            MyAdapter adapter = new MyAdapter(layoutSelector, newItems);
            recyclerView.setAdapter(adapter);
        } else {
            MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
            List oldItems = adapter.getItemList();
            adapter.setItemList(newItems);
            if ((newItems.size() > 0 && newItems.get(0) instanceof DiffItem) || (oldItems.size() > 0 && oldItems.get(0) instanceof DiffItem)) {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(oldItems, newItems), true);
                diffResult.dispatchUpdatesTo(adapter);
            } else {
                if (oldItems.size() == 0) {
                    adapter.notifyDataSetChanged();
                    newItems.addOnListChangedCallback(adapter.onListChangedCallback);
                }
            }
        }
    }

    @BindingAdapter({"app:itemList", "app:itemRes"})
    @SuppressWarnings("unchecked")
    public static void setItems(RecyclerView recyclerView, ObservableList newItems, int itemRes) {
        if (recyclerView.getAdapter() == null) {
            MyAdapter adapter = new MyAdapter(new DefaultLayoutSelector(itemRes), newItems);
            recyclerView.setAdapter(adapter);
        } else {
            MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
            List oldItems = adapter.getItemList();
            adapter.setItemList(newItems);
            if ((newItems.size() > 0 && newItems.get(0) instanceof DiffItem) || (oldItems.size() > 0 && oldItems.get(0) instanceof DiffItem)) {
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(oldItems, newItems), true);
                diffResult.dispatchUpdatesTo(adapter);
            } else {
                if (oldItems.size() == 0) {
                    adapter.notifyDataSetChanged();
                    newItems.addOnListChangedCallback(adapter.onListChangedCallback);
                }
            }
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        //int resId;
        List itemList;
        BaseViewModel handler;
        LayoutSelector selector;
        ObservableList.OnListChangedCallback onListChangedCallback = new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList observableList) {
                MyAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList observableList, int i, int i1) {
                MyAdapter.this.notifyItemRangeChanged(i, i1);
            }

            @Override
            public void onItemRangeInserted(ObservableList observableList, int i, int i1) {
                MyAdapter.this.notifyItemRangeInserted(i, i1);
            }

            @Override
            public void onItemRangeMoved(ObservableList observableList, int i, int i1, int i2) {
                for (int i3 = 0; i3 < i1; i3++) {
                    MyAdapter.this.notifyItemMoved(i + i3, i2 + i3);
                }
            }

            @Override
            public void onItemRangeRemoved(ObservableList observableList, int i, int i1) {
                MyAdapter.this.notifyItemRangeRemoved(i, i1);
            }
        };

        /*@SuppressWarnings("unchecked")
        MyAdapter(int resId, ArrayList itemList) {
            this.resId = resId;
            this.itemList = new ArrayList(itemList);
        }*/

        @SuppressWarnings("unchecked")
        MyAdapter(LayoutSelector layoutSelector, List itemList) {
            this.selector = layoutSelector;
            this.itemList = new ArrayList(itemList);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            ViewDataBinding binding = holder.getBinding();
            binding.setVariable(BR.item, itemList.get(position));
            if (handler != null) {
                binding.setVariable(BR.handler, handler);
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return selector.getType(itemList.get(position));
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), selector.getLayout(viewType), parent, false);
            return new MyViewHolder(binding);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ViewDataBinding binding;
            MyViewHolder(ViewDataBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public ViewDataBinding getBinding() {
                return binding;
            }
        }

        public List getItemList() {
            return itemList;
        }

        @SuppressWarnings("unchecked")
        public void setItemList(List itemList) {
            this.itemList = new ArrayList(itemList);
        }

        public BaseViewModel getHandler() {
            return handler;
        }

        public void setHandler(BaseViewModel handler) {
            this.handler = handler;
        }

        public LayoutSelector getSelector() {
            return selector;
        }
    }

    private static class DiffCallback extends DiffUtil.Callback {
        List<DiffItem> oldDiffItemList, newDiffItemList;

        DiffCallback(List<DiffItem> oldDiffItemList, List<DiffItem> newDiffItemList){
            this.oldDiffItemList = oldDiffItemList;
            this.newDiffItemList = newDiffItemList;
        }

        @Override
        public int getNewListSize() {
            return newDiffItemList.size();
        }

        @Override
        public int getOldListSize() {
            return oldDiffItemList.size();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            DiffItem oldDiffItem = oldDiffItemList.get(oldItemPosition);
            DiffItem newDiffItem = newDiffItemList.get(newItemPosition);
            return oldDiffItem.areContentsTheSame(newDiffItem);
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            DiffItem oldDiffItem = oldDiffItemList.get(oldItemPosition);
            DiffItem newDiffItem = newDiffItemList.get(newItemPosition);
            return oldDiffItem.areItemsTheSame(newDiffItem);
        }
    }

}
