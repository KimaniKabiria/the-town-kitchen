package com.codepath.the_town_kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.adapters.OrderItemAdapter;
import com.codepath.the_town_kitchen.fragments.ProgressBarDialog;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;
import com.codepath.the_town_kitchen.utilities.UIUtility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryActivity extends TheTownKitchenBaseActivity {
    Button bSubmitOrder;
    ImageView ivEdit;
    ProgressBarDialog progressBarDialog;

    private ListView lvOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderItem> orderItems;
    private TextView tvOrderTotal;
    private TextView tvSubTotal;
    private TextView tvDeliveryTime;
    private TextView tvAddress;
    private TextView tvTax;
    private TextView tvDiscount;
    private Order orderToSave;
    private TextView tvDiscountlabel;
    private RelativeLayout llDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        bSubmitOrder = (Button) findViewById(R.id.bSubmitOrder);
        bSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPogressBarDialog();
            }
        });

        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderSummaryActivity.this, PaymentInfoActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);

            }
        });
        tvOrderTotal = (TextView) findViewById(R.id.tvOrderTotal);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvSubTotal= (TextView) findViewById(R.id.tvSubTotal);
        tvTax= (TextView) findViewById(R.id.tvTax);
        tvDiscount= (TextView) findViewById(R.id.tvDiscount);
        tvDiscountlabel =  (TextView) findViewById(R.id.tvDiscountlabel);
        llDiscount = (RelativeLayout) findViewById(R.id.llDiscount);
        //order items
        lvOrderItems = (ListView) findViewById(R.id.lvOrderItems);
        tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime);
        Order.getLastOrderByDate(TheTownKitchenApplication.orderDate, new Order.IOrderReceivedListener() {
            @Override
            public void handle(Order orderFromParse, List<OrderItem> orderItemsFromParse) {
                if (orderFromParse != null) {

                    setFormattedDateTime(orderFromParse.getDate(), orderFromParse.getTime());

                    orderItems = orderItemsFromParse;
                    if (orderItems == null)
                        orderItems = new ArrayList<>();
                    orderItemAdapter = new OrderItemAdapter(OrderSummaryActivity.this, orderItems, null);
                    lvOrderItems.setAdapter(orderItemAdapter);
                    double subTotal = orderFromParse.getCost();
                    double tax = orderFromParse.getCost() * 0.09;
                    tvSubTotal.setText("$" + subTotal);
                    tvTax.setText("$" + new DecimalFormat("##.##").format(tax));
                    tvOrderTotal.setText("$" + new DecimalFormat("##.##").format(subTotal + tax));

                    String deliveryLocation = orderFromParse.getDeliveryLocation();
                    if (deliveryLocation!= null && deliveryLocation.contains("USA")) {
                        deliveryLocation = deliveryLocation.replace("USA", "");
                    }
                    tvAddress.setText(deliveryLocation);

                    orderToSave = orderFromParse;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showPogressBarDialog() {
        FragmentManager fm = getSupportFragmentManager();
        progressBarDialog = ProgressBarDialog.newInstance();
        progressBarDialog.show(fm, "fragment_progress_bar");


        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                saveOrder();
                progressBarDialog.dismiss();
                startMealListActivity();
            }
        }, 1000);
    }

    private void saveOrder() {
        if(orderItems != null) {
            for (OrderItem orderItem : orderItems) {
                orderItem.getMeal().quantityOrdered = 0;
            }
        }
        orderToSave.setIsPlaced(true);
        orderToSave.saveInBackground();
    }

    private void startMealListActivity(){
        Intent startIntent = new Intent(this, MealListActivity.class);
        this.startActivity(startIntent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

    }

    public void onCouponCodeSubmit(View view) {
        EditText etCouponCode = (EditText) findViewById(R.id.etCouponCode);
        tvDiscountlabel.setVisibility(View.VISIBLE);
        tvDiscount.setText("$-" + new DecimalFormat("##.##").format(orderToSave.getCost() * 0.15));
        tvDiscountlabel.setVisibility(View.VISIBLE);
        tvDiscount.setVisibility(View.VISIBLE);
        orderToSave.setCost(orderToSave.getCost() * 0.85);
        tvOrderTotal.setText("$" + new DecimalFormat("##.##").format(orderToSave.getCost()));
        UIUtility.expand(llDiscount);

        etCouponCode.setText("");
    }

    private void setFormattedDateTime(String date, String time) {
        try {
            String[] datePieces = date.split("-");
            String year = datePieces[0];
            String month = datePieces[1];
            String day = datePieces[2];
            switch (month) {
                case "01":
                    month = "January";
                    break;
                case "02":
                    month = "February";
                    break;
                case "03":
                    month = "March";
                    break;
                case "04":
                    month = "April";
                    break;
                case "05":
                    month = "May";
                    break;
                case "06":
                    month = "June";
                    break;
                case "07":
                    month = "July";
                    break;
                case "08":
                    month = "August";
                    break;
                case "09":
                    month = "September";
                    break;
                case "10":
                    month = "October";
                    break;
                case "11":
                    month = "November";
                    break;
                case "12":
                    month = "December";
                    break;
            }
            tvDeliveryTime.setText(month + " " + day + ", " + year + " at " + time);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

}
