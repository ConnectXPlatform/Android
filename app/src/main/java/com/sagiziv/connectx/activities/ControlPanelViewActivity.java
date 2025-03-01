package com.sagiziv.connectx.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.sagiziv.connectx.Grid;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.bundlewrappers.ControlPanelBundleWrapper;
import com.sagiziv.connectx.dialogs.ComponentEditDialog;
import com.sagiziv.connectx.dto.ControlPanel;
import com.sagiziv.connectx.dto.CreatePositionedComponent;
import com.sagiziv.connectx.dto.Position;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.dto.Size;
import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.dto.UpdatePositionedComponent;
import com.sagiziv.connectx.fragments.components.ComponentsFactory;
import com.sagiziv.connectx.fragments.components.DraggableComponentFragment;
import com.sagiziv.connectx.repositories.ControlPanelsRepository;
import com.sagiziv.connectx.repositories.PositionedComponentsRepository;
import com.sagiziv.connectx.repositories.TopicsRepository;
import com.sagiziv.connectx.utils.AndroidUtils;
import com.sagiziv.connectx.utils.DialogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ControlPanelViewActivity extends AppCompatActivity {

    private Grid grid;
    private ViewGroup componentsLayout;
    private boolean isInEditMode;
    private PositionedComponentsRepository componentsRepository;
    private ArrayList<DraggableComponentFragment> componentFragments;
    private ControlPanel controlPanel;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel_view);
        ControlPanelBundleWrapper wrapper = new ControlPanelBundleWrapper(getIntent().getExtras());
        controlPanel = wrapper.getControlPanel();

        toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle(controlPanel.getName());
        isInEditMode = wrapper.isInEditMode();
        componentsRepository = new PositionedComponentsRepository(controlPanel);
        componentFragments = new ArrayList<>(controlPanel.getComponents().length);
        componentsLayout = findViewById(R.id.control_panel_LAY_workspace);
        ViewTreeObserver viewTreeObserver = componentsLayout.getViewTreeObserver();
        if (!viewTreeObserver.isAlive()) {
            return;
        }
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                componentsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Rect r = new Rect();
                componentsLayout.getDrawingRect(r);
                grid = new Grid(12, 6, r);

                componentsRepository.load(data -> {
                    runOnUiThread(() -> {
                        TopicsRepository repository = new TopicsRepository("");
                        for (PositionedComponent component : data.values()) {
                            if (component == null) continue;
                            addComponent(component, repository);
                        }
                        initializeToolbar();
                    });
                }, throwable -> {
                    Log.e("pttt", throwable.getMessage(), throwable);
                });
                componentsLayout.setOnDragListener(dragListener);
                backPressedCallback.setEnabled(true);
            }
        });
    }

    private void initializeToolbar() {
        if (isInEditMode) {
            enterEditMode();
        } else {
            exitEditMode();
        }

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                deleteItem();
                return true;
            }
            if (item.getItemId() == R.id.menu_edit) {
                if (!isInEditMode)
                    enterEditMode();
                else {
                    exitEditMode();
                }
                isInEditMode = !isInEditMode;
                return true;
            }
            if (item.getItemId() == R.id.menu_add) {
                showNewComponentDialog();
                return true;
            }
            return false;
        });
        getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }
    OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            for (DraggableComponentFragment<?> componentFragment : componentFragments) {
                componentFragment.close();
            }
            backPressedCallback.setEnabled(false);
            getOnBackPressedDispatcher().onBackPressed();
        }
    };
    private void showNewComponentDialog() {
        ComponentEditDialog componentEditDialog = new ComponentEditDialog();
        AlertDialog dialog = DialogUtils.createDialog(this)
                .withTitle("New component")
                .withPositiveButton(R.string.create_button, i -> {
                    CreatePositionedComponent component = componentEditDialog.create();
                    new PositionedComponentsRepository(controlPanel)
                            .add(component, created -> {
                                runOnUiThread(() -> {
                                    addComponent(created, new TopicsRepository(""));
                                });
                            }, throwable -> {
                                Log.e("pttt", throwable.getMessage());
                            });
                })
                .isCancelable(true)
                .withLayoutContent(componentEditDialog.getLayoutResource())
                .getDialog();
        dialog.show();
        componentEditDialog.initializeViews(dialog);
    }

//    @Override
//    public void onBackPressed() {
//        for (DraggableComponentFragment componentFragment : componentFragments) {
//            componentFragment.close();
//        }
//        super.onBackPressed();
//    }

    private void enterEditMode() {
        MenuItem editMenuItem = toolbar.getMenu().findItem(R.id.menu_edit);
        editMenuItem.setIcon(R.drawable.ic_save);
        editMenuItem.setTitle("Save");
        toolbar.findViewById(R.id.menu_add).setVisibility(View.VISIBLE);
        for (DraggableComponentFragment<?> componentFragment : componentFragments) {
            componentFragment.enterEditMode();
        }
    }

    private void exitEditMode() {
        MenuItem editMenuItem = toolbar.getMenu().findItem(R.id.menu_edit);
        editMenuItem.setIcon(R.drawable.ic_edit);
        editMenuItem.setTitle("Edit");
        toolbar.findViewById(R.id.menu_add).setVisibility(View.GONE);
        toolbar.setTitle(controlPanel.getName());

        Map<String, UpdatePositionedComponent> updates = new HashMap<>(componentFragments.size());
        for (DraggableComponentFragment<?> componentFragment : componentFragments) {
            componentFragment.exitEditMode();
            if (!componentFragment.getComponent().getPosition().equals(componentFragment.getNewPosition())) {
                updates.put(componentFragment.getComponent().getId(), new UpdatePositionedComponent()
                        .setPosition(componentFragment.getNewPosition()));
            }
        }

        updates.forEach((id, update) -> {
            componentsRepository.update(update, id, () -> {
            }, throwable -> {
            });
        });

        // Put the updated control-panel in the result, so the home activity can update accordingly.
        Intent resultIntent = new Intent();
        ControlPanelBundleWrapper wrapper = new ControlPanelBundleWrapper(new Bundle());
        wrapper.setControlPanel(controlPanel);
        resultIntent.putExtras(wrapper.getBundle());
        setResult(RESULT_OK, resultIntent);
    }

    private void deleteItem() {
        DialogUtils.createDialog(this)
                .withTitle(R.string.delete_confirmation_title)
                .withIcon(R.drawable.ic_delete)
                .withTextContent(R.string.delete_confirmation_message)
                .isCancelable(true)
                .withPositiveButton(R.string.confirm_delete, dialog -> {
                    ControlPanelsRepository.getInstance().delete(controlPanel, this::finish, throwable -> {

                    });
                })
                .withNegativeButton(R.string.cancel_delete, Dialog::dismiss)
                .showDialog();
    }

    private Position original;
    View.OnDragListener dragListener = (layout, event) -> {
        DraggableComponentFragment.LocalStateHolder stateHolder = (DraggableComponentFragment.LocalStateHolder) event.getLocalState();
        View draggedComponent = (View) stateHolder.getParent().getParent();
        Position currentPosition = new Position(
                grid.snapXToGrid(event.getX() - draggedComponent.getWidth() / 2.0f),
                grid.snapYToGrid(event.getY() - draggedComponent.getHeight() / 2.0f)
        );
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                original = new Position(draggedComponent.getX(), draggedComponent.getY());
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                // The user existed the layout's bounds, return the view to its position.
                draggedComponent.setX(original.getX());
                draggedComponent.setY(original.getY());
                layout.invalidate();
                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_ENDED:
                layout.invalidate();
                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                draggedComponent.setX(currentPosition.getX());
                draggedComponent.setY(currentPosition.getY());
                return true;
            case DragEvent.ACTION_DROP:
                ClipData.Item item = event.getClipData().getItemAt(0);
                CharSequence dragData = item.getText();

                layout.invalidate();
                draggedComponent.setX(currentPosition.getX());
                draggedComponent.setY(currentPosition.getY());
                stateHolder.getComponentFragment().positionChanged(grid.calculateGridPosition(currentPosition));
//                Log.d("pttt", event.getLocalState().getClass().getSimpleName());
//                Log.d("pttt", event.getLocalState().toString());
//                Log.d("pttt", currentPosition.toString());
//                Log.d("pttt", grid.calculateGridPosition(currentPosition).toString());
                return true;
        }
        return false;
    };

    private void addComponent(@NonNull PositionedComponent component, TopicsRepository repository) {
        Log.d("pttt", "Adding component " + component.getComponentId() + " (topic: " + component.getTopicId() + " )");
        DraggableComponentFragment<?> fragment = ComponentsFactory.getComponentFragmentInstance(component.getComponentId());
        if (fragment == null) {
            Log.e("pttt", "Unknown component id: " + component.getComponentId());
            return;
        }

        repository.get(component.getTopicId(), topic -> {
            runOnUiThread(() -> {
                loadComponent(component, topic, fragment);
            });
        }, throwable -> {
        });
    }

    private void loadComponent(@NonNull PositionedComponent component, TopicInfo topicInfo, DraggableComponentFragment<?> fragment) {
        Position position = component.getPosition();
        Position positionOnLayout = grid.calculateViewPosition(position);

        Size size = component.getSize();
        Size sizeOnLayout = grid.calculateViewSize(size);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(sizeOnLayout.getWidth(), sizeOnLayout.getHeight());
        FrameLayout container = new FrameLayout(this);
        container.setId(View.generateViewId());
        container.setLayoutParams(params);
        container.setX(positionOnLayout.getX());
        container.setY(positionOnLayout.getY());
        getSupportFragmentManager()
                .beginTransaction()
                .add(container.getId(), fragment)
                .runOnCommit(() -> {
                    fragment.loadComponent(component, topicInfo);
                    if (!isInEditMode)
                        fragment.exitEditMode();
                    else
                        fragment.enterEditMode();
                })
                .commit();
        componentsLayout.addView(container);
        componentFragments.add(fragment);
    }
}