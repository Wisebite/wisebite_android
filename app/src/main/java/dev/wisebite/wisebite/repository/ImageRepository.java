package dev.wisebite.wisebite.repository;

import android.content.Context;

import com.firebase.client.DataSnapshot;

import dev.wisebite.wisebite.domain.Image;
import dev.wisebite.wisebite.utils.FirebaseRepository;

/**
 * Created by albert on 13/03/17.
 * @author albert
 */
public class ImageRepository extends FirebaseRepository<Image> {

    public static final String OBJECT_REFERENCE = "image";
    public static final String IMAGE_FILE_REFERENCE = "imageFile";
    public static final String DESCRIPTION_REFERENCE = "description";

    /**
     * Constructor class
     * @param context Repository's context
     */
    public ImageRepository(Context context) {
        super(context);
    }

    @Override
    protected Image convert(DataSnapshot data) {
        Image image = new Image();
        image.setId(data.getKey());
        for (DataSnapshot d : data.getChildren()) {
            if (d.getKey().equals(IMAGE_FILE_REFERENCE)) {
                image.setImageFile(d.getValue(String.class));
            } else if (d.getKey().equals(DESCRIPTION_REFERENCE)) {
                image.setDescription(d.getValue(String.class));
            }
        }
        return image;
    }

    @Override
    public String getObjectReference() {
        return OBJECT_REFERENCE;
    }

}
