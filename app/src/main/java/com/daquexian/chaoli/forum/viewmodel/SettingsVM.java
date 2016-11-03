package com.daquexian.chaoli.forum.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.daquexian.chaoli.forum.R;
import com.daquexian.chaoli.forum.utils.AccountUtils;

import java.io.File;

/**
 * Created by jianhao on 16-10-6.
 */

public class SettingsVM extends BaseViewModel {
    public ObservableBoolean showProcessDialog = new ObservableBoolean();
    public ObservableInt showToast = new ObservableInt();
    public ObservableField<String> toastContent = new ObservableField<>();
    public ObservableInt goToAlbum = new ObservableInt();

    public ObservableField<String> signature = new ObservableField<>();
    public ObservableField<String> userStatus = new ObservableField<>();
    public ObservableBoolean privateAdd = new ObservableBoolean();
    public ObservableBoolean starOnReply = new ObservableBoolean();
    public ObservableBoolean starPrivate = new ObservableBoolean();
    public ObservableBoolean hideOnline = new ObservableBoolean();
    public File avatarFile;

    public SettingsVM(String signature, String userStatus, Boolean privateAdd, Boolean starOnReply, Boolean starPrivate, Boolean hideOnline) {
        this.signature.set(signature);
        this.userStatus.set(userStatus);
        this.privateAdd.set(privateAdd);
        this.starOnReply.set(starOnReply);
        this.starPrivate.set(starPrivate);
        this.hideOnline.set(hideOnline);
    }

    public void save() {
        showProcessDialog.set(true);
        AccountUtils.modifySettings(avatarFile, "Chinese", privateAdd.get(), starOnReply.get(),
                starPrivate.get(), hideOnline.get(), signature.get(), userStatus.get(), new AccountUtils.ModifySettingsObserver() {
                    @Override
                    public void onModifySettingsSuccess() {
                        AccountUtils.getProfile(new AccountUtils.GetProfileObserver() {
                            @Override
                            public void onGetProfileSuccess() {
                                showProcessDialog.set(false);
                                showToast.notifyChange();
                                toastContent.set(getString(R.string.modified_successfully));
                            }

                            @Override
                            public void onGetProfileFailure() {
                                showProcessDialog.set(false);
                                showToast.notifyChange();
                                toastContent.set(getString(R.string.network_err));
                            }
                        });
                    }

                    @Override
                    public void onModifySettingsFailure(int statusCode) {
                        showProcessDialog.set(false);
                        showToast.notifyChange();
                        toastContent.set(getString(R.string.fail_on_modifying));
                    }
                });
    }

    public void clickChangeAvatar() {
        goToAlbum.notifyChange();

    }
}
