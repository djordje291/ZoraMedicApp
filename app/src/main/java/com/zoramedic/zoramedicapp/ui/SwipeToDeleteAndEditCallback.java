package com.zoramedic.zoramedicapp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.zoramedic.zoramedicapp.R;
import com.zoramedic.zoramedicapp.data.Note;
import com.zoramedic.zoramedicapp.data.UserMe;
import com.zoramedic.zoramedicapp.view.notes.NotesActivity;
import com.zoramedic.zoramedicapp.view.patients.PatientsActivity;
import com.zoramedic.zoramedicapp.view.patients.service.ServiceActivity;
import com.zoramedic.zoramedicapp.view.pharmacy.PharmacyActivity;

import java.util.List;

public class SwipeToDeleteAndEditCallback extends ItemTouchHelper.Callback {

    Context mContext;
    private Paint mClearPaint;
    private ColorDrawable mBackground;
    private int backgroundColor;
    private Drawable deleteDrawable;
    private int intrinsicWidth;
    private int intrinsicHeight;

    private ItemTouchHelperDelete adapter;
    private ItemTouchHelperEdit activity;

    private List<Note> noteList;

    private Drawable ediDrawableBackground;
    private Drawable editDrawable;
    private int editIntrinsicWidth;
    private int editIntrinsicHeight;

    private Drawable deleteDrawablePharmacy;

    public SwipeToDeleteAndEditCallback(Context context, ItemTouchHelperDelete adapter, ItemTouchHelperEdit activity, List<Note> noteList) {

        mContext = context;

        this.adapter = adapter;
        this.activity = activity;

        this.noteList = noteList;

        mBackground = new ColorDrawable();
        backgroundColor = ContextCompat.getColor(context, R.color.new_red);
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete);
        intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        intrinsicHeight = deleteDrawable.getIntrinsicHeight();

        deleteDrawable.setTint(ContextCompat.getColor(context, R.color.white));


        editDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_edit);
        editDrawable.setTint(Color.WHITE);
        editIntrinsicWidth = editDrawable.getIntrinsicWidth();
        editIntrinsicHeight = editDrawable.getIntrinsicHeight();

        deleteDrawablePharmacy = ContextCompat.getDrawable(mContext, R.drawable.delete_background);
        ediDrawableBackground = ContextCompat.getDrawable(mContext, R.drawable.edit_background);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        if  (activity instanceof NotesActivity) {
            if (noteList != null && !noteList.isEmpty()) {
                Note n = noteList.get(viewHolder.getAdapterPosition());
                if (!n.getCreatorID().equals(UserMe.getInstance().getUserFirebaseID())) {
                    return makeMovementFlags(0, 0);
                } else {
                    return makeMovementFlags(0, ItemTouchHelper.START);
                }
            }
        }
        if (activity == null) {
            return makeMovementFlags(0, ItemTouchHelper.START);
        }
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }
        if ((noteList != null && !noteList.isEmpty()) || activity instanceof PatientsActivity) {

            mBackground.setColor(backgroundColor);
            mBackground.setBounds(itemView.getRight() + (int) dX - 100, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            mBackground.draw(c);

            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            int deleteIconBottom = deleteIconTop + intrinsicHeight;


            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteDrawable.draw(c);

        } else if (activity == null) {
            if (dX < 0) {

                deleteDrawablePharmacy.setBounds(itemView.getRight() + (int) dX - 500, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                deleteDrawablePharmacy.draw(c);


                int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
                int deleteIconRight = itemView.getRight() - deleteIconMargin;
                int deleteIconBottom = deleteIconTop + intrinsicHeight;


                deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                deleteDrawable.draw(c);
            }
        } else {
            if (dX < 0) {

                deleteDrawablePharmacy.setBounds(itemView.getRight() + (int) dX - 500, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                deleteDrawablePharmacy.draw(c);


                int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
                int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
                int deleteIconRight = itemView.getRight() - deleteIconMargin;
                int deleteIconBottom = deleteIconTop + intrinsicHeight;


                deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
                deleteDrawable.draw(c);
            }
            if (dX > 0) {

                ediDrawableBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX + 500, itemView.getBottom());
                ediDrawableBackground.draw(c);

                int editIconTop = itemView.getTop() + (itemHeight - editIntrinsicHeight) / 2;
                int editIconMargin = (itemHeight - editIntrinsicHeight) / 2;
                int editIconLeft = itemView.getLeft() + editIconMargin;
                int editIconRight = itemView.getLeft() + editIconMargin + editIntrinsicWidth;
                int editIconBottom = editIconTop + editIntrinsicHeight;

                editDrawable.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom);
                editDrawable.draw(c);
            }
        }
        if (activity instanceof PatientsActivity) {
            if (dX > 0) {
                ediDrawableBackground = ContextCompat.getDrawable(mContext, R.drawable.edit_background_patient);
                ediDrawableBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX + 100, itemView.getBottom());
                ediDrawableBackground.draw(c);

                int editIconTop = itemView.getTop() + (itemHeight - editIntrinsicHeight) / 2;
                int editIconMargin = (itemHeight - editIntrinsicHeight) / 2;
                int editIconLeft = itemView.getLeft() + editIconMargin;
                int editIconRight = itemView.getLeft() + editIconMargin + editIntrinsicWidth;
                int editIconBottom = editIconTop + editIntrinsicHeight;

                editDrawable.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom);
                editDrawable.draw(c);
            }

        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }


    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        switch (direction) {
            case ItemTouchHelper.START:
                adapter.onItemDelete(viewHolder.getAdapterPosition());
                break;
            case ItemTouchHelper.END:
                activity.onItemEdit(viewHolder.getAdapterPosition());
                break;
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
}
