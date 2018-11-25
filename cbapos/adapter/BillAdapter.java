package com.cbasolutions.cbapos.adapter;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.Cart;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.viewmodel.BillViewModel;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by "Don" on 10/23/2017.
 * Class Functionality :-
 */

public class BillAdapter extends BaseSwipeAdapter {

    ArrayList<Item> data;
    public FragmentActivity context;
    SwipeLayout swipeLayout;
    BillViewModel billViewModel;
    Cart cart;

    public BillAdapter(ArrayList<Item> data, FragmentActivity fragContext, BillViewModel billViewModel, Cart cart) {
        this.context = fragContext;
        this.data = data;
        this.billViewModel = billViewModel;
        this.cart = cart;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.bill_list_item, null);

        swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, v.findViewById(R.id.swipe_layout));

        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }

        });

        return v;
    }

    @Override
    public void fillValues(final int position, View v) {
        Item item = data.get(position);

        TextView number = (TextView) v.findViewById(R.id.number);
        TextView name = (TextView) v.findViewById(R.id.name);
        TextView qty = (TextView) v.findViewById(R.id.qty);
        TextView price = (TextView) v.findViewById(R.id.price);

        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setHorizontallyScrolling(false);
        name.setMaxLines(1);

        final int pos = position + 1;

        number.setText(pos + ". ");
        name.setText(item.getItem_name());
        qty.setText("x"+item.getQuantity());
            price.setText(String.format("%.2f", item.getItemTotal()));

        TextView number1 = (TextView) v.findViewById(R.id.number1);
        TextView name1 = (TextView) v.findViewById(R.id.name1);
        TextView qty1 = (TextView) v.findViewById(R.id.qty1);
        TextView price1 = (TextView) v.findViewById(R.id.price1);

        number1.setText(pos + ". ");
        name1.setText(item.getItem_name());
        qty1.setText("x"+item.getQuantity());
            price1.setText(String.format("%.2f", item.getItemTotal()));

        name1.setEllipsize(TextUtils.TruncateAt.END);
        name1.setHorizontallyScrolling(false);
        name1.setMaxLines(1);

        v.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cart.deleteItemFromCart(position, data.get(position));
                List<Integer> openItems = getOpenItems();
                for (int i = 0; i < openItems.size(); i++){
                    closeItem(openItems.get(i));
                }

                if(cart.getAllItemsBill() <= 0){
                    for(int i=0; i<data.size(); i++){
                        if(data.get(i).getItemType().equals(Config.DISCOUNT)){
                            cart.deleteItemFromCart(i, data.get(i));
                            Toast.makeText(context, context.getString(R.string.discountHigherTotal), Toast.LENGTH_LONG).show();
                        }

                    }
                }


                notifyDataSetChanged();
                billViewModel.updateItemCountAndBill();


            }
        });
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}






















//        RecyclerView.Adapter<BillAdapter.BindingHolder> {
//
//
//    private List<Item> mArticles = new ArrayList<Item>();
//    private android.content.Context mContext;;
//
//
//    public BillAdapter(List<Item> mArticles, Context mContext) {
//        this.mArticles = mArticles;
//        this.mContext = mContext;
//    }
//
//    @Override
//    public BillAdapter.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////        BillListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.bill_list_item, parent, false);
////
////        return new BillAdapter.BindingHolder(binding);
//
//
//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        BillListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.bill_list_item, parent, false);
//        BindingHolder holder = new BindingHolder(binding.getRoot(), binding);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(BillAdapter.BindingHolder holder, int position) {
//        BillListItemBinding binding = holder.binding;
//        binding.setAvmq(new BillViewModel(mArticles.get(position), mContext));
//    }
//
//    @Override
//    public int getItemCount() {
//        return mArticles.size();
//    }
//
//    public static class BindingHolder extends RecyclerView.ViewHolder {
//        private BillListItemBinding binding;
//
//        public BindingHolder(View rowView, BillListItemBinding binding) {
//            super(rowView);
//            this.binding = binding;
//        }
//    }
//
//    public void removeItem(int position) {
//        mArticles.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, mArticles.size());
//    }
//
//}
//
