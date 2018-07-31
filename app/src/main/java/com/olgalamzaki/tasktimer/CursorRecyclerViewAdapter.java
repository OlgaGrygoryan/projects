package com.olgalamzaki.tasktimer;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by sudomaster on 7/3/18.
 */

class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.TaskViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";
    private Cursor mCursor;
    private OnTaskClickListener mlistener;

    interface OnTaskClickListener {
        void onEditClick(Task task);
        void onDeleteClick (Task task);
        }

    public CursorRecyclerViewAdapter(Cursor cursor, OnTaskClickListener listener) {
        Log.d(TAG, "CursorRecyclerViewAdapter: constructor called");
        mCursor = cursor;
        mlistener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_items, parent, false);
        return new TaskViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: starts");
        if ((mCursor == null) || (mCursor.getCount() == 0)) {
            Log.d(TAG, "onBindViewHolder: providing instructions");
            holder.name.setText(R.string.instructions_heading);
            holder.description.setText(R.string.instructions);
            holder.editButton.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);
        } else {
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move the coursor to the position " + position);
            }

            final Task task = new Task(mCursor.getLong(mCursor.getColumnIndex(TaskContract.Columns._ID)),
                    mCursor.getString(mCursor.getColumnIndex(TaskContract.Columns.TASK_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(TaskContract.Columns.TASK_DESCRIPTION)),
                    mCursor.getInt(mCursor.getColumnIndex(TaskContract.Columns.TASK_SORTORDER)));


            holder.name.setText(mCursor.getString(mCursor.getColumnIndex(TaskContract.Columns.TASK_NAME)));
            holder.description.setText(mCursor.getString(mCursor.getColumnIndex(TaskContract.Columns.TASK_DESCRIPTION)));
            holder.editButton.setVisibility(View.VISIBLE);//TODO add onclick listener
            holder.editButton.setVisibility(View.VISIBLE);//TODO add onclick listener

            View.OnClickListener buttonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d(TAG, "onClick: starts");
                    switch (v.getId()) {
                        case R.id.tli_edit:
                            if (mlistener != null)
                                mlistener.onEditClick(task);
                            break;
                        case R.id.tli_delete:
                            if (mlistener != null)
                                mlistener.onDeleteClick(task);
                            break;
                        default:
                            Log.d(TAG, "onClick: found unexpected button");
                    }
//                    Log.d(TAG, "onClick: button with id " + v.getId() + " clicked");
//                    Log.d(TAG, "onClick: task name is " + task.getName());

                }
            };

            holder.editButton.setOnClickListener(buttonListener);
            holder.deleteButton.setOnClickListener(buttonListener);

        }

    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: starts");
        if ((mCursor == null) || (mCursor.getCount() == 0)){
    return 1;
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Swap in a new cursor, returning the old cursor
     * The returned old cursor is not closed
     *
     * @param newCursor the new cursor to be used
     * @return returns the previously set Cursor or null if there wasn't one
     * If the given new cursor is the same instance as the previously set
     * Cursor, null is also returned.
     */
    Cursor swapCursor(Cursor newCursor){
        if (newCursor == mCursor){
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursor != null){
            notifyDataSetChanged();

        } else {
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;

    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "TaskViewHolder";

        TextView name = null;
        TextView description = null;
        ImageButton editButton = null;
        ImageButton deleteButton = null;

        public TaskViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tli_name);
            this.description = (TextView) itemView.findViewById(R.id.tli_description);
            this.editButton = (ImageButton) itemView.findViewById(R.id.tli_edit);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.tli_delete);
        }
    }
}
