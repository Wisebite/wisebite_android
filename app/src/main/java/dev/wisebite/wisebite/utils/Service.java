package dev.wisebite.wisebite.utils;

import java.util.List;

import dev.wisebite.wisebite.firebase.Repository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public abstract class Service<T extends Entity> {

    protected final Repository<T> repository;

    public Service (Repository<T> repository){
        this.repository = repository;
    }

    public T save(T item) {
        if (repository.exists(item.getId())) return repository.update(item);
        return repository.insert(item);
    }

    public T get(String key) {
        return repository.get(key);
    }

    @SuppressWarnings("unused")
    public void delete(String key){
        repository.delete(key);
    }

    @SuppressWarnings("unused")
    public int getAmount(){
        return repository.all().size();
    }

    public List<T> getAll() {
        return repository.all();
    }

    public void setOnChangedListener(Repository.OnChangedListener listener){
        repository.setOnChangedListener(listener);
    }

}
