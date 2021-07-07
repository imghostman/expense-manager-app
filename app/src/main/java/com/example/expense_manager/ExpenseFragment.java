package com.example.expense_manager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.expense_manager.model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;


public class ExpenseFragment extends Fragment {

    // TextView

    private TextView expenseTotalSum;

    //Firebase...

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    private RecyclerView recyclerView;

    // Update eit text
    private EditText editAmount;
    private EditText editType;
    private EditText editNote;


    //Buttons
    private Button btnUpdate;
    private Button btnDelete;

    //Data Item

    private String type;
    private String note;
    private String post_key;
    private int amount;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView= inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth =FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        expenseTotalSum=myView.findViewById(R.id.expense_txt_result);

        recyclerView=myView.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot mySnapshot:snapshot.getChildren()){

                    int totalValue =0;

                    Data data=mySnapshot.getValue(Data.class);

                    assert data != null;
                    totalValue+=data.getAmount();

                    String stTotalValue = String.valueOf(totalValue);

                    expenseTotalSum.setText(stTotalValue);



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, ExpenseFragment.myViewHolder> adapter=new FirebaseRecyclerAdapter<Data, ExpenseFragment.myViewHolder>(
                Data.class,
                R.layout.expense_recycler_data,
                ExpenseFragment.myViewHolder.class,
                mExpenseDatabase

        ) {


            @Override
            protected void populateViewHolder(myViewHolder myViewHolder, Data model, final int i) {
                myViewHolder.setType(model.getType());
                myViewHolder.setNote(model.getNote());
                myViewHolder.setDate(model.getDate());
                myViewHolder.setAmount(model.getAmount());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key=getRef(i).getKey();


                        type=model.getType();
                        note = model.getNote();
                        amount=model.getAmount();

                        updateDataItem();
                    }
                });
            }


        };

        recyclerView.setAdapter(adapter);
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{
        View mView;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_expense);
            String stAmount=String.valueOf(amount);
            mAmount.setText(stAmount);


        }

    }


    private void updateDataItem(){
        AlertDialog.Builder myDialog =new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myView=inflater.inflate(R.layout.update_data_item,null);
        myDialog.setView(myView);


        editAmount=myView.findViewById(R.id.amount_edit);
        editType=myView.findViewById(R.id.type_edit);
        editNote=myView.findViewById(R.id.note_edit);

        //Set Data to edit text

        editType.setText(type);
        editType.setSelection(type.length());

        editNote.setText(note);
        editNote.setSelection(note.length());

        editAmount.setText(String.valueOf(amount));
        editAmount.setSelection(String.valueOf(amount).length());

        btnUpdate=myView.findViewById(R.id.btnUpdate);
        btnDelete=myView.findViewById(R.id.btnDelete);

        AlertDialog dialog = myDialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                type=editType.getText().toString().trim();
                note = editNote.getText().toString().trim();

                String mdAmount=String.valueOf(amount);

                mdAmount=editAmount.getText().toString().trim();

                int myAmount = Integer.parseInt(mdAmount);

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(myAmount,type,note,post_key,mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();


            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();

            }
        });
        dialog.show();

    }





}