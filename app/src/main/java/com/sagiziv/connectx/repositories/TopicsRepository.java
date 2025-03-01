package com.sagiziv.connectx.repositories;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.Database;
import com.sagiziv.connectx.dto.CreateTopicDto;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.dto.UpdateTopicDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TopicsRepository extends DataRepository<TopicInfo, CreateTopicDto, UpdateTopicDto> {
    public TopicsRepository(@NonNull DeviceInfo parentDevice) {
        super(new ArrayList<>(parentDevice.getTopics()), parentDevice.getId());
    }

    public TopicsRepository(String parentId) {
        super(new ArrayList<>(0), parentId);
    }

    @Override
    protected void loadImpl(Database.RequestCallback<Map<String, TopicInfo>> requestCallback) {
        Database.getInstance()
                .getTopics(ids(), requestCallback);
    }

    @Override
    protected void addImpl(CreateTopicDto createTopicDto, String deviceId, Database.RequestCallback<TopicInfo> requestCallback) {
        Database.getInstance()
                .createTopic(createTopicDto, deviceId, requestCallback);
    }

    @Override
    protected void getImpl(String itemId, Database.RequestCallback<TopicInfo> requestCallback) {
        Database.getInstance()
                .getTopic(itemId, requestCallback);
    }

    @Override
    protected void updateImpl(UpdateTopicDto updateTopicDto, String itemId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .updateTopic(updateTopicDto, itemId, requestCallback);
    }

    @Override
    protected void deleteImpl(@NonNull String topicId, String deviceId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .deleteTopic(topicId, deviceId, requestCallback);
    }

    @Override
    protected String getId(@NonNull TopicInfo obj) {
        return obj.getId();
    }
}
