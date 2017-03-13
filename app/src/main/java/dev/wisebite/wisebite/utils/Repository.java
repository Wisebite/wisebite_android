package dev.wisebite.wisebite.utils;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;

import java.util.List;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public abstract class Repository<T extends Entity> implements ChildEventListener {

    private OnChangedListener listener;

    /**
     * Just for the Firebase when needed
     */
    public Repository () {

    }

    public interface OnChangedListener {
        enum EventType {Added, Changed, Removed, Moved, Full}

        void onChanged(EventType type);
    }

    public void setOnChangedListener(OnChangedListener listener) {
        this.listener = listener;
    }

    public abstract T insert(T item);

    public abstract T insertInternal(T item);

    public abstract T update(T item);

    public abstract T updateInternal(T item);

    public abstract void delete(String id);

    public abstract void deleteInternal(String id);

    public abstract boolean exists(String id);

    public abstract T get(String id);

    public abstract List<T> all();

    protected void notifyChange(OnChangedListener.EventType type) {
        if (listener != null){
            listener.onChanged(type);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        insertInternal(convert(dataSnapshot));
        notifyChange(OnChangedListener.EventType.Added);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        updateInternal(convert(dataSnapshot));
        notifyChange(OnChangedListener.EventType.Changed);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        deleteInternal(convert(dataSnapshot).getId());
        notifyChange(OnChangedListener.EventType.Removed);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        updateInternal(convert(dataSnapshot));
        notifyChange(OnChangedListener.EventType.Moved);
    }

    protected abstract T convert(DataSnapshot data);

}
