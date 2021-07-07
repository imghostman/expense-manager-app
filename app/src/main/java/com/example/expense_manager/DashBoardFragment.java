package com.example.expense_manager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expense_manager.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class DashBoardFragment extends Fragment {


    //Floating Button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating Button textView

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolean
    private boolean isOpen = false;


    //Animation
    private Animation fadeOpen,fadeClose;

    //Dashboard income and expense
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;


    //Firebase...

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView= inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth =FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);



        //Connext floating button to layout

        fab_main_btn = myView.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myView.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myView.findViewById(R.id.expense_ft_btn);

        //Connext Floating text

        fab_income_txt= myView.findViewById(R.id.income_ft_text);
        fab_expense_txt= myView.findViewById(R.id.expense_ft_text);

        //Total income and expense result text
        totalIncomeResult=myView.findViewById(R.id.income_set_result);
        totalExpenseResult=myView.findViewById(R.id.expense_set_result);

        //Recycler View
        mRecyclerIncome= myView.findViewById(R.id.recycler_income);
        mRecyclerExpense= myView.findViewById(R.id.recycler_expense);



        // Animation connect
        fadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        fadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);


        fab_main_btn.setOnClickListener(v -> {
            addData();

            if(isOpen){

                fab_income_btn.startAnimation(fadeClose);
                fab_expense_btn.startAnimation(fadeClose);
                fab_income_btn.setClickable(false);
                fab_expense_btn.setClickable(false);

                fab_income_txt.startAnimation(fadeClose);
                fab_expense_txt.startAnimation(fadeClose);
                fab_income_txt.setClickable(false);
                fab_expense_txt.setClickable(false);
                isOpen=false;

            }
            else{
                fab_income_btn.startAnimation(fadeOpen);
                fab_expense_btn.startAnimation(fadeOpen);
                fab_income_btn.setClickable(true);
                fab_expense_btn.setClickable(true);

                fab_income_txt.startAnimation(fadeOpen);
                fab_expense_txt.startAnimation(fadeOpen);
                fab_income_txt.setClickable(true);
                fab_expense_txt.setClickable(true);
                isOpen=true;
            }




        });

        //Calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalSum=0;

                for(DataSnapshot mySnap:snapshot.getChildren()){

                    Data data=mySnap.getValue(Data.class);
                    totalSum+=data.getAmount();

                    String stResult =String.valueOf(totalSum);

                    totalIncomeResult.setText(stResult);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalSum=0;

                for(DataSnapshot mySnap:snapshot.getChildren()){

                    Data data=mySnap.getValue(Data.class);
                    assert data != null;
                    totalSum+=data.getAmount();

                    String stResult =String.valueOf(totalSum);

                    totalExpenseResult.setText(stResult);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //Recycler
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);



        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);




        return myView;
    }


    //Foatting Button animation
    private void ftAnimation(){
        if(isOpen){

            fab_income_btn.startAnimation(fadeClose);
            fab_expense_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(fadeClose);
            fab_expense_txt.startAnimation(fadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;

        }
        else{
            fab_income_btn.startAnimation(fadeOpen);
            fab_expense_btn.startAnimation(fadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fadeOpen);
            fab_expense_txt.startAnimation(fadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }

    }



    private void addData(){
        //Fab Button Income

        fab_income_btn.setOnClickListener(v -> incomeDataInsert());

        fab_expense_btn.setOnClickListener(v -> expenseDataInsert());

    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myView);

        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText editAmount = myView.findViewById(R.id.amount_edit);
        EditText editType = myView.findViewById(R.id.type_edit);
        EditText editNote = myView.findViewById(R.id.note_edit);

        Button btnsave= myView.findViewById(R.id.btnSave);
        Button btnCancel= myView.findViewById(R.id.btnCancel);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if(TextUtils.isEmpty(amount)){

                    editAmount.setError(("Required Field"));
                    return;
                }

                if(TextUtils.isEmpty(type)){

                    editType.setError(("Required Field"));
                    return;
                }

                int ourAmountInt = Integer.parseInt(amount);

                if(TextUtils.isEmpty(note)){

                    editNote.setError(("Required Field"));
                    return;
                }

                String id=mIncomeDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data =new Data(ourAmountInt,type,note,id,mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data Added", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myView);

        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        EditText editAmount = myView.findViewById(R.id.amount_edit);
        EditText editType = myView.findViewById(R.id.type_edit);
        EditText editNote = myView.findViewById(R.id.note_edit);

        Button btnsave = myView.findViewById(R.id.btnSave);
        Button btnCancel = myView.findViewById(R.id.btnCancel);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(amount)) {

                    editAmount.setError(("Required Field"));
                    return;
                }

                if (TextUtils.isEmpty(type)) {

                    editType.setError(("Required Field"));
                    return;
                }

                int ourAmountInt = Integer.parseInt(amount);

                if (TextUtils.isEmpty(note)) {

                    editNote.setError(("Required Field"));
                    return;
                }

                String id = mExpenseDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(ourAmountInt, type, note, id, mDate);

                assert id != null;
                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                ftAnimation();

                dialog.dismiss();

            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ftAnimation();
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>
                (
                        Data.class,
                        R.layout.dashboard_income,
                        IncomeViewHolder.class,
                        mIncomeDatabase
                ) {
            @Override
            protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data model, int i) {

                incomeViewHolder.setIncomeType((model.getType()));
                incomeViewHolder.setIncomeAmount((model.getAmount()));
                incomeViewHolder.setIncomeDate((model.getDate()));


            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(
                Data.class,
                R.layout.dashboard_expense,
                ExpenseViewHolder.class,
                mExpenseDatabase

        ) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Data model, int i) {

                expenseViewHolder.setExpenseType((model.getType()));
                expenseViewHolder.setExpenseAmount((model.getAmount()));
                expenseViewHolder.setExpenseDate((model.getDate()));


            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);

    }





    //For Income Data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {

            super(itemView);
            mIncomeView=itemView;
        }
        public void setIncomeType(String type){
            TextView mType =mIncomeView.findViewById(R.id.type_Income_ds);
            mType.setText(type);
        }

        public void setIncomeAmount(int amount){
            TextView mAmount =mIncomeView.findViewById(R.id.amount_Income_ds);
            String stAmount=String.valueOf(amount);
            mAmount.setText(stAmount);
        }

        public void setIncomeDate(String date){
            TextView mDate =mIncomeView.findViewById(R.id.date_Income_ds);
            mDate.setText(date);
        }

    }

    //For Expense Data

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mExpenseView;

        public ExpenseViewHolder(@NonNull View itemView) {

            super(itemView);
            mExpenseView=itemView;
        }
        public void setExpenseType(String type){
            TextView mType =mExpenseView.findViewById(R.id.type_Expense_ds);
            mType.setText(type);
        }

        public void setExpenseAmount(int amount){
            TextView mAmount =mExpenseView.findViewById(R.id.amount_Expense_ds);
            String stAmount=String.valueOf(amount);
            mAmount.setText(stAmount);
        }

        public void setExpenseDate(String date){
            TextView mDate =mExpenseView.findViewById(R.id.date_Expense_ds);
            mDate.setText(date);
        }

    }



}