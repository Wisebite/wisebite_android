package dev.wisebite.wisebite.service;

import dev.wisebite.wisebite.domain.OpenTime;
import dev.wisebite.wisebite.firebase.Repository;
import dev.wisebite.wisebite.utils.Service;

/**
 * Created by albert on 16/04/17.
 * @author albert
 */
public class OpenTimeService extends Service<OpenTime> {

    public OpenTimeService(Repository<OpenTime> repository) {
        super(repository);
    }

}
