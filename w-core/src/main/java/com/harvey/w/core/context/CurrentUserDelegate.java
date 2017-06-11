package com.harvey.w.core.context;

import com.harvey.w.core.model.UserBaseModel;

public interface CurrentUserDelegate {
    <T extends UserBaseModel> T getCurrentUser();
}
