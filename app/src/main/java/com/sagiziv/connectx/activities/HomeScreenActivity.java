package com.sagiziv.connectx.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagiziv.connectx.FeedbackHandler;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.ZoomOutPageTransformer;
import com.sagiziv.connectx.bundlewrappers.HomeScreenBundleWrapper;
import com.sagiziv.connectx.customviews.SelectableButtonsGroup;
import com.sagiziv.connectx.dataadapters.BaseAdapter;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.User;
import com.sagiziv.connectx.fragments.PromptFragment;
import com.sagiziv.connectx.tabs.ControlPanelsHomeTab;
import com.sagiziv.connectx.tabs.DevicesHomeTab;
import com.sagiziv.connectx.tabs.HomeScreenTab;
import com.sagiziv.connectx.utils.AndroidUtils;
import com.sagiziv.connectx.utils.ColorUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import me.relex.circleindicator.CircleIndicator3;

public class HomeScreenActivity extends FullScreenActivity {
    private ViewPager2 viewPager;
    private CircleIndicator3 indicator;
    private SelectableButtonsGroup buttonsGroup;
    private Toolbar toolbar;
    private Predicate<Integer> deletionPredicate;
    private int currentTab = -1, initialTabIndex;
    private final PromptFragment promptFragment = new PromptFragment();
    private final HomeScreenTab<?>[] tabs = new HomeScreenTab[2];
    private final AtomicInteger numberOfLoadedTabs = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        HomeScreenBundleWrapper wrapper = new HomeScreenBundleWrapper(getIntent().getExtras());
        User user = wrapper.getUser();
        DeviceInfo thisDevice = wrapper.getDeviceInfo();

        findViews();

        buttonsGroup.disableAllButtons();
        buttonsGroup.setButtonChangedCallback(this::onTabSelected);
        // If the button group has a default selected button, use it.
        initialTabIndex = Math.max(buttonsGroup.getSelectedButtonIndex(), 0);

        tabs[0] = new ControlPanelsHomeTab(user.getControlPanels(), this, this::onError, user.getId());
        tabs[1] = new DevicesHomeTab(user.getDevices(), this, this::onError, user.getId(), thisDevice.getId());

        initializeViewPager();
        toolbar.setTitle(user.getName());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_edit) {
                tabs[currentTab].edit(viewPager.getCurrentItem());
                return true;
            }
            if (item.getItemId() == R.id.menu_delete) {
                tabs[currentTab].delete(viewPager.getCurrentItem());
                return true;
            }
            return false;
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDeleteButton(position);
            }
        });
        FloatingActionButton addButton = findViewById(R.id.home_BTN_floating);
        addButton.setOnClickListener(v -> tabs[currentTab].add());
        loadTabs();
    }

    private void findViews() {
        buttonsGroup = findViewById(R.id.home_NAV_bottom);
        indicator = findViewById(R.id.home_VIEW_indicator);
        viewPager = findViewById(R.id.home_LAY_pager);
        toolbar = findViewById(R.id.topAppBar);
    }

    private void onItemAdded(@NonNull BaseAdapter<?> modifiedAdapter, int itemIndex) {
        // Update the indicator
        indicator.setViewPager(viewPager);
        viewPager.setCurrentItem(itemIndex);
        viewPager.requestTransform();
        hidePrompt();
    }

    private void onItemDeleted(@NonNull BaseAdapter<?> modifiedAdapter, int itemIndex) {
        // Update the indicator
        indicator.setViewPager(viewPager);
        // TODO: Fix a bug with the transformer. When an item is deleted, the transformer is stuck in mid-transition.
        if (modifiedAdapter.getItemCount() == 0)
            showPrompt(R.string.no_items_found);
        else if (itemIndex == viewPager.getCurrentItem())
            viewPager.setCurrentItem(itemIndex > 0 ? itemIndex - 1 : itemIndex + 1, false);
    }

    private void onTabSelected(int selectedTabIndex) {
        if (selectedTabIndex == currentTab) return;
        currentTab = selectedTabIndex;
        BaseAdapter<?> adapterForTab = tabs[currentTab].getAdapter();
        viewPager.setAdapter(adapterForTab);
        indicator.setViewPager(viewPager);
        deletionPredicate = tabs[currentTab].getItemDeletionPredicate();
        if (adapterForTab.getItemCount() == 0) {
            showPrompt(R.string.no_items_found);
        } else {
            hidePrompt();
        }
        updateDeleteButton(viewPager.getCurrentItem());
    }

    private void updateDeleteButton(int position) {
        MenuItem deleteMenuItem = toolbar.getMenu().findItem(R.id.menu_delete);
        int color;
        if (!deletionPredicate.test(position)) {
            deleteMenuItem.setOnMenuItemClickListener(v -> {
                FeedbackHandler.getInstance().toast(R.string.cant_delete);
                return true;
            });
            color = ColorUtils.getColor(this, R.color.grey);
        } else {
            deleteMenuItem.setOnMenuItemClickListener(null);
            color = ColorUtils.getColor(this, R.color.white);
        }
        Drawable icon = deleteMenuItem.getIcon();
        if (icon != null) {
            deleteMenuItem.setIcon(AndroidUtils.tintIcon(icon, color));
        }
    }

    private void onTabLoaded() {
        if (numberOfLoadedTabs.incrementAndGet() == tabs.length) {
            runOnUiThread(() -> {
                findViewById(R.id.home_PRG_loading_indicator).setVisibility(View.GONE);
                onTabSelected(initialTabIndex);
                buttonsGroup.enableAllButtons();
                for (HomeScreenTab<?> tab : tabs) {
                    tab.setOnItemAdded(this::onItemAdded);
                    tab.setOnItemDeleted(this::onItemDeleted);
                }
            });
        }
    }

    private void deviceConnected(String deviceId) {
        FeedbackHandler.getInstance()
                .toast("Device " + deviceId + " connected");
    }

    private void deviceDisconnected(String deviceId) {
        FeedbackHandler.getInstance()
                .toast("Device " + deviceId + " disconnected");
    }

    private void onError(Throwable error) {
//        runOnUiThread(() -> {
//            DialogUtils.createDialog(this)
//                    .withTitle("Error")
//                    .withIcon(R.drawable.ic_error)
//                    .withTextContent(error.getLocalizedMessage())
//                    .isCancelable(true)
//                    .withPositiveButton("Retry", retryAction)
//                    .showDialog();
//        });
    }

    private void loadTabs() {
        for (HomeScreenTab<?> tab : tabs) {
            tab.loadData(this::onTabLoaded);
        }
    }

    private void initializeViewPager() {
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }

    private void showPrompt(@StringRes int promptText) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.home_LAY_prompt, promptFragment)
                .runOnCommit(() -> promptFragment.setText(promptText))
                .commit();
    }

    private void hidePrompt() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(promptFragment)
                .commit();
    }
}