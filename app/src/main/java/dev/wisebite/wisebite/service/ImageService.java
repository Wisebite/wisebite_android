package dev.wisebite.wisebite.service;

import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class ImageService extends Service<Image> {

    public ImageService(Repository<Image> repository) {
        super(repository);
    }

    @SuppressWarnings("unused")
    public String getImageUrl(String key) {
        Image image = repository.get(key);
        return (image != null ? image.getImageFile() : "");
    }

    public String getDescription(String key) {
        Image image = repository.get(key);
        return (image != null ? image.getDescription() : "");
    }

}
